package com.ccms.util.lunece.recovery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;
import java.util.List;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import dinamica.Recordset;

import com.ccms.util.lunece.core.IndexManage;
/**
 * 
 * 搜索纠正
 * @author hdh
 * 2013-06-07
 */
public class RecoveryError {	
	
	/**
	 * 获取建议关键字
	 * @param path
	 * @param search
	 * @return
	 * @throws Throwable
	 */
	public static Recordset iuRecovery(String indexPath,String dicPath,String search) throws Throwable {				
		Directory directory = FSDirectory.open(new File(indexPath));				 
 	   	SpellChecker sp = new SpellChecker(directory);	   	
 	   	try{
 	   		sp.indexDictionary(new PlainTextDictionary(new File(dicPath)),new IndexWriterConfig(IndexManage.getVersion(), IndexManage.getAnalyzer()),false);
 	   		//sp.indexDictionary(new FileDictionary(new FileInputStream(new File(path))), new IndexWriterConfig(IndexManage.getVersion(), IndexManage.getAnalyzer()), false);
 	   	}catch(Exception e){
 			e.printStackTrace();
 	   	}
 	   
 	   	int suggestionNumber = 2;
 	    
 	   	String[] sugges = sp.suggestSimilar(search, suggestionNumber);
 	   
 	   	String column = "content";
 	   	Recordset data  = new Recordset();	
 	   	data.append(column, Types.VARCHAR);
 	   	if(sugges!=null && sugges.length != 0){	
 	   		data.addNew();
 	   		StringBuilder su = new StringBuilder(sugges[0]);
			for (int i = 1; i < sugges.length; i++) {
				 su.append(",").append(sugges[i]);				 
			}
			data.setValue(column,su.toString());
 	   	}
 	   	sp.close();
 	   	directory.close();
 	   	return data;
	}
	
	/**
	 * delete index
	 * @param appPath
	 */
	public static void iuRemoveIndexFile(String appPath) throws Throwable {
		File file = new File(appPath);
		if(file.exists()){
			File []files = file.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		File parentFile = file.getParentFile();
		if(parentFile.isDirectory()){
			/*boolean isDelete =*/ parentFile.delete();
		}
	}
	
	/**
	 * create index
	 * @param appPath
	 * @throws IOException
	 */
	public static void iuWriteIndexFile(String appPath) throws Throwable {
		
		File file = new File(appPath + File.separatorChar  );
		File parentFile = file.getParentFile();
		if(!parentFile.exists() && !parentFile.isDirectory()){
			parentFile.mkdir();
		}
		if(!file.exists())
			file.createNewFile();
		List<String> dicts = null;
		FileWriter writer = new FileWriter(file);
		if(dicts != null){
			for (String dict : dicts) {
				writer.write(dict);
				writer.write("\n");
			}
		}
		writer.close();
	}
	
	
}
