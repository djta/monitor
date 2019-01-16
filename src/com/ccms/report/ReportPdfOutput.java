package com.ccms.report;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.ccms.report.lucaslee.ReportException;
import com.ccms.report.lucaslee.ReportManager;
import com.ccms.report.lucaslee.grouparithmetic.AverageArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.CountArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.MaxArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.MinArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.SumArithmetic;
import com.ccms.report.lucaslee.model.HeaderTable;
import com.ccms.report.lucaslee.model.Rectangle;
import com.ccms.report.lucaslee.model.Report;
import com.ccms.report.lucaslee.model.ReportBody;
import com.ccms.report.lucaslee.model.Table;
import com.ccms.report.lucaslee.model.TableCell;
import com.ccms.report.lucaslee.model.TableRow;
import com.ccms.report.lucaslee.model.crosstable.CrossCol;
import com.ccms.report.lucaslee.model.crosstable.CrossTable;
import com.ccms.report.lucaslee.model.crosstable.HeadCol;
import com.ccms.report.lucaslee.printer.PDFCss;
import com.ccms.report.lucaslee.printer.PDFCssItem;
import com.ccms.report.lucaslee.printer.PDFPrinter;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import dinamica.AbstractPDFOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;


public class ReportPdfOutput  extends AbstractPDFOutput
{	
	@Override
    protected void createPDF(GenericTransaction data, ByteArrayOutputStream buf)
            throws Throwable
    {
        //取得报表类型
		Recordset rsReport = data.getRecordset("query_report.sql");

        //取得报表类型
        rsReport.first();
        String strReportType = rsReport.getString("report_type");

        if(strReportType.equals("2")){  /*交叉报表*/
			getPDFReport(getCrossReport(data), buf);
        }else if(strReportType.equals("1")){
            getPDFReport(getGroupReport(data), buf);
        }else if(strReportType.equals("0")){
            getPDFReport(getFlatReport(data), buf);
        }
	}

	/**
	 * 根据报表对象生成pdf格式的报表.
	 * 
	 * @param report
	 *            报表对象
	 * @throws Exception
	 */
	public static void getPDFReport(Report report, OutputStream os)
			throws Exception {
		// ******************定义pdf中文字体*****************
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		BaseFont bfChineseBold = BaseFont.createFont("STSong-Light,Bold",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		Font FontChinese = new Font(bfChinese, 10, Font.NORMAL);
		Font FontChineseBold = new Font(bfChineseBold, 10, Font.NORMAL);
		// ******************end 定义pdf中文字体*****************

		// ****************设置pdf报表的样式表*********************
		PDFCss css = new PDFCss();

		PDFCssItem item = new PDFCssItem();
		item.setBackgroudColor(new Color(0xd8e4f1));
		item.setFont(FontChinese);
		css.setGroupTotal(item);
		css.setTotal(item);

		item = new PDFCssItem();
		item.setBackgroudColor(new Color(0xffdead));
		item.setFont(FontChineseBold);
		css.setHead(item);

		item = new PDFCssItem();
		item.setFont(new Font(bfChineseBold, 15, Font.BOLD));
		css.setTitle(item);

		item = new PDFCssItem();
		item.setFont(new Font(bfChinese, 10, Font.NORMAL));
		css.setData(item);

		item = new PDFCssItem();
		item.setFont(new Font(bfChinese, 7, Font.NORMAL));
		item.setBackgroudColor(new Color(0xffdead));
		css.setCrossHeadHead(item);
		// ****************end 设置pdf报表的样式表*********************

		// 执行PDF格式报表的输出
		new PDFPrinter().print(report, css, os);

	}

	/**
	 * 设置报表的头部和尾部.
	 * 
	 * @param report
	 *            要设置头尾的报表对象.
	 * @throws ReportException
	 */
	private static void setTitleFooter(Report report,GenericTransaction data) throws Throwable {
        //取得报表类型
		Recordset rsReport = data.getRecordset("query_report.sql");

        //取得报表类型
        rsReport.first();
        String strReportName = rsReport.getString("report_name");

		// *****************设置报表头部*********************
		Table headerTable = new Table();
		// 设置表格的宽度比例(百分比)
		int[] widths = { 100 };
		headerTable.setWidths(widths);
		report.setHeaderTable(headerTable);

		headerTable.setBorder(0);
		headerTable.setAlign(Table.ALIGN_CENTER);

		TableCell tc = null;
		TableRow tr = null;

		tr = new TableRow(1);
		headerTable.addRow(tr);
		tc = tr.getCell(0);
		tc.setColSpan(1);
		tc.setAlign(TableCell.ALIGN_CENTER);
		tc.setContent(strReportName);
		//tr.getCell(1).setIsHidden(true);

		// *****************end 设置报表头部*********************

		// *****************设置报表尾部*********************
		Table footerTable = new Table();
		report.setFooterTable(footerTable);

		tr = new TableRow(3);
		footerTable.setBorder(0);
		footerTable.setAlign(Table.ALIGN_CENTER);
		footerTable.addRow(tr);
		tr.getCell(0).setContent("制表人:");
		tr.getCell(0).setAlign(TableCell.ALIGN_LEFT);
		tr.getCell(1).setContent("审核人:");
		tr.getCell(1).setAlign(TableCell.ALIGN_CENTER);

		java.util.Date d = new java.util.Date();
		String curr_date = StringUtil.formatDate(d, "yyyy-MM-dd");
		
		tr.getCell(2).setContent("制表日期:"+curr_date);
		tr.getCell(2).setAlign(TableCell.ALIGN_RIGHT);
		// *****************end 设置报表尾部*********************
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
        while(rs.next()){
			TableRow tr = new TableRow();
            rsRowHead.top();
            while(rsRowHead.next()){
    			tr.addCell(new TableCell(rs.getString(rsRowHead.getString("field_code"))==null?"":rs.getString(rsRowHead.getString("field_code"))));                
            }
            rsColHead.top();
            while(rsColHead.next()){
    			tr.addCell(new TableCell(rs.getString(rsColHead.getString("field_code"))==null?"":rs.getString(rsColHead.getString("field_code"))));                
            }
            rsCrossValue.top();
            while(rsCrossValue.next()){
    			tr.addCell(new TableCell(rs.getString(rsCrossValue.getString("field_code"))==null?"0":rs.getString(rsCrossValue.getString("field_code"))));                
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
		// 报表管理器
		ReportManager rm = new ReportManager();

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

        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		// ************定义交叉表*************
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
		// ************end 定义交叉表*************

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
			t = rm
					.generateCrossTabColTotal(t, crossTab, show_sub_total_type.equals("1")||show_sub_total_type.equals("3")?true:false,
							show_percent_type.equals("1")||show_percent_type.equals("3")?true:false,
							new SumArithmetic());
		}

		// 格式化数据
		//t = rm.formatData(t, crossTab, new DefaultFormatter());

		// ****************定义报表的其他部分****************
		Report report = new Report();

		// 将交叉表设为报表的主体
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);

		// 设置报表的头和尾两部分
		setTitleFooter(report,data);
		// ****************end 定义报表的其他部分************
		return report;
	}

	/**
	 * 获得报表对象
	 * 
	 * @throws Exception
	 * @return Report
	 */
	public static Report getGroupReport(GenericTransaction data) throws Throwable {
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
		if(rsCrossValue.getRecordCount()==0){
			throw new Throwable("值项不能为空！");
		}
		//是否显示小计
		Recordset rsReport = data.getRecordset("query_report.sql");

		int intColHeadCount = rsColHead.getRecordCount();
		int intCrossValueCount = rsCrossValue.getRecordCount();

        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		// 定义报表对象
		Report report = new Report();

		// **************设置报表主体部分**************
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);
		// **************end 设置报表主体部分**************

		// ***********按指定列分组**********
		int[] cols = new int[intColHeadCount];
		rsColHead.top();
		while(rsColHead.next()){
		    cols[rsColHead.getRecordNumber()] = rsColHead.getRecordNumber();
		}
		// 合并列中相邻的同值单元
		t = rm.mergeSameCells(t, cols, ReportManager.COLUMN_ORIENTATION);
		// 按列的先后顺序,完成分组
		t = rm.split(t, cols);
		// ***********end 按指定列分组**********

		// *****************设置表格的属性********************
		t.setAlign(Rectangle.ALIGN_CENTER);
		t.setWidth(75);
		t.setBorder(1);
		t.setBordercolor(new java.awt.Color(0x000000));
		// *****************end 设置表格的属性********************

		rsCrossValue.first();
		rsReport.first();
        String caculate_type = rsCrossValue.getString("cal_type_show");
        String show_total_type = rsReport.getString("show_total_type");
        String show_sub_total_type = rsReport.getString("show_sub_total_type");
        String show_percent_type = rsReport.getString("show_percent_type");

		// ********************进行行统计*********************
		int[] totalCols = new int[intCrossValueCount];
        int[] formatCols = new int[intCrossValueCount];
		rsCrossValue.top();
		while(rsCrossValue.next()){
		    totalCols[rsCrossValue.getRecordNumber()] = intColHeadCount + rsCrossValue.getRecordNumber();
		    formatCols[rsCrossValue.getRecordNumber()] = intColHeadCount + rsCrossValue.getRecordNumber();
		}

		if(show_total_type.equals("2")||show_total_type.equals("3")){
			rm.generateRowTotal(t, totalCols, show_sub_total_type.equals("2")||show_sub_total_type.equals("3")?true:false, 
					show_percent_type.equals("1")||show_percent_type.equals("3")?true:false,
				    caculate_type.equals("count")?new SumArithmetic():(
				    caculate_type.equals("sum")?new SumArithmetic():(
				    caculate_type.equals("avg")?new AverageArithmetic():(
				    caculate_type.equals("max")?new MaxArithmetic():(
				    caculate_type.equals("min")?new MinArithmetic():
					new SumArithmetic()
				    )))));
		}
		// ********************end 进行行统计*********************

		// *********************格式化数据**********************
		//int[] formatCols = { 1, 2, 3 };
		//t = rm.formatData(t, formatCols, new DefaultFormatter());
		// *********************end 格式化数据**********************

		// *****************设置报表主体表格的列头*********************/
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
		// *****************end 设置报表主体表格的列头*********************/

		// 设置报表的头和尾
		setTitleFooter(report,data);

		return report;
	}

	/**
	 * 获得报表对象
	 * 
	 * @throws Exception
	 * @return Report
	 */
	public static Report getFlatReport(GenericTransaction data) throws Throwable {

        //取得交叉表行表头字段
		Recordset rsRowHead = new Recordset();
        //取得交叉表列表头字段
		Recordset rsColHead = data.getRecordset("query_colhead_field.sql");
		if(rsColHead.getRecordCount()==0){
			throw new Throwable("列项不能为空！");
		}
        //取得交叉表值字段
		Recordset rsCrossValue = data.getRecordset("query_crossvalue_field.sql");
		//if(rsCrossValue.getRecordCount()==0){
		//	throw new Throwable("值项不能为空！");
		//}

		int intColHeadCount = rsColHead.getRecordCount();
		int intCrossValueCount = rsCrossValue.getRecordCount();

        //取得查询结果集
		Recordset rsQuery = data.getRecordset("query-report-result.sql");

		// 待处理的原始数据表格对象
		Table t = getTableByRecordset(rsQuery,rsRowHead,rsColHead,rsCrossValue);

		// 定义报表对象
		Report report = new Report();

		// **************设置报表主体部分**************
		ReportBody body = new ReportBody();
		body.setData(t);
		report.setBody(body);
		// **************end 设置报表主体部分**************

		// *****************设置表格的属性********************
		t.setAlign(Rectangle.ALIGN_CENTER);
		t.setWidth(75);
		t.setBorder(1);
		t.setBordercolor(new java.awt.Color(0x000000));
		// *****************end 设置表格的属性********************

		// *********************格式化数据**********************
		//int[] formatCols = { 1, 2, 3 };
        int[] formatCols = new int[intCrossValueCount];
		rsCrossValue.top();
		while(rsCrossValue.next()){
		    formatCols[rsCrossValue.getRecordNumber()] = intColHeadCount + rsCrossValue.getRecordNumber();
		}
		//t = rm.formatData(t, formatCols, new DefaultFormatter());
		// *********************end 格式化数据**********************

		// *****************设置报表主体表格的列头*********************/
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
		// *****************end 设置报表主体表格的列头*********************/

		// 设置报表的头和尾
		setTitleFooter(report,data);

		return report;
	}

}
