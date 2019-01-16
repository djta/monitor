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
public class CopyReport extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);

		Db db = getDb();
		//通过sequence 获取主键
		String querySeq = getSQL(getResource("query-seq.sql"),inputParams);
		
		String insertReport = getSQL(getResource("insert-report.sql"),inputParams);
		String insertReportShowField = getSQL(getResource("insert-report_show_field.sql"),inputParams);
		String insertReportFilterField = getSQL(getResource("insert-report_filter_field.sql"),inputParams);
		String insertReportChart = getSQL(getResource("insert-report_chart.sql"),inputParams);
		
		//获取主键
		String report_id = getSeq(inputParams,querySeq);
		
		String report = StringUtil.replace(insertReport, "${report_id}", report_id);
		db.addBatchCommand(report);
		
		String reportShowField = StringUtil.replace(insertReportShowField, "${report_id}", report_id);
		db.addBatchCommand(reportShowField);
		
		String reportFilterField = StringUtil.replace(insertReportFilterField, "${report_id}", report_id);
		db.addBatchCommand(reportFilterField);
		
		String reportChart = StringUtil.replace(insertReportChart, "${report_id}", report_id);
		db.addBatchCommand(reportChart);
		
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
