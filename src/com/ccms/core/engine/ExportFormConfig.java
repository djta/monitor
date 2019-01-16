package com.ccms.core.engine;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.xml.Document;
import dinamica.xml.Element;

/**
 * @author zc
 * 导出表单的配置
 *
 */
public class ExportFormConfig extends GenericOutput{

	public void print(GenericTransaction data) throws Throwable {
		
		//获取数据库连接
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");
		String dbType = getContext().getInitParameter("db");
		
		if (getConfig().transDataSource!=null)
			dataSourceName = getConfig().transDataSource;
		
		if (jndiPrefix==null)
			jndiPrefix="";
		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);
		
		Connection conn = null;
		try
		{
			if(this.getConnection()==null)
			{
				conn = ds.getConnection();
				this.setConnection(conn);
			}

			Document doc = getConfig().getDocument();
			
			StringBuffer xml = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
			DatabaseMetaData md = conn.getMetaData();
			//记录XML业务类型
			String xmlCategory = doc.getElement("xml-category").getValue();
			xml.append("<config category='").append(xmlCategory).append("' ");
			
			String requestPpk = doc.getElement("request-ppk").getValue();
			String ppk = getRequest().getParameter(requestPpk);//存储当前导出主表编号
			
			xml.append(" ppk='").append(ppk).append("'>");
			
			Element[] tables = doc.getElements("export-tables/table");
			for(int i=0;i<tables.length;i++){
				Element table = tables[i];
				String table_name = table.getAttribute("name");
				String recordsetName = table.getAttribute("recordset");
				Recordset recordset = data.getRecordset(recordsetName);
				//oracle中需要把表名称转化成大写
				if(dbType.equalsIgnoreCase("oracle")){
					table_name = table_name.toUpperCase();
				}
				
				//获取字段
				ResultSet cols = md.getColumns(null, "%", table_name, "%");
				Recordset rsCol = new Recordset(cols);
				cols.close();
				
				xml.append("<table name='").append(table_name).append("'>");
				recordset.top();
				while(recordset.next()){
					xml.append("<record>");
					rsCol.top();
					while(rsCol.next()){
						String column_name = rsCol.getString("column_name");
						String field_type = rsCol.getString("type_name");
						
						xml.append("<").append(column_name.toLowerCase()).append(" type='").append(field_type).append("'>");
						
						Object obj = recordset.getValue(column_name.toLowerCase());
						if(obj != null){
							if("date".equalsIgnoreCase(field_type)){
								xml.append(StringUtil.formatDate((Date)obj, "yyyy-MM-dd"));
							}else if("timestamp".equalsIgnoreCase(field_type) || "datetime".equalsIgnoreCase(field_type)){
								xml.append(StringUtil.formatDate((Date)obj, "yyyy-MM-dd HH:mm:ss"));
							}else if("integer".equalsIgnoreCase(field_type) || "int4".equalsIgnoreCase(field_type) || "int".equalsIgnoreCase(field_type) || "number".equalsIgnoreCase(field_type)){
								xml.append(String.valueOf(obj));
							}else if("double".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)){
								xml.append(String.valueOf(obj));
							}else if("bytea".equalsIgnoreCase(field_type) || "blob".equalsIgnoreCase(field_type)){
								byte[] b = (byte[])obj;
								String v = new String(b);
								xml.append(replaceXML(v));
							}else{//varchar
								xml.append(replaceXML(String.valueOf(obj)));
							}
						}
						xml.append("</").append(column_name.toLowerCase()).append(">");
					}
					xml.append("</record>");
				}
				xml.append("</table>");
				
			}
			

			//获取所有需要同步值的序列，中间逗号隔开
			String sequences = doc.getElement("sequences").getValue();
			String querySeq = getResource("query-seqs.sql");
			//同步序列值
			xml.append("<sequence>");
			if(sequences != null && sequences.length() > 0){
				String[] seqs = sequences.split(",");
				for(String seq : seqs){
					String sql = StringUtil.replace(querySeq, "${seq_name}", seq);
					TemplateEngine tSql = new TemplateEngine(getContext(), getRequest(), sql);
					Recordset rsSeq = getDb().get(tSql.getSql(null));
					rsSeq.first();
					xml.append("<seq name='").append(rsSeq.getString("seq_name")).append("'>").append(rsSeq.getInt("seq_value")).append("</seq>");
				}
			}
			xml.append("</sequence>");
			xml.append("</config>");
			byte[] b = xml.toString().getBytes();
			getResponse().setBufferSize(b.length);
			getResponse().setContentType("text/xml");
			getResponse().setHeader("Content-Disposition", "attachment; filename=\"config_"+requestPpk+"_"+ppk+".xml\"");
			getResponse().setContentLength(b.length);
			ServletOutputStream out = getResponse().getOutputStream();
			out.write(b);
		}catch(Throwable e){
			throw e;
		}finally{
			try
			{
				if(conn != null){
					conn.close();
				}
			}
			catch (Throwable e)
			{
				throw e;
			}	
		}
	}
	
	/**
	 * 转义xml数据
	 * @param template
	 * @return
	 */
	public String replaceXML(String template){
		if(template == null) return "";
		return StringUtil.replace(	
				StringUtil.replace(
						StringUtil.replace(
								StringUtil.replace(
										StringUtil.replace(
												StringUtil.replace(
														StringUtil.replace(
															StringUtil.replace(
																	StringUtil.replace(
																			StringUtil.replace(template, "&", "&amp;")
																	, "'", "&apos;")//单引号
															, "\"", "&quot;")//双引号
														, "<", "&lt;")//小括号
												, ">", "&gt;")//大括号
										, "\\", "##x##")//反斜线
								, "\r\n", "##rn##")//回车换行
						, "\n", "##n##")//换行
					, "\t", "##t##")//tab
				, " ", "##k##");//空格
	}
}
