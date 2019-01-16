package com.ccms.term;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.IServiceWrapper;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SaveTermScore extends GenericTableManager implements IServiceWrapper {

	@Override
	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);
		//先校验文档数据

		Db db = getDb();

		String types = inputs.getString("types");
		String items = inputs.getString("items");
		String list_scores = inputs.getString("list_scores");
		String matrix_scores = inputs.getString("matrix_scores");
		
		if(types != null && !"".equals(types) && items != null && !"".equals(items)){
			Set<TermType> typeSet = new HashSet<TermType>();
			Set<TermItem> itemSet = new HashSet<TermItem>();
			Set<TermList> listSet = new HashSet<TermList>();
			Set<TermMatrixItem> matrixItemSet = new HashSet<TermMatrixItem>();
			
			//处理 选项
			if(list_scores != null && !"".equals(list_scores)){
				String[] listArray = list_scores.split(";");
				for(int i=0;i<listArray.length;i++){
					String[] listValue = listArray[i].split(":");
					if(listValue.length > 2){
						TermList list = new TermList();
						list.setItem_id(listValue[0]);
						list.setList_id(listValue[1]);
						list.setList_score(listValue[2]);
						if(listValue.length > 3){
							list.setList_text(listValue[3]);
						}
						if(listValue.length > 4){
							list.setList_dropdown_value(listValue[4]);
						}
						listSet.add(list);
					}
				}
			}
			//处理矩阵选项
			if(matrix_scores != null && !"".equals(matrix_scores)){
				String[] matrixArray = matrix_scores.split(";");
				for(int i=0;i<matrixArray.length;i++){
					String[] matrixValue = matrixArray[i].split(":");
					if(matrixValue.length > 3){
						TermMatrixItem matrix = new TermMatrixItem();
						matrix.setItem_id(matrixValue[0]);
						matrix.setList_id(matrixValue[1]);
						matrix.setMatrix_item_id(matrixValue[2]);
						matrix.setMatrix_score(matrixValue[3]);
						if(matrixValue.length > 4){
							matrix.setMatrix_text(matrixValue[4]);
						}
						matrixItemSet.add(matrix);
					}
				}
			}
			//处理话术题
			String[] itemArray = items.split(";");
			for(int i=0;i<itemArray.length;i++){
				String[] itemValue = itemArray[i].split(":");
				if(itemValue.length > 2){
					TermItem item = new TermItem();
					item.setType_id(itemValue[0]);
					item.setItem_id(itemValue[1]);
					item.setItem_score(itemValue[2]);
					Iterator<TermList> listIt = listSet.iterator();
					while(listIt.hasNext()){
						TermList listObj = listIt.next();
						if(listObj.getItem_id().equals(itemValue[1])){
							item.getLists().add(listObj);
						}
					}
					Iterator<TermMatrixItem> matrixIt = matrixItemSet.iterator();
					while(matrixIt.hasNext()){
						TermMatrixItem matrixObj = matrixIt.next();
						if(matrixObj.getItem_id().equals(itemValue[1])){
							item.getMatrixItems().add(matrixObj);
						}
					}
					itemSet.add(item);
				}
			}
			//处理话术类型
			String[] typeArray = types.split(";");
			for(int i=0;i<typeArray.length;i++){
				String[] typeValue = typeArray[i].split(":");
				if(typeValue.length > 1){
					TermType type = new TermType();
					type.setType_id(typeValue[0]);
					type.setType_score(typeValue[1]);
					Iterator<TermItem> itemIt = itemSet.iterator();
					while(itemIt.hasNext()){
						TermItem itemObj = itemIt.next();
						if(itemObj.getType_id().equals(typeValue[0])){
							type.getItems().add(itemObj);
						}
					}
					typeSet.add(type);
				}
			}
			
			//Db db = getDb();
			List<String> sqls = new ArrayList<String>();//保存sql执行语句，批处理执行
			
			//拼sql语句，通过sequence 获取主键
			String querySeq = getSQL(getResource("query-seq.sql"),inputs);
			String querySeqOther = getSQL(getResource("query-seq-other.sql"),inputs);
			String insertTerm = getSQL(getResource("insert-term.sql"),inputs);
			String insertType = getSQL(getResource("insert-type.sql"),inputs);
			String insertItem = getSQL(getResource("insert-item.sql"),inputs);
			String insertList = getSQL(getResource("insert-list.sql"),inputs);
			String insertMatrix = getSQL(getResource("insert-matrix.sql"),inputs);
			
			String seq_term = getSeq(inputs,querySeq);
			insertTerm = StringUtil.replace(insertTerm, "${tuid}", seq_term);
			sqls.add(insertTerm);
			
			//将term_score_tuid放到inputs中
			inputs.setValue("term_score_tuid", seq_term);
			
			Iterator<TermType> typeIt = typeSet.iterator();
			while(typeIt.hasNext()){
				String seq_type = getSeq(inputs,querySeqOther);
				TermType type = typeIt.next();
				String sqlType = StringUtil.replace(insertType, "${tuid}", seq_type);
				sqlType = StringUtil.replace(sqlType, "${score_term_id}", seq_term);
				sqlType = StringUtil.replace(sqlType, "${type_id}", type.getType_id());
				sqlType = StringUtil.replace(sqlType, "${type_score}", type.getType_score());
				sqls.add(sqlType);
				
				Iterator<TermItem> itemIt = type.getItems().iterator();
				while(itemIt.hasNext()){
					String seq_item = getSeq(inputs,querySeqOther);
					TermItem item = itemIt.next();
					String sqlItem = StringUtil.replace(insertItem, "${tuid}", seq_item);
					sqlItem = StringUtil.replace(sqlItem, "${score_type_id}", seq_type);
					sqlItem = StringUtil.replace(sqlItem, "${item_id}", item.getItem_id());
					sqlItem = StringUtil.replace(sqlItem, "${item_score}", item.getItem_score());
					sqls.add(sqlItem);
					
					Iterator<TermList> listIt = item.getLists().iterator();
					while(listIt.hasNext()){
						TermList list = listIt.next();
						String sqlList = StringUtil.replace(insertList, "${score_item_id}", seq_item);
						sqlList = StringUtil.replace(sqlList, "${list_id}", list.getList_id());
						sqlList = StringUtil.replace(sqlList, "${list_score}", list.getList_score());
						sqlList = StringUtil.replace(sqlList, "${list_text}", list.getList_text());
						sqlList = StringUtil.replace(sqlList, "${list_dropdown_value}", list.getList_dropdown_value());
						sqls.add(sqlList);
					}
					
					Iterator<TermMatrixItem> matrixIt = item.getMatrixItems().iterator();
					while(matrixIt.hasNext()){
						TermMatrixItem matrix = matrixIt.next();
						String sqlMatrix = StringUtil.replace(insertMatrix, "${score_item_id}", seq_item);
						sqlMatrix = StringUtil.replace(sqlMatrix, "${matrix_item_id}", matrix.getMatrix_item_id());
						sqlMatrix = StringUtil.replace(sqlMatrix, "${list_id}", matrix.getList_id());
						sqlMatrix = StringUtil.replace(sqlMatrix, "${matrix_score}", matrix.getMatrix_score());
						sqlMatrix = StringUtil.replace(sqlMatrix, "${matrix_text}", matrix.getMatrix_text());
						sqls.add(sqlMatrix);
					}
				}
			}
			//最后批处理执行sql语句
			for(int i=0;i<sqls.size();i++){
				db.addBatchCommand(sqls.get(i));
			}
			db.exec();
		}
		return rc;
	}
	
	/**
	 * 返回一个序列
	 * @param inputParams
	 * @return
	 * @throws Throwable
	 */
	public String getSeq(Recordset inputParams,String sql) throws Throwable{
		String seq = "0";
		Db db = getDb();
		Recordset seqRs = db.get(sql);
		if(seqRs.next()){
			seq = seqRs.getString("seq");
		}
		return seq;
	}

	@Override
	public void afterService(Recordset inputs) throws Throwable {
		if(!inputs.containsField("post_class")){
			return;
		}
		String strClassName = inputs.getString("post_class");
		if (strClassName == null || strClassName.equalsIgnoreCase(""))
			return;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		GenericTransaction t = (GenericTransaction) loader.loadClass(strClassName).newInstance();
					
		t.init(this.getContext(), this.getRequest(), this.getResponse());
		t.setConfig(this.getConfig());
		t.setConnection(this.getConnection());
		t.service(inputs);
	}
	
	@Override
	public void beforeService(Recordset inputs) throws Throwable {
		if(!inputs.containsField("pre_class")){
			return;
		}
		String strClassName = inputs.getString("pre_class");
		if (strClassName == null || strClassName.equalsIgnoreCase(""))
			return;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		GenericTransaction t = (GenericTransaction) loader.loadClass(strClassName).newInstance();
					
		t.init(this.getContext(), this.getRequest(), this.getResponse());
		t.setConfig(this.getConfig());
		t.setConnection(this.getConnection());
		t.service(inputs);
	}

	/**
	 * Crea un string con la lista de valores de un recordset separados por
	 * coma.
	 * 
	 * @param rs
	 * @return String
	 * @throws Throwable
	 */
	String buildArrayRecordsetInsert(Recordset rs) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			String is_virtual_type = rs.getString("is_virtual_type");
			// asignar nombre del campo junto con la coma
			if ("0".equals(is_virtual_type)){
				// asignar nombre del campo junto con la coma
				field = field + rs.getString("field_code") + ",";
			}
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}
	String buildArrayRecordsetUpdate(Recordset rs) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			String is_virtual_type = rs.getString("is_virtual_type");
			if ("0".equals(is_virtual_type)){
				// asignar nombre del campo junto con la coma
				field = field + rs.getString("field_code") + "=" + rs.getString("colname_mark") + ",";
			}
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}
	String buildArrayRecordsetMarkInsert(Recordset rs) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			String is_virtual_type = rs.getString("is_virtual_type");
			// asignar nombre del campo junto con la coma
			if ("0".equals(is_virtual_type)){
				field = field + rs.getString("colname_mark") + ",";
			}
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

	String getTableFieldDiffValue(String field_code, String sql) throws Throwable {
		if (getRequest().getParameterValues("_" + field_code + "_") == null)
			return "";
		if (getRequest().getParameterValues(field_code) == null)
			return "";

		String strBeforeValue = getRequest().getParameterValues("_" + field_code + "_")[0].trim();
		strBeforeValue = StringUtil.replace(strBeforeValue, "'", "''");
		String strAfterValue = getRequest().getParameterValues(field_code)[0].trim();
		strAfterValue = StringUtil.replace(strAfterValue, "'", "''");
		String sql_return = sql;

		if (!strAfterValue.equals(strBeforeValue)) {
			sql_return = StringUtil.replace(sql_return, "${field_code}", field_code);
			//最多支持512长度
		    sql_return = StringUtil.replace(sql_return, "${before_value}", strBeforeValue.substring(strBeforeValue.length()>4000?strBeforeValue.length()-4000:0));
		    sql_return = StringUtil.replace(sql_return, "${after_value}", strAfterValue.substring(strAfterValue.length()>4000?strAfterValue.length()-4000:0));
		} else {
			sql_return = "";
		}
		return sql_return;
	}

}
