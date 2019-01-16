package com.ccms.core.engine;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * @author zc
 * 将某专题下某业务表的表单数据复制为另一个表单
 *
 */
public class CopyForm extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);

		Db db = getDb();
		//通过sequence 获取主键
		String querySeq = getSQL(getResource("query-seq.sql"),inputParams);
		String queryFormItem = getSQL(getResource("query-form_item.sql"),inputParams);

		//重新插入的语句
		String insertForm = getSQL(getResource("insert-form.sql"),inputParams);
		String insertFormShow = getSQL(getResource("insert-form_show.sql"),inputParams);
		String insertFormFilter = getSQL(getResource("insert-form_filter.sql"),inputParams);
		String insertFormGrid = getSQL(getResource("insert-form_grid.sql"),inputParams);
		String insertFormPdf = getSQL(getResource("insert-form_pdf.sql"),inputParams);
		String insertFormExcel = getSQL(getResource("insert-form_excel.sql"),inputParams);
		String insertFormItem = getSQL(getResource("insert-form_item.sql"),inputParams);
		
		String insertFormShowNoItem = getSQL(getResource("insert-form_show_no_item.sql"),inputParams);
		String insertFormFilterNoItem = getSQL(getResource("insert-form_filter_no_item.sql"),inputParams);
		
		//获取主键
		String form_id = getSeq(inputParams,querySeq);
		db.addBatchCommand(StringUtil.replace(insertForm, "${form_id}", form_id));
		
		String grid = StringUtil.replace(insertFormGrid, "${form_id}", form_id);
		db.addBatchCommand(grid);
		
		String pdf = StringUtil.replace(insertFormPdf, "${form_id}", form_id);
		db.addBatchCommand(pdf);
		
		String excel = StringUtil.replace(insertFormExcel, "${form_id}", form_id);
		db.addBatchCommand(excel);
		
		String showNoItem = StringUtil.replace(insertFormShowNoItem, "${form_id}", form_id);
		db.addBatchCommand(showNoItem);
		
		String filterNoItem = StringUtil.replace(insertFormFilterNoItem, "${form_id}", form_id);
		db.addBatchCommand(filterNoItem);
		
		Recordset rsItems = db.get(queryFormItem);
		while(rsItems.next()){
			String old_item_id = rsItems.getString("old_item_id");
			String item_id = rsItems.getString("item_id");
			
			String item = StringUtil.replace(insertFormItem, "${form_id}", form_id);
			item = StringUtil.replace(item, "${old_item_id}", old_item_id);
			item = StringUtil.replace(item, "${item_id}", item_id);
			db.addBatchCommand(item);
			
			String show = StringUtil.replace(insertFormShow, "${form_id}", form_id);
			show = StringUtil.replace(show, "${old_item_id}", old_item_id);
			show = StringUtil.replace(show, "${item_id}", item_id);
			db.addBatchCommand(show);
			
			String filter = StringUtil.replace(insertFormFilter, "${form_id}", form_id);
			filter = StringUtil.replace(filter, "${old_item_id}", old_item_id);
			filter = StringUtil.replace(filter, "${item_id}", item_id);
			db.addBatchCommand(filter);
		}
		
		//最后执行批处理
		db.exec();
		
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
}
