package com.ccms.util.lunece.core;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Types;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ccms.util.lunece.QueryParser.HQueryParser;
import dinamica.Recordset;
import dinamica.RecordsetException;

/**
 * 2011-7-25 索引
 * 2013-5-29 更新功能
 * 2013-5-30 更新版本
 */
public class IndexManage {

	private static Version matchVersion = Version.LUCENE_43;//Version.LUCENE_35;
	private static Analyzer analyzer = new HAnalyzer(false,false);//new StandardAnalyzer(matchVersion);
	
	public static Analyzer getAnalyzer(){
		return IndexManage.analyzer;
	}
	
	public static Version getVersion(){
		return IndexManage.matchVersion ;
	}
	/**
	 * 创建索引
	 * @param path 路径
	 * @param columns 列名
	 * @param pk_column 主键字段名
	 * @param data 需要创建索引的数据
	 * @throws Throwable 
	 */
	
	public static void createIndex(String path, String[] columns, String pk_column, Recordset data) throws Throwable{
		Directory dir = FSDirectory.open(new File(path));
		IndexWriterConfig iwc = new IndexWriterConfig(matchVersion, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter iw = new IndexWriter(dir, iwc);
		
		data.top();
		while(data.next()){
			Document doc = new Document();
			Field tuidField = new StringField(pk_column, data.getString(pk_column), Field.Store.YES);
			doc.add(tuidField);
			String tmp = "" ;
			doc.add(new StringField("create_date",data.containsField("create_date")&&(tmp=data.getString("create_date"))==null?"":tmp, Field.Store.YES));
			doc.add(new StringField("superior",data.containsField("superior")&&(tmp=data.getString("superior"))==null?"":tmp, Field.Store.YES));
			doc.add(new StringField("is_expired",(tmp=data.getString("is_expired"))==null?"":tmp, Field.Store.YES));
			for(String column : columns){
				doc.add(new TextField(column, (tmp=data.getString(column))==null?"":tmp, Field.Store.YES));
			}
			iw.addDocument(doc);
		}
		iw.close();
		
	}

	/**
	 * 重建索引
	 * @param path 路径
	 * @param columns 列名
	 * @param pk_column 主键字段名
	 * @param data 需要创建索引的数据
	 * @throws Throwable 
	 */
	public static void rebuildIndex(String path, String[] columns, String pk_column, Recordset data) throws Throwable{		
		Directory dir = FSDirectory.open(new File(path));		
		IndexWriterConfig iwc = new IndexWriterConfig(matchVersion, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(dir, iwc);
			
		data.top();
		while(data.next()){
			Document doc = new Document();
			Field tuidField = new StringField(pk_column, data.getString(pk_column), Field.Store.YES);
			doc.add(tuidField);
			String tmp ;
			doc.add(new StringField("create_date",(tmp=data.getString("create_date"))==null?"":tmp, Field.Store.YES));
			doc.add(new StringField("superior",(tmp=data.getString("superior"))==null?"":tmp, Field.Store.YES));
			doc.add(new StringField("is_expired",(tmp=data.getString("is_expired"))==null?"":tmp, Field.Store.YES));
			for(String column : columns){
				doc.add(new TextField(column, (tmp=data.getString(column))==null?"":tmp, Field.Store.YES));
			}
			iw.addDocument(doc);
		}   
		iw.close();
		
	}
	
	/**
	 *  new index.
	 * @param path
	 * @param columns
	 * @param pk_column
	 * @param data
	 * @throws Throwable
	 */
	public static void addIndex(String path,Recordset data)throws Throwable{
		Directory directory = FSDirectory.open(new File(path));
		IndexWriter indexWriter= new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, analyzer));		
		data.top();
		while(data.next()){		
			indexWriter.addDocument(createAndReDoc(data));
		}
		indexWriter.commit();
		indexWriter.close();
	}
	
	/**
	 * update index.
	 * @param path
	 * @param columns
	 * @param pk_column
	 * @param data
	 * @throws Throwable
	 */
	public static void modifyIndex(String path,Recordset data)throws Throwable{
		Directory directory = FSDirectory.open(new File(path));
		IndexWriter indexWriter= new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, analyzer));		
		data.top();
		while(data.next()){	
			Term term = new Term("tuid",data.getString("tuid"));		
			indexWriter.updateDocument(term, createAndReDoc(data));
		}
		indexWriter.commit();
		indexWriter.close();
		
	}
	
	public static Document createAndReDoc(Recordset data)throws Throwable{
		Document doc = new Document();		
		String tmp ;
		Field tuidField = new StringField("tuid", data.getString("tuid"), Field.Store.YES);
		doc.add(tuidField);
		doc.add(new StringField("show_name", (tmp=data.getString("show_name"))==null?"":tmp, Field.Store.YES));	
		doc.add(new StringField("lable", (tmp=data.getString("lable"))==null?"":tmp, Field.Store.YES));
		doc.add(new TextField("content", (tmp=data.getString("content"))==null?"":tmp, Field.Store.YES));
		doc.add(new StringField("create_date",(tmp=data.getString("create_date"))==null?"":tmp, Field.Store.YES));
		doc.add(new StringField("superior",(tmp=data.getString("superior"))==null?"":tmp, Field.Store.YES));
		doc.add(new StringField("is_expired",(tmp=data.getString("is_expired"))==null?"":tmp, Field.Store.YES));	
		return doc ;
		
	}
	
	/**
	 * 根据主键删除索引
	 * @param path 索引路径
	 * @param pk_column 主键字段名
	 * @param pk_value 主键值
	 * @throws Throwable
	 */
	public static void deleteIndex(String path, String pk_column, String pk_value) throws Throwable{
		/*Directory dir = FSDirectory.open(new File(path));
        IndexReader reader = IndexReader.open(dir);//IndexReader.open(dir, false);
        Term term = new Term(pk_column,pk_value);
        reader.deleteDocuments(term);        
        reader.close();*/
		Directory directory = FSDirectory.open(new File(path));
		IndexWriter indexWriter= new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, analyzer));
		Term term = new Term(pk_column,pk_value);
		indexWriter.deleteDocuments(term);
		indexWriter.commit();
		indexWriter.close();	
	     
	}
	/**
	 * hdh 索引明细  高亮显示
	 * @param path
	 * @param columns
	 * @param pk_column
	 * @param pk_value
	 * @param search
	 * @return
	 * @throws Throwable
	 */
	public static Recordset queryIndexDetails(String path ,String[]columns ,String pk_column,String pk_value ,String search)throws Throwable{	
		Directory dir = FSDirectory.open(new File(path));
		IndexReader reader=DirectoryReader.open(dir);  	    
		IndexSearcher is =  new IndexSearcher(reader);		
		String[] fields = columns;
		
		QueryParser qp = new HQueryParser(matchVersion, fields, analyzer);		
		Query query = new TermQuery(new Term(pk_column, pk_value));		
		TopDocs tDocs = is.search(query, reader.maxDoc());
		
		Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlighter\">", "</span>");
		query = qp.parse(search); 
		Scorer fragmentScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
		Fragmenter fragmenter = new SimpleFragmenter(Integer.MAX_VALUE);
		highlighter.setTextFragmenter(fragmenter);
		
		int numTotalHits = tDocs.totalHits;
		Recordset data = null;
		if(numTotalHits > 0){
			data = new Recordset();
			data.append(pk_column, Types.VARCHAR);
			data.append("superior", Types.VARCHAR);
			//data.append("is_expired", Types.VARCHAR);
			//data.append("create_date", Types.VARCHAR);
			for(String column : columns){
				data.append(column, Types.VARCHAR);
			}			
			
			for (int i = 0; i < tDocs.scoreDocs.length; i++) {
				ScoreDoc sdoc = tDocs.scoreDocs[i];
				Document document = is.doc(sdoc.doc);
				data.addNew();
				data.setValue(pk_column, document.get(pk_column)); 
				String column_value ,tmp ;
				for(String column : columns){					
					TokenStream tokenStream = analyzer.tokenStream(column, new StringReader(column_value = document.get(column)));							
					data.setValue(column,(tmp=highlighter.getBestFragment(tokenStream, column_value))!=null?tmp:column_value);					 
				}	
				data.setValue("superior",document.get("superior"));
				//data.setValue("is_expired", document.get("is_expired"));
				//data.setValue("create_date",document.get("create_date"));
			}
		}		
		reader.close();
		return data ;
	}
	
	/**
	 * 查询
	 * 高亮显示style=highlighter
	 * @param path 路径
	 * @param columns 列名
	 * @param pk_column 主键字段名
	 * @param value 查询条件
	 * @return 返回Recordset 结果集
	 * @throws Throwable
	 */
	public static Recordset queryIndex(String path, String[] columns, String pk_column, String value){		
		Directory dir;
		IndexReader reader;
		Recordset data = null;
		try {
			dir = FSDirectory.open(new File(path));
			reader = DirectoryReader.open(dir);
			IndexSearcher is =  new IndexSearcher(reader);		
			String[] fields = columns;

			QueryParser qp = new HQueryParser/*MultiFieldQueryParser*/(matchVersion, fields, analyzer);		
			Query query = qp.parse(value); 		
			TopDocs tDocs = is.search(query, reader.maxDoc());//is.maxDoc());// 一次查询多少个结果
			
			// 准备高亮器
			Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlighter\">", "</span>");
			Scorer fragmentScorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
			Fragmenter fragmenter = new SimpleFragmenter(100);// 高亮范围
			highlighter.setTextFragmenter(fragmenter);
			
			int numTotalHits = tDocs.totalHits;
			IndexUtil iu = new IndexUtil();
			
			if(numTotalHits > 0){
				data = new Recordset();
				data.append(pk_column, Types.VARCHAR);
				data.append("superior", Types.VARCHAR);
				for(String column : columns){
					data.append(column, Types.VARCHAR);
				}
				
				for (int i = 0; i < tDocs.scoreDocs.length; i++) {
					ScoreDoc sdoc = tDocs.scoreDocs[i];
					Document document = is.doc(sdoc.doc);
					data.addNew();
					data.setValue(pk_column, document.get(pk_column)); 
					String temp ,tmp;
					for(String column : columns){
						temp =  iu.getNoHtml(true,document.get(column));
						TokenStream tokenStream = analyzer.tokenStream(column/*"text"*/, new StringReader(temp)); 						
						data.setValue(column, (tmp=highlighter.getBestFragment(tokenStream, temp))!=null?tmp:temp);							
					}
					data.setValue("superior",document.get("superior"));
					 
				}
			}
			reader.close();
		} catch (IOException | ParseException | RecordsetException | InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	 public static class IndexUtil{
			
			public String getContent(String content , int len){
				if (content.length() > len) {
					int index1 = content.indexOf("</", len);
					int index2 = content.indexOf("<", len);
					int index3 = content.indexOf(">", len);
					if (index1 >= 0 && index1 == index2){						
						content = content.substring(0, index3 + 1);						
					} else if (index3 >= 0 && index3 < index2){						
						content = content.substring(0, index3 + 1);						
					} else if (index3 >= 0 && index2 < 0){						
						content = content.substring(0, index3 + 1);						
					} else {						
						content = content.substring(0, len);
					}
				}
				return content;
			}
			
			public String getNoHtml(boolean isUse , String content) {
				if (!isUse)
					return content;
				content = this.Html2Text(content);
				//content = StringUtil.replaceEx(content, "<", "&lt;");
				//content = StringUtil.replaceEx(content, ">", "&gt;");
				return content;	
			}
			
			public String Html2Text(String inputString) {
				String htmlStr = inputString;
				String textStr = "";
				java.util.regex.Pattern p_script;
				java.util.regex.Matcher m_script;
				java.util.regex.Pattern p_style;
				java.util.regex.Matcher m_style;
				java.util.regex.Pattern p_html;
				java.util.regex.Matcher m_html;
				java.util.regex.Pattern p_html1;
				java.util.regex.Matcher m_html1;

				try {
					// 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
					String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
					// 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
					String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
					// 定义HTML标签的正则表达式
					String regEx_html = "<[^>]+>";
					String regEx_html1 = "<[^>]+";
					// 过滤script标签
					p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
					m_script = p_script.matcher(htmlStr);
					htmlStr = m_script.replaceAll("");
					// 过滤style标签
					p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
					m_style = p_style.matcher(htmlStr);
					htmlStr = m_style.replaceAll("");
					// 过滤html标签
					p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
					m_html = p_html.matcher(htmlStr);
					htmlStr = m_html.replaceAll("");
					// 过滤html标签
					p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
					m_html1 = p_html1.matcher(htmlStr);
					htmlStr = m_html1.replaceAll("");

					textStr = htmlStr;

				} catch (Exception e) {
					System.err.println("Html2Text: " + e.getMessage());
				}

				return textStr;// 返回文本字符串
			}
		}
}
