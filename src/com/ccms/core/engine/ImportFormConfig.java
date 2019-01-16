package com.ccms.core.engine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * @author zc
 * 导入表单的配置
 * OS_WFM特殊处理
 *
 */

public class ImportFormConfig extends GenericTableManager {

	@SuppressWarnings("unchecked")
	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		String dbType = getContext().getInitParameter("db");
		
		if (inputParams.isNull("file.filename"))
			throw new Throwable("导入文件不能为空!");
		String file = inputParams.getString("file");
		File f = new File(file);
		//写入指定的文件中 
		byte[] b = new byte[1024*1024];
		int nRead=0;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((nRead = bis.read(b)) > 0) { 
			bos.write(b, 0, nRead);
		}
		//关闭流
		bis.close();
		//删除临时文件
		f.delete();
		//用 dom4j读取xml流
		Document doc=(Document)DocumentHelper.parseText(bos.toString()); 
		bos.close();
		
		//获取根节点，取到 ppk 的值和code
		Element element = doc.getRootElement();
		String ppk = element.attributeValue("ppk");

		String category = element.attributeValue("category");
		if (category==null || category.equals(""))
			throw new Throwable("配置文件业务分类不能为空!");
		
		//存储配置表信息
		String strImportCategory = "import-"+category.toLowerCase();
		dinamica.xml.Document docStruts = getConfig().getDocument();
		
		dinamica.xml.Element[] tables = docStruts.getElements(strImportCategory+"/table");

		if (tables==null||tables.length==0)
			throw new Throwable("配置文件表定义不能为空!");

		Map<String, ImportBean> tableMap = new HashMap<String, ImportBean>();
		for(int i=0;i<tables.length;i++){
			dinamica.xml.Element table = tables[i];
			String table_name = table.getAttribute("name");
			String pkValue = table.getAttribute("pk");
			String ppkValue = table.getAttribute("ppk");
			String ppkType = table.getAttribute("ppk_type");
			String update_mode = table.getAttribute("update_mode");
			String seq = table.getAttribute("seq");
			String delete = table.getAttribute("delete");
			ImportBean bean = new ImportBean(table_name, pkValue, ppkValue, ppkType==null?"integer":ppkType, update_mode, seq != null ?getSQL(seq,null):null, delete);
			tableMap.put(table_name, bean);
		}
		
		DatabaseMetaData md = getConnection().getMetaData();
		
		Db db = getDb();
		//开启事物控制
		db.beginTrans();
		
		Set<String> updateSeqSet = new HashSet<String>();
		Set<String> dropSeqSet = new HashSet<String>();
		
		//遍历xml数据
		Iterator<Element> itE = element.elementIterator();
		while(itE.hasNext()){//遍历表和序列
			Element table = itE.next();
			String tagName = table.getName();
			
			if("table".equalsIgnoreCase(tagName)){
				String table_name = table.attributeValue("name");
				String table_name_idx = table_name;
				//oracle中需要把表名称转化成大写
				if(dbType.equalsIgnoreCase("oracle")){
					table_name_idx = table_name.toUpperCase();
				}
				//获取字段
				ResultSet cols = md.getColumns(null, "%", table_name_idx, "%");
				Recordset rsCol = new Recordset(cols);
				cols.close();
				
				Recordset rsData = new Recordset();
				rsCol.top();
				while(rsCol.next()){
					String column_name = rsCol.getString("column_name").toLowerCase();
					String field_type = rsCol.getString("type_name");
					if("date".equalsIgnoreCase(field_type)){
						rsData.append(column_name, Types.DATE);
					}else if("timestamp".equalsIgnoreCase(field_type) || "datetime".equalsIgnoreCase(field_type)){
						rsData.append(column_name, Types.TIMESTAMP);
					}else if("integer".equalsIgnoreCase(field_type) || "int4".equalsIgnoreCase(field_type) || "int".equalsIgnoreCase(field_type) || "number".equalsIgnoreCase(field_type)){
						rsData.append(column_name, Types.INTEGER);
					}else if("double".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)){
						rsData.append(column_name, Types.DOUBLE);
					}else if("bytea".equalsIgnoreCase(field_type) || "blob".equalsIgnoreCase(field_type) || "image".equalsIgnoreCase(field_type)){
						rsData.append(column_name, Types.VARCHAR);
					}else{//varchar
						rsData.append(column_name, Types.VARCHAR);
					}
				}
				
				//获取当前表的配置
				ImportBean bean = tableMap.get(table_name.toLowerCase());
				//跟据处理模式拼接sql语句（注意：如果mode=1时主键要特殊处理）
				//update_mode 0:判断存在就更新，不存在就原值新增 1:每次都删除后新增
				
				String deleteSql = bean.getDelete();//自定义删除语句
				if(deleteSql != null){
					db.exec(StringUtil.replace(deleteSql, "${ppk}", ("integer".equalsIgnoreCase(bean.getPpk_type())?ppk:"'"+ppk+"'")));
				}else{
					if("1".equals(bean.getUpdate_mode())){
						deleteSql = "delete from "+table_name+" where "+bean.getPpk()+"="+("integer".equalsIgnoreCase(bean.getPpk_type())?ppk:"'"+ppk+"'");
						db.exec(deleteSql);
					}
				}
				//拼insert语句
				StringBuffer sqlField = new StringBuffer();
				StringBuffer sqlValue = new StringBuffer();
				StringBuffer sqlUpdate = new StringBuffer();
				rsCol.top();
				while(rsCol.next()){
					String column_name = rsCol.getString("column_name");
					sqlField.append(column_name).append(",");
					//针对os_wfm表中存在clob类型特殊处理
					if("os_wfm".equalsIgnoreCase(table_name)){
						if(column_name.equalsIgnoreCase(bean.getPk()) && "1".equals(bean.getUpdate_mode()) && bean.getSeq() != null){
							sqlValue.append(bean.getSeq()).append(",");
						}else{
							if(column_name.equalsIgnoreCase("xml_value") || column_name.equalsIgnoreCase("xml_release")){
								sqlValue.append("?,");
							}else{
								sqlValue.append("${fld:").append(column_name.toLowerCase()).append("},");
							}
						}
						
						if(!column_name.equalsIgnoreCase(bean.getPk())){
							if(column_name.equalsIgnoreCase("xml_value") || column_name.equalsIgnoreCase("xml_release")){
								sqlUpdate.append(column_name).append(" = ?,");
							}else{
								sqlUpdate.append(column_name).append(" = ${fld:").append(column_name.toLowerCase()).append("},");
							}
						}
					}else{
						if(column_name.equalsIgnoreCase(bean.getPk()) && "1".equals(bean.getUpdate_mode()) && bean.getSeq() != null){
							sqlValue.append(bean.getSeq()).append(",");
						}else{
							sqlValue.append("${fld:").append(column_name.toLowerCase()).append("},");
						}
						
						if(!column_name.equalsIgnoreCase(bean.getPk())){
							sqlUpdate.append(column_name).append(" = ${fld:").append(column_name.toLowerCase()).append("},");
						}
					}
				}
				StringBuffer insertSql = new StringBuffer();
				insertSql.append("insert into ").append(table_name).append(" (").append(sqlField.substring(0, sqlField.length()-1))
				.append(") values(").append(sqlValue.substring(0, sqlValue.length()-1)).append(")");
				
				String selectSql = null;
				StringBuffer updateSql = new StringBuffer();
				if("0".equals(bean.getUpdate_mode())){
					//查询语句
					selectSql = "select "+bean.getPk()+" from "+table_name+" where "+bean.getPk()+"=${fld:"+bean.getPk()+"}";
					//修改语句
					updateSql.append("update ").append(table_name).append(" set ").append(sqlUpdate.substring(0, sqlUpdate.length()-1))
					.append(" where ").append(bean.getPk()).append(" = ${fld:").append(bean.getPk()).append("}");
				}
				
				Iterator<Element> itRecords = table.elementIterator();
				while(itRecords.hasNext()){
					Element record = itRecords.next();
					Iterator<Element> itCols = record.elementIterator();
					rsData.addNew();
					while(itCols.hasNext()){
						Element column = itCols.next();
						String field_type = column.attributeValue("type");
						String column_name = column.getName();
						String value = column.getTextTrim();
						if(value != null && value.length() > 0){
							if("date".equalsIgnoreCase(field_type)){
								rsData.setValue(column_name, StringUtil.getDateObject(value, "yyyy-MM-dd"));
							}else if("timestamp".equalsIgnoreCase(field_type) || "datetime".equalsIgnoreCase(field_type)){
								rsData.setValue(column_name, StringUtil.getDateObject(value, "yyyy-MM-dd HH:mm:ss"));
							}else if("integer".equalsIgnoreCase(field_type) || "int4".equalsIgnoreCase(field_type) || "int".equalsIgnoreCase(field_type)|| "number".equalsIgnoreCase(field_type)){
								rsData.setValue(column_name, Integer.parseInt(value));
							}else if("double".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)){
								rsData.setValue(column_name, Double.parseDouble(value));
							}else if("bytea".equalsIgnoreCase(field_type) || "blob".equalsIgnoreCase(field_type) || "image".equalsIgnoreCase(field_type)){
								rsData.setValue(column_name, replaceXMLReserve(value));
							}else{//varchar
								rsData.setValue(column_name, replaceXMLReserve(value));
							}
						}else{
							rsData.setValue(column_name, null);
						}
					}
					
					if("1".equals(bean.getUpdate_mode())){
						String insert = getSQL(insertSql.toString(),rsData);
						db.exec(insert);
					}else if("0".equals(bean.getUpdate_mode())){
						String select = getSQL(selectSql,rsData);
						Recordset rs = db.get(select);
						//针对os_wfm表中存在clob类型特殊处理
						if("os_wfm".equalsIgnoreCase(table_name)){
							String str_xml_value = rsData.getString("xml_value");
							String str_xml_release = rsData.getString("xml_release");
							if(rs.next()){//修改
								String update = getSQL(updateSql.toString(),rsData);
								PreparedStatement p = null;
								try
								{
									p = getConnection().prepareStatement(update);
									if(dbType.equalsIgnoreCase("postgresql")){
										p.setString(1, str_xml_value);
										p.setString(2, str_xml_release);
									}else{
										StringReader xml_value = new StringReader(str_xml_value);
										StringReader xml_release = new StringReader(str_xml_release);
										p.setClob(1, xml_value, str_xml_value.length());
										p.setClob(2, xml_release, str_xml_release.length());
									}
									p.execute();
								}
								catch (Throwable e)
								{
									throw e;
								}
								finally
								{
									if (p!=null) p.close();
								}
							}else{//新增
								String insert = getSQL(insertSql.toString(),rsData);
								PreparedStatement p = null;
								try
								{
									p = getConnection().prepareStatement(insert);
									if(dbType.equalsIgnoreCase("postgresql")){
										p.setString(1, str_xml_value);
										p.setString(2, str_xml_release);
									}else{
										StringReader xml_value = new StringReader(str_xml_value);
										StringReader xml_release = new StringReader(str_xml_release);
										p.setClob(1, xml_value, str_xml_value.length());
										p.setClob(2, xml_release, str_xml_release.length());
									}
									p.execute();
								}
								catch (Throwable e)
								{
									throw e;
								}
								finally
								{
									if (p!=null) p.close();
								}
							}
						}else{
							if(rs.next()){//修改
								String update = getSQL(updateSql.toString(),rsData);
								db.exec(update);
							}else{//新增
								String insert = getSQL(insertSql.toString(),rsData);
								db.exec(insert);
							}
						}
					}
				}
			}else if("sequence".equalsIgnoreCase(tagName)){//同步序列
				Iterator<Element> itRecords = table.elementIterator();
				while(itRecords.hasNext()){
					Element record = itRecords.next();
					String seq_name = record.attributeValue("name");
					String seq_value = record.getText();
					if("sqlserver".equalsIgnoreCase(dbType)){
						String updateSeq = "ALTER SEQUENCE dbo."+seq_name+" RESTART WITH "+seq_value+"";
						updateSeqSet.add(updateSeq);
					}else if("oracle".equalsIgnoreCase(dbType)){
						String updateSeq = "DROP SEQUENCE "+seq_name + "" ;
						dropSeqSet.add(updateSeq);
						updateSeq = "CREATE SEQUENCE "+seq_name + " INCREMENT BY 1 START WITH " + seq_value+"" ;
						updateSeqSet.add(updateSeq);
					}else{
						String updateSeq = "{ call setval('"+seq_name+"',"+seq_value+") }";
						updateSeqSet.add(updateSeq);
					}
				}
			}
		}
		
		//一切就绪，事务提交
		db.commit();
		
		//序列单独处理dbType
		Statement stat = getConnection().createStatement();
		Iterator<String> itSeqDrop = dropSeqSet.iterator();
		while(itSeqDrop.hasNext()){
			if("sqlserver".equalsIgnoreCase(dbType)){
			}else if("oracle".equalsIgnoreCase(dbType)){
				stat.executeUpdate(itSeqDrop.next());
			}else{
			}
		}

		Iterator<String> itSeq = updateSeqSet.iterator();
		while(itSeq.hasNext()){
			if("sqlserver".equalsIgnoreCase(dbType)){
				stat.executeUpdate(itSeq.next());
			}else if("oracle".equalsIgnoreCase(dbType)){
				stat.executeUpdate(itSeq.next());
			}else{
				CallableStatement upperProc = getConnection().prepareCall(itSeq.next());
				upperProc.execute();
				upperProc.close();
			}
		}
		stat.close();
		
		return rc;
	}
	
	/**
	 * 反向转义xml数据
	 * @param template
	 * @return
	 */
	public String replaceXMLReserve(String template){
		if(template == null) return "";
		return StringUtil.replace(
					StringUtil.replace(
						StringUtil.replace(
								StringUtil.replace(
										StringUtil.replace(StringUtil.replace(
												StringUtil.replace(
													StringUtil.replace(
															StringUtil.replace(
																	StringUtil.replace(template,  "##k##", " ")//空格
															, "##t##", "\t")//tab
													, "##n##", "\n")//换行
												, "##rn##", "\r\n")//回车换行
											, "##x##", "\\")//反斜线
										, "&gt;", ">")//大括号
								, "&lt;", "<")//小括号
						 , "&quot;", "\"")//双引号
					, "&apos;", "'")//单引号
				,"&amp;", "&");
		 	 
 
	}
	
	class ImportBean{
		private String table_name = null;
		private String pk = null;
		private String ppk = null;
		private String ppk_type = null;
		private String update_mode = null;
		private String seq = null;
		private String delete = null;
		
		public String getDelete() {
			return delete;
		}

		public String getPpk_type() {
			return ppk_type;
		}

		public void setPpk_type(String ppkType) {
			ppk_type = ppkType;
		}

		public void setDelete(String delete) {
			this.delete = delete;
		}

		public ImportBean(String table_name, String pk, String ppk, String ppk_type, String update_mode, String seq, String delete){
			this.table_name = table_name;
			this.pk = pk;
			this.ppk = ppk;
			this.ppk_type = ppk_type;
			this.update_mode = update_mode;
			this.seq = seq;
			this.delete = delete;
		}
		
		public String getSeq() {
			return seq;
		}
		public void setSeq(String seq) {
			this.seq = seq;
		}
		public String getTable_name() {
			return table_name;
		}
		public void setTable_name(String tableName) {
			table_name = tableName;
		}
		public String getPk() {
			return pk;
		}
		public void setPk(String pk) {
			this.pk = pk;
		}
		public String getPpk() {
			return ppk;
		}
		public void setPpk(String ppk) {
			this.ppk = ppk;
		}
		public String getUpdate_mode() {
			return update_mode;
		}
		public void setUpdate_mode(String updateMode) {
			update_mode = updateMode;
		}
	}
}
