package com.ccms.report2;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.ccms.report2.lucaslee.report.NullFormatter;
import com.ccms.report2.lucaslee.report.ReportManager;
import com.ccms.report2.lucaslee.report.grouparithmetic.AverageArithmetic;
import com.ccms.report2.lucaslee.report.grouparithmetic.CountArithmetic;
import com.ccms.report2.lucaslee.report.grouparithmetic.MaxArithmetic;
import com.ccms.report2.lucaslee.report.grouparithmetic.MinArithmetic;
import com.ccms.report2.lucaslee.report.grouparithmetic.SumArithmetic;
import com.ccms.report2.lucaslee.report.model.HeaderTable;
import com.ccms.report2.lucaslee.report.model.Report;
import com.ccms.report2.lucaslee.report.model.ReportBody;
import com.ccms.report2.lucaslee.report.model.Table;
import com.ccms.report2.lucaslee.report.model.TableCell;
import com.ccms.report2.lucaslee.report.model.TableRow;
import com.ccms.report2.lucaslee.report.model.crosstable.CrossCol;
import com.ccms.report2.lucaslee.report.model.crosstable.CrossTable;
import com.ccms.report2.lucaslee.report.model.crosstable.HeadCol;
import com.ccms.report2.lucaslee.report.printer.HTMLCss;
import com.ccms.report2.lucaslee.report.printer.HTMLPrinter;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.TemplateEngine;

public class ReportOutput extends GenericOutput{	
	@Override
	public void print(TemplateEngine te, GenericTransaction data) throws Throwable{
		super.print(te, data);

        //取得报表类型
		Recordset rsReport = data.getRecordset("query_report.sql");
		
		//取得报表样式
		Recordset rsReportCss = data.getRecordset("query_css.sql");

        //取得报表类型
        rsReport.first();
        String strReportType = rsReport.getString("report_type");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String strReportHtml = "";
        if(strReportType.equals("2")){  /*交叉报表*/
            getHTMLReport(getCrossReport(data), baos, rsReportCss,strReportType);
        }else if(strReportType.equals("1")){
            getHTMLReport(getGroupReport(data), baos, rsReportCss,strReportType);
        }else if(strReportType.equals("0")){
            getHTMLReport(getFlatReport(data), baos, rsReportCss,strReportType);
        }
        
        strReportHtml = new String(baos.toByteArray(), "GBK");		
		te.replace("${html}", strReportHtml);

	}

	/**
	 * 根据报表对象生成HTML格式的报表.
	 * 
	 * @param report
	 *            报表对象
	 * @throws Exception
	 */
	public static void getHTMLReport(Report report, OutputStream os, Recordset reportCss,String strReportType) throws Throwable {
		/**
		 * Author: 马志礼
		 * Date: 2017-03-18
		 * Content: 加载CSS的Style样式表,该样式在query_css.sql中写入,如需更改样式，则可在该SQL中进行更改
		 */
		/* -------------------------Start:设置HTML报表的样式表---------------------------------- */
		reportCss.first();
		HTMLCss css = new HTMLCss();
		css.setGroupTotal(reportCss.getString("report_grouptotal"));
		css.setHead(reportCss.getString("report_head"));
		css.setTotal(reportCss.getString("report_total"));
		css.setTitle(reportCss.getString("report_title"));
		if(strReportType.equals("2")){
			css.setData(reportCss.getString("cross_report_data"));
		}else{
			css.setData(reportCss.getString("report_data"));
		}
		css.setCrossHeadHead(reportCss.getString("report_crossheadhead"));
		/* -------------------------End:设置HTML报表的样式表---------------------------------- */
		/*执行HTML格式报表的输出*/
		new HTMLPrinter().print(report, css, os);
	}

	/**
	 * 从xml文件获得原始数据表格.注意其中使用的crossTabSample.xml应该设置为此文件所在的路径.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Table getTableByRecordset(Recordset rs,Recordset rsRowHead,Recordset rsColHead,Recordset rsCrossValue) throws Throwable {
		Table t = new Table();
        rs.top();
        //平面报表中需要显示序号问题
        Integer _rowNumber = 0;
        while(rs.next()){
        	_rowNumber ++;
			TableRow tr = new TableRow();
            rsRowHead.top();
            while(rsRowHead.next()){
    			tr.addCell(new TableCell(rs.getString(rsRowHead.getString("field_code"))==null?"":rs.getString(rsRowHead.getString("field_code"))));                
            }
            rsColHead.top();
            while(rsColHead.next()){
            	String value = rs.getString(rsColHead.getString("field_code"));
            	if("${fld:_rowNumber}".equalsIgnoreCase(value)){
            		tr.addCell(new TableCell(_rowNumber.toString()));
            	}else{
            		tr.addCell(new TableCell(value==null?"":value));
            	}  
            }
            rsCrossValue.top();
            while(rsCrossValue.next()){
    			tr.addCell(new TableCell(rs.getString(rsCrossValue.getString("field_code"))==null?"":rs.getString(rsCrossValue.getString("field_code"))));                
            }
			t.addRow(tr);
        }
		return t;
	}

	/**
	 * 获得报表对象
	 * 
	 * @throws Exception
	 * @return Report
	 */
	public static Report getCrossReport(GenericTransaction data) throws Throwable {
        //取得交叉表行表头字段
		Recordset rsRowHead = data.getRecordset("query_rowhead_field.sql");
		if(rsRowHead.getRecordCount()==0){
			throw new Throwable("行项不能为空！");
		}
        //取得交叉表列表头字段
		Recordset rsColHead = data.getRecordset("query_colhead_field.sql");
		if(rsColHead.getRecordCount()==0){
			throw new Throwable("列项不能为空！");
		}
        //取得交叉表值字段
		Recordset rsCrossValue = data.getRecordset("query_crossvalue_field.sql");
		if(rsCrossValue.getRecordCount()==0){
			throw new Throwable("值项不能为空！");
		}
		//是否显示小计
		Recordset rsReport = data.getRecordset("query_report.sql");
		//国际化
		String locale = rsReport.getString("locale");
		
		// 报表管理器
		ReportManager rm = new ReportManager(locale);
		
        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		/* -------------------------Start:定义交叉表----------------------------- */
		int intRowHeadCount = rsRowHead.getRecordCount();
		int intColHeadCount = rsColHead.getRecordCount();
        
		HeadCol[] colH = new HeadCol[intColHeadCount];        
        rsColHead.top();
        while(rsColHead.next()){
            colH[rsColHead.getRecordNumber()] = new HeadCol(intRowHeadCount + rsColHead.getRecordNumber(), rsColHead.getString("head_name"));
        }

		HeadCol[] rowH = new HeadCol[intRowHeadCount];        
        rsRowHead.top();
        while(rsRowHead.next()){
        	/*不知为什么,为了显示表头正确,只能将行记录逆序一下*/
            rowH[intRowHeadCount-1-rsRowHead.getRecordNumber()] = new HeadCol(intRowHeadCount-1-rsRowHead.getRecordNumber(), rsRowHead.getString("head_name"));
        }
        
        rsCrossValue.first();
        rsReport.first();
        String caculate_type = rsCrossValue.getString("cal_type_show");
        String show_total_type = rsReport.getString("show_total_type");
        String show_sub_total_type = rsReport.getString("show_sub_total_type");
        String show_percent_type = rsReport.getString("show_percent_type");
        String is_merge_zero = rsReport.getString("is_merge_zero");
        String is_merge_vertical = rsReport.getString("is_merge_vertical");
        String is_show_zero = rsReport.getString("is_show_zero");
        
		CrossCol crs = new CrossCol(intRowHeadCount+intColHeadCount, rsCrossValue.getString("head_name"), 
		    caculate_type.equals("count")?new CountArithmetic():(
		    caculate_type.equals("sum")?new SumArithmetic():(
		    caculate_type.equals("avg")?new AverageArithmetic():(
		    caculate_type.equals("max")?new MaxArithmetic():(
		    caculate_type.equals("min")?new MinArithmetic():
		    new SumArithmetic()
		    )))));

		CrossTable crossTab = new CrossTable(colH, rowH, crs);
		/* -------------------------End:定义交叉表----------------------------- */

		// 通过原始数据和交叉表的定义生成交叉表
		t = rm.generateCrossTab(t, crossTab,is_merge_zero,is_show_zero,is_merge_vertical);

		// 进行行统计
		if(show_total_type.equals("2")||show_total_type.equals("3")){
			t = rm.generateCrossTabRowTotal(t, crossTab, show_sub_total_type.equals("2")||show_sub_total_type.equals("3")?true:false, 
					show_percent_type.equals("2")||show_percent_type.equals("3")?true:false,
				    caculate_type.equals("count")?new SumArithmetic():(
				    caculate_type.equals("sum")?new SumArithmetic():(
				    caculate_type.equals("avg")?new AverageArithmetic():(
				    caculate_type.equals("max")?new MaxArithmetic():(
				    caculate_type.equals("min")?new MinArithmetic():
				    new SumArithmetic()
				    )))));			
		}

		// 进行列统计
		if(show_total_type.equals("1")||show_total_type.equals("3")){
			t = rm.generateCrossTabColTotal(t, crossTab, show_sub_total_type.equals("1")||show_sub_total_type.equals("3")?true:false,
					show_percent_type.equals("1")||show_percent_type.equals("3")?true:false,
					new SumArithmetic());
		}

		// 格式化数据
		//t = rm.formatData(t, crossTab, new IntegerFormatter());
		//t = rm.formatData(t, crossTab, new DefaultFormatter());
		//t = rm.formatData(t, crossTab, new IntegerFormatter());
		
		Report report = new Report();

		// 将交叉表设为报表的主体
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);

		return report;
	}

	/**
	 * 获得报表对象
	 * 
	 * @throws Exception
	 * @return Report
	 */
	public static Report getGroupReport(GenericTransaction data) throws Throwable {
		
        //取得交叉表行表头字段
		Recordset rsRowHead = new Recordset();
        //取得交叉表列表头字段
		Recordset rsColHead = data.getRecordset("query_colhead_field.sql");
		if(rsColHead.getRecordCount()==0){
			throw new Throwable("列项不能为空！");
		}
        //取得交叉表值字段
		Recordset rsCrossValue = data.getRecordset("query_crossvalue_field.sql");
		if(rsCrossValue.getRecordCount()==0){
			throw new Throwable("值项不能为空！");
		}
		//是否显示小计
		Recordset rsReport = data.getRecordset("query_report.sql");
		//国际化
		String locale = rsReport.getString("locale");
		
		// 报表管理器
		ReportManager rm = new ReportManager(locale);
		
		int intColHeadCount = rsColHead.getRecordCount();
		int intCrossValueCount = rsCrossValue.getRecordCount();

        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		// 定义报表对象
		Report report = new Report();

		// 设置报表主体部分
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);

		/* -------------Start:按指定列分组----------- */
		int[] cols = new int[intColHeadCount];
		rsColHead.top();
		while(rsColHead.next()){
		    cols[rsColHead.getRecordNumber()] = rsColHead.getRecordNumber();
		}
		// 合并列中相邻的同值单元
		t = rm.mergeSameCells(t, cols, ReportManager.COLUMN_ORIENTATION);
		// 按列的先后顺序,完成分组
		t = rm.split(t, cols);
		/* -------------End:按指定列分组----------- */

		/**
		 * Author: 马志礼
		 * Date: 2017-03-18
		 * Content: 设置表格的属性,在Bootstrap中用不到了
		 */
		/*t.setAlign(Rectangle.ALIGN_CENTER);
		t.setWidth(100);
		t.setBorder(1);
		t.setBordercolor(new java.awt.Color(0x000000));*/

		rsCrossValue.first();
		rsReport.first();
        String show_total_type = rsReport.getString("show_total_type");
        String show_sub_total_type = rsReport.getString("show_sub_total_type");
        String show_percent_type = rsReport.getString("show_percent_type");
        
		/* ---------------------Start:进行行统计---------------------- */
		int[] totalCols = new int[intCrossValueCount];
        String[] caculateCols = new String[intColHeadCount+intCrossValueCount];
		rsCrossValue.top();
		while(rsCrossValue.next()){
		    totalCols[rsCrossValue.getRecordNumber()] = intColHeadCount + rsCrossValue.getRecordNumber();
		    caculateCols[intColHeadCount + rsCrossValue.getRecordNumber()] = rsCrossValue.getString("cal_type_show");
		}
		//行汇总统计
		if(show_total_type.equals("2")||show_total_type.equals("3")){
			rm.generateRowTotal(t, totalCols, show_sub_total_type.equals("2")||show_sub_total_type.equals("3")?true:false, 
					show_percent_type.equals("1")||show_percent_type.equals("3")?true:false,
					caculateCols);
		}
		/* ---------------------End:进行行统计---------------------- */
		
		/* ---------------------Start:设置报表主体表格的列头---------------------- */
		HeaderTable th = new HeaderTable();
		report.getBody().setTableColHeader(th);
		TableRow thr = new TableRow(rsColHead.getRecordCount()+rsCrossValue.getRecordCount());
		th.addRow(thr);

		rsColHead.top();
		while(rsColHead.next()){
    		thr.setCell(rsColHead.getRecordNumber(), new TableCell(rsColHead.getString("head_name")));
		}
		
		rsCrossValue.top();
		while(rsCrossValue.next()){
    		thr.setCell(intColHeadCount + rsCrossValue.getRecordNumber(), new TableCell(rsCrossValue.getString("head_name")));
		}
		/* ---------------------End:设置报表主体表格的列头---------------------- */

		return report;
	}

	/**
	 * 获得报表对象
	 * 
	 * @throws Exception
	 * @return Report
	 */
	public static Report getFlatReport(GenericTransaction data) throws Throwable {
		// 报表管理器
		ReportManager rm = new ReportManager();

        //取得交叉表行表头字段
		Recordset rsRowHead = new Recordset();
        //取得交叉表列表头字段
		Recordset rsColHead = data.getRecordset("query_colhead_field.sql");
		if(rsColHead.getRecordCount()==0){
			throw new Throwable("列项不能为空！");
		}
        //取得交叉表值字段
		Recordset rsCrossValue = data.getRecordset("query_crossvalue_field.sql");

		int intColHeadCount = rsColHead.getRecordCount();
		int intCrossValueCount = rsCrossValue.getRecordCount();

        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		// 定义报表对象
		Report report = new Report();

		// 设置报表主体部分
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);

		/**
		 * Author: 马志礼
		 * Date: 2017-03-18
		 * Content: 设置表格的属性,在Bootstrap中用不到了
		 */
		/*t.setAlign(Rectangle.ALIGN_CENTER);
		t.setWidth(100);
		t.setBorder(1);
		t.setBordercolor(new java.awt.Color(0x000000));*/

		
		// 格式化数据
        int[] formatCols = new int[intCrossValueCount];
		rsCrossValue.top();
		while(rsCrossValue.next()){
		    formatCols[rsCrossValue.getRecordNumber()] = intColHeadCount + rsCrossValue.getRecordNumber();
		}
		t = rm.formatData(t, formatCols, new NullFormatter());

		
		// 设置报表主体表格的列头
		HeaderTable th = new HeaderTable();
		report.getBody().setTableColHeader(th);
		TableRow thr = new TableRow(rsColHead.getRecordCount()+rsCrossValue.getRecordCount());
		th.addRow(thr);

		rsColHead.top();
		while(rsColHead.next()){
    		thr.setCell(rsColHead.getRecordNumber(), new TableCell(rsColHead.getString("head_name")));
		}
		
		rsCrossValue.top();
		while(rsCrossValue.next()){
    		thr.setCell(intColHeadCount + rsCrossValue.getRecordNumber(), new TableCell(rsCrossValue.getString("head_name")));
		}

		return report;
	}

}
