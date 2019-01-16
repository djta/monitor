package com.ccms.core.engine;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * @author zc
 * 复制专题
 *
 */
public class CopySubject extends GenericTableManager {

public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);

		Db db = getDb();
		
		String queryTables = getSQL(getResource("query-table.sql"),inputParams);
		String queryFields = getSQL(getResource("query-field.sql"),inputParams);
		String queryForms = getSQL(getResource("query-form.sql"),inputParams);
		String queryReport = getSQL(getResource("query-report.sql"),inputParams);
		String queryFormItem = getSQL(getResource("query-form_item.sql"),inputParams);
		
		//重新插入的语句
		String insertSubject = getSQL(getResource("insert-subject.sql"),inputParams);
		String insertTable = getSQL(getResource("insert-table.sql"),inputParams);
		String insertForm = getSQL(getResource("insert-form.sql"),inputParams);
		String insertField = getSQL(getResource("insert-field.sql"),inputParams);
		String insertFormShow = getSQL(getResource("insert-form_show.sql"),inputParams);
		String insertFormFilter = getSQL(getResource("insert-form_filter.sql"),inputParams);
		String insertFormGrid = getSQL(getResource("insert-form_grid.sql"),inputParams);
		String insertFormPdf = getSQL(getResource("insert-form_pdf.sql"),inputParams);
		String insertFormExcel = getSQL(getResource("insert-form_excel.sql"),inputParams);
		String insertFormItem = getSQL(getResource("insert-form_item.sql"),inputParams);
		
		String insertFormShowNoItem = getSQL(getResource("insert-form_show_no_item.sql"),inputParams);
		String insertFormFilterNoItem = getSQL(getResource("insert-form_filter_no_item.sql"),inputParams);
		
		String insertReport = getSQL(getResource("insert-report.sql"),inputParams);
		String insertReportShowField = getSQL(getResource("insert-report_show_field.sql"),inputParams);
		String insertReportFilterField = getSQL(getResource("insert-report_filter_field.sql"),inputParams);
		String insertReportChart = getSQL(getResource("insert-report_chart.sql"),inputParams);
		
		Recordset rsTables = db.get(queryTables);
		
		//插入subject skill
		db.addBatchCommand(insertSubject);
		
		while(rsTables.next()){
			String old_table_id = rsTables.getString("old_table_id");
			String table_id = rsTables.getString("table_id");
			
			Recordset rsFields = db.get(StringUtil.replace(queryFields, "${old_table_id}", old_table_id));
			Recordset rsForms = db.get(StringUtil.replace(queryForms, "${old_table_id}", old_table_id));
			
			//插入table
			String table = StringUtil.replace(insertTable, "${table_id}", table_id);
			table = StringUtil.replace(table, "${old_table_id}", old_table_id);
			db.addBatchCommand(table);
			
			//插入Field
			while(rsFields.next()){
				String old_field_id = rsFields.getString("old_field_id");
				String field_id = rsFields.getString("field_id");
				
				String insert = StringUtil.replace(insertField, "${field_id}", field_id);
				insert = StringUtil.replace(insert, "${old_field_id}", old_field_id);
				insert = StringUtil.replace(insert, "${table_id}", table_id);
				db.addBatchCommand(insert);
			}
			
			//插入form
			while(rsForms.next()){
				String old_form_id = rsForms.getString("old_form_id");
				String form_id = rsForms.getString("form_id");
				
				String insert = StringUtil.replace(insertForm, "${form_id}", form_id);
				insert = StringUtil.replace(insert, "${old_form_id}", old_form_id);
				insert = StringUtil.replace(insert, "${table_id}", table_id);
				db.addBatchCommand(insert);
				
				rsFields.top();
				while(rsFields.next()){
					String old_field_id = rsFields.getString("old_field_id");
					String field_id = rsFields.getString("field_id");
					
					String show = StringUtil.replace(insertFormShowNoItem, "${form_id}", form_id);
					show = StringUtil.replace(show, "${old_form_id}", old_form_id);
					show = StringUtil.replace(show, "${field_id}", field_id);
					show = StringUtil.replace(show, "${old_field_id}", old_field_id);
					db.addBatchCommand(show);
					
					String filter = StringUtil.replace(insertFormFilterNoItem, "${form_id}", form_id);
					filter = StringUtil.replace(filter, "${old_form_id}", old_form_id);
					filter = StringUtil.replace(filter, "${field_id}", field_id);
					filter = StringUtil.replace(filter, "${old_field_id}", old_field_id);
					db.addBatchCommand(filter);
					
					String grid = StringUtil.replace(insertFormGrid, "${form_id}", form_id);
					grid = StringUtil.replace(grid, "${old_form_id}", old_form_id);
					grid = StringUtil.replace(grid, "${field_id}", field_id);
					grid = StringUtil.replace(grid, "${old_field_id}", old_field_id);
					db.addBatchCommand(grid);
					
					String pdf = StringUtil.replace(insertFormPdf, "${form_id}", form_id);
					pdf = StringUtil.replace(pdf, "${old_form_id}", old_form_id);
					pdf = StringUtil.replace(pdf, "${field_id}", field_id);
					pdf = StringUtil.replace(pdf, "${old_field_id}", old_field_id);
					db.addBatchCommand(pdf);
					
					String excel = StringUtil.replace(insertFormExcel, "${form_id}", form_id);
					excel = StringUtil.replace(excel, "${old_form_id}", old_form_id);
					excel = StringUtil.replace(excel, "${field_id}", field_id);
					excel = StringUtil.replace(excel, "${old_field_id}", old_field_id);
					db.addBatchCommand(excel);
				}
				String queryItem = StringUtil.replace(queryFormItem, "${form_id}", form_id);
				Recordset rsItems = db.get(queryItem);
				while(rsItems.next()){
					String old_item_id = rsItems.getString("old_item_id");
					String item_id = rsItems.getString("item_id");
					
					String item = StringUtil.replace(insertFormItem, "${form_id}", form_id);
					item = StringUtil.replace(item, "${old_form_id}", old_form_id);
					item = StringUtil.replace(item, "${old_item_id}", old_item_id);
					item = StringUtil.replace(item, "${item_id}", item_id);
					db.addBatchCommand(item);
					
					rsFields.top();
					while(rsFields.next()){
						String old_field_id = rsFields.getString("old_field_id");
						String field_id = rsFields.getString("field_id");
						
						String show = StringUtil.replace(insertFormShow, "${form_id}", form_id);
						show = StringUtil.replace(show, "${old_form_id}", old_form_id);
						show = StringUtil.replace(show, "${field_id}", field_id);
						show = StringUtil.replace(show, "${old_field_id}", old_field_id);
						show = StringUtil.replace(show, "${item_id}", item_id);
						show = StringUtil.replace(show, "${old_item_id}", old_item_id);
						db.addBatchCommand(show);
						
						String filter = StringUtil.replace(insertFormFilter, "${form_id}", form_id);
						filter = StringUtil.replace(filter, "${old_form_id}", old_form_id);
						filter = StringUtil.replace(filter, "${field_id}", field_id);
						filter = StringUtil.replace(filter, "${old_field_id}", old_field_id);
						filter = StringUtil.replace(filter, "${item_id}", item_id);
						filter = StringUtil.replace(filter, "${old_item_id}", old_item_id);
						db.addBatchCommand(filter);
					}
				}
			}
			
		}
		
		//插入报表
		
		Recordset rsReport = db.get(queryReport);
		
		while(rsReport.next()){
			String old_report_id = rsReport.getString("old_report_id");
			String report_id = rsReport.getString("report_id");
			
			String report = StringUtil.replace(insertReport, "${report_id}", report_id);
			report = StringUtil.replace(report, "${old_report_id}", old_report_id);
			db.addBatchCommand(report);
			
			String reportChart = StringUtil.replace(insertReportChart, "${report_id}", report_id);
			reportChart = StringUtil.replace(reportChart, "${old_report_id}", old_report_id);
			db.addBatchCommand(reportChart);

			String reportShowField = StringUtil.replace(insertReportShowField, "${report_id}", report_id);
			reportShowField = StringUtil.replace(reportShowField, "${old_report_id}", old_report_id);
			db.addBatchCommand(reportShowField);
			
			String reportFilterField = StringUtil.replace(insertReportFilterField, "${report_id}", report_id);
			reportFilterField = StringUtil.replace(reportFilterField, "${old_report_id}", old_report_id);
			db.addBatchCommand(reportFilterField);
			
		}
		
		//最后执行批处理
		db.exec();
		
		return rc;
	}
}
