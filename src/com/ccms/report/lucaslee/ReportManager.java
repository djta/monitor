package com.ccms.report.lucaslee;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.ccms.report.lucaslee.grouparithmetic.AverageArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.GroupArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.MaxArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.MinArithmetic;
import com.ccms.report.lucaslee.grouparithmetic.SumArithmetic;
import com.ccms.report.lucaslee.model.Report;
import com.ccms.report.lucaslee.model.Table;
import com.ccms.report.lucaslee.model.TableCell;
import com.ccms.report.lucaslee.model.TableColumn;
import com.ccms.report.lucaslee.model.TableLine;
import com.ccms.report.lucaslee.model.TableRow;
import com.ccms.report.lucaslee.model.crosstable.CrossTable;
import com.ccms.report.lucaslee.model.crosstable.HeadCol;

/**
 * 报表管理器。主要的报表操作都在此类中进行。
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:Lucas-lee Soft
 * </p>
 * 
 * @author Lucas Lee
 * @version 1.0
 */

public class ReportManager {

	// 为true则打印调试信息
	private boolean debug = false;

	/** 行方向,即行延伸的方向，水平向右。 */
	public static final int ROW_ORIENTATION = 1;

	/** 列方向，即列延伸的方向，垂直向下 */
	public static final int COLUMN_ORIENTATION = 2;

	/** 总计或者小计等显示是中文还是英文 en/cn，默认cn */
	public String locale = "cn"; 
	
	public static final String LOCALE_CN = "cn";
	public static final String LOCALE_EN = "en";
	
	public ReportManager(){
		
	}
	
	public ReportManager(String locale){
		this.locale = locale;
	}
	
	/**
	 * 检测一个数是否在数组中。
	 * 
	 * @param a
	 *            要检测的数
	 * @param col
	 *            数组
	 * @return
	 */
	private boolean isAmonge(int a, int[] col) {
		for (int i = 0; i < col.length; i++) {
			if (a == col[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得一个汇总行.
	 * 
	 * @param t
	 *            表格对象
	 * @param from
	 *            起始行,包括此行
	 * @param end
	 *            终止行,不包括此行
	 * @param memo
	 *            汇总行的注释
	 * @param totalCols
	 *            进行汇总列号数组
	 * @param arith
	 *            统计算法
	 */
	private TableRow getTotalRow(Table t, int from, int end, String memo,
			int[] totalCols, GroupArithmetic arith) throws ReportException {
		TableRow totalRow = new TableRow();

		int[] tempTotalCols = (int[]) totalCols.clone(); // 克隆到新变量是为了不改变入口参数
		Arrays.sort(tempTotalCols);
		int nonTotal = 0; // 指明头几个不需要汇总的列，值为这其中最后一个列数。
		if (tempTotalCols.length > 0) {
			nonTotal = tempTotalCols[0] - 1;
		}

		// 添加注释单元
		TableCell desc = new TableCell(memo);
		desc.setColSpan(nonTotal + 1);
		totalRow.addCell(desc);

		// 这几个单元是为了符合表格标准（每行的cell数一致）,使每行的单元数相等,不显示出来
		for (int i = 0; i < nonTotal; i++) {
			TableCell nullCell = new TableCell("");
			nullCell.setIsHidden(true);
			totalRow.addCell(nullCell);
		}
		//标识是否为百分比计算
		boolean is_percent = false;
		
		for (int k = 0; k < t.getColCount(); k++) { // 遍历列
			if (k > nonTotal) { // "小计"列不用添加了
				TableCell c = new TableCell("");

				if (isAmonge(k, totalCols)) { // 指定的统计列才进行汇总,非统计列是一个空字符串

					String[] values = new String[end - from]; // 记录要汇总的一个序列的数据
					for (int j = from; j < end; j++) { // 从上至下扫描
						try {
							if (debug) {
								System.out.println("row:" + j + " col:" + k);
							}
							String value = (String) t.getRow(j).getCell(k)
									.getContent();
							if (value != null) {
								if (value.indexOf("%")>0) 
									is_percent=true;

								value = value.replace("%", "");	//去掉百分号
								value = value.replace(",", "");	//去掉千位分隔符
								value = value.trim();
								//判断是否为数字
								if(isNumeric(value)){
									values[j - from] = String.valueOf(Double.parseDouble(value));	
								}else{
									values[j - from] = value;
								}
							}
						} catch (NumberFormatException e) {
						}
					}
					String totalValue = arith.getResult(values);   
					if(is_percent){
						DecimalFormat f = new DecimalFormat();
						f.setMaximumFractionDigits(2);
						f.setMinimumFractionDigits(0);	//百分比保留两位小数

						totalValue = totalValue.replace(",", "");	//去掉千位分隔符
						totalValue = String.valueOf(f.format(Double.parseDouble(totalValue)/values.length))+"%";		
					}
					
					c.setContent(totalValue);
					is_percent = false;
				}
				totalRow.addCell(c);
			}
		}

		return totalRow;
	}
	
	private TableRow getTotalRow(Table t, int from, int end, String memo,
			int[] totalCols, String[] arith) throws ReportException {
		TableRow totalRow = new TableRow();

		int[] tempTotalCols = (int[]) totalCols.clone(); // 克隆到新变量是为了不改变入口参数
		Arrays.sort(tempTotalCols);
		int nonTotal = 0; // 指明头几个不需要汇总的列，值为这其中最后一个列数。
		if (tempTotalCols.length > 0) {
			nonTotal = tempTotalCols[0] - 1;
		}

		// 添加注释单元
		TableCell desc = new TableCell(memo);
		desc.setColSpan(nonTotal + 1);
		totalRow.addCell(desc);

		// 这几个单元是为了符合表格标准（每行的cell数一致）,使每行的单元数相等,不显示出来
		for (int i = 0; i < nonTotal; i++) {
			TableCell nullCell = new TableCell("");
			nullCell.setIsHidden(true);
			totalRow.addCell(nullCell);
		}

		//标识是否为百分比计算
		boolean is_percent = false;
		
		for (int k = 0; k < t.getColCount(); k++) { // 遍历列
			if (k > nonTotal) { // "小计"列不用添加了
				TableCell c = new TableCell("");

				if (isAmonge(k, totalCols)) { // 指定的统计列才进行汇总,非统计列是一个空字符串

					String[] values = new String[end - from]; // 记录要汇总的一个序列的数据
					for (int j = from; j < end; j++) { // 从上至下扫描
						try {
							if (debug) {
								System.out.println("row:" + j + " col:" + k);
							}
							String value = (String) t.getRow(j).getCell(k)
									.getContent();
							if (value != null) {
								if (value.indexOf("%")>0) 
									is_percent=true;
								
								value = value.replace("%", "");	//去掉百分号
								value = value.replace(",", "");	//去掉千位分隔符
								value = value.trim();
								//判断是否为数字
								if(isNumeric(value)){
									values[j - from] = String.valueOf(Double.parseDouble(value));	
								}else{
									values[j - from] = value;
								}
							}
						} catch (NumberFormatException e) {
						}
					}
					String arithThis = "";
					arithThis = ( null != arith[k] ? arith[k] : "" );
					GroupArithmetic arithMe=arithThis.equals("count")?new SumArithmetic():(
							arithThis.equals("sum")?new SumArithmetic():(
								arithThis.equals("avg")?new AverageArithmetic():(
									arithThis.equals("max")?new MaxArithmetic():(
											arithThis.equals("min")?new MinArithmetic():
												new SumArithmetic()))));
					String totalValue = arithMe.getResult(values);    

					if(is_percent){
						DecimalFormat f = new DecimalFormat();
						f.setMaximumFractionDigits(2);
						f.setMinimumFractionDigits(0);	//百分比保留两位小数

						totalValue = totalValue.replace(",", "");	//去掉千位分隔符
						totalValue = String.valueOf(f.format(Double.parseDouble(totalValue)/values.length))+"%";		
					}					

					c.setContent(isNumeric(totalValue)?totalValue:"");
					is_percent = false;
				}
				totalRow.addCell(c);
			}
		}

		return totalRow;
	}
	
	public boolean isNumeric(String str){
		if(str == null || str.trim().length() == 0) return false;
		if(str.matches("\\d*") || str.matches(("^\\d{1,3}(,\\d{3})*(\\.\\d+)?|\\d+(\\.\\d+)?$"))){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 获得一个汇总行百分比.
	 * 
	 * @param tr
	 *            总汇总行对象
	 * @param memo
	 *            汇总行的注释
	 * @param totalColCount
	 *            总列数
	 * @param totalCols
	 *            进行汇总列号数组
	 * @param arith
	 *            统计算法
	 */
	private TableRow getTotalRowPercent(TableRow tr, String memo,int totalColCount,
			int[] totalCols) throws ReportException {
		TableRow totalRowPercent = new TableRow();

		int[] tempTotalCols = (int[]) totalCols.clone(); // 克隆到新变量是为了不改变入口参数
		Arrays.sort(tempTotalCols);
		int nonTotal = 0; // 指明头几个不需要汇总的列，值为这其中最后一个列数。
		if (tempTotalCols.length > 0) {
			nonTotal = tempTotalCols[0] - 1;
		}

		// 添加注释单元
		TableCell desc = new TableCell(memo);
		desc.setColSpan(nonTotal + 1);
		totalRowPercent.addCell(desc);

		// 这几个单元是为了符合表格标准（每行的cell数一致）,使每行的单元数相等,不显示出来
		for (int i = 0; i < nonTotal; i++) {
			TableCell nullCell = new TableCell("");
			nullCell.setIsHidden(true);
			totalRowPercent.addCell(nullCell);
		}
		//遍历列,选把总数计算出来.
		Double dTotalValue = new Double("0");
		for (int k = 0; k < totalColCount; k++) { // 遍历列
			if (k > nonTotal) { // "小计"列不用添加了
				if (isAmonge(k, totalCols)) { // 指定的统计列才进行汇总,非统计列是一个空字符串
					//小计值
					String value = (String) tr.getCell(k)
					.getContent();
					value = value.replace("%", "");
					if(value != null && isNumeric(value)){
						dTotalValue	= dTotalValue+Double.parseDouble(value.replace(",", ""));				
					}
				}
			}
		}

		for (int k = 0; k < totalColCount; k++) { // 遍历列
			if (k > nonTotal) { // "小计"列不用添加了
				TableCell c = new TableCell("");

				if (isAmonge(k, totalCols)) { // 指定的统计列才进行汇总,非统计列是一个空字符串
					//小计值
					String value = (String) tr.getCell(k)
					.getContent();

					String valuePercent = "";
					value = value.replace("%", "");

					if (value != null && !StringUtils.isBlank(value) && !value.equals("")&& dTotalValue!=0 && isNumeric(value)) {
						DecimalFormat f = new DecimalFormat();
						f.setMaximumFractionDigits(2);
						f.setMinimumFractionDigits(2);	//百分比保留两位小数
						valuePercent = f.format((Double.parseDouble(value.replace(",", ""))/dTotalValue)*100)+"%";
					}else{
						if(dTotalValue == 0){
							valuePercent = "0%";	//100%?
						}else{
							valuePercent = "0%";
						}
					}

					c.setContent(valuePercent + "");
				}
				totalRowPercent.addCell(c);
			}
		}

		return totalRowPercent;
	}

	/**
	 * 用父级列形成的表格线分割子列。 一个表可能有多列需要进行同值合并，此方法使前面的列总能完全包容后面的列.
	 * 即从逻辑上说，若此表表示一个分类表，前面的列总是后面的父级分类。 实现的操作是，用父级列形成的表格线分割子列。
	 * 
	 * @param t
	 *            表格
	 * @param cols
	 *            要进行合并的列,应该是连续的
	 */
	public Table split(Table t, int[] cols) throws ReportException {
		for (int i = 1; i < cols.length; i++) { // 从左至右扫描
			TableColumn pre = t.getCol(i - 1);
			TableColumn curr = t.getCol(i);
			for (int j = 0; j < pre.getCellCount(); j++) { // 从上至下扫描
				if (pre.getCell(j).getIsHidden() == false) {
					if (curr.getCell(j).getIsHidden() == false || j == 0) {
						continue;
					}
					for (int k = j - 1; k >= 0; k--) { // 从下至上扫描
						if (curr.getCell(k).getIsHidden() == false) {
							curr.getCell(j).setRowSpan(
									curr.getCell(k).getRowSpan() - (j - k));
							curr.getCell(k).setRowSpan(j - k);
							break;
						}
					}
					curr.getCell(j).setIsHidden(false);

				}
			}
		}
		return t;
	}

	/**
	 * 格式化交叉表的数据。
	 * 
	 * @param t
	 *            交叉数据表
	 * @param crossTab
	 *            交叉表定义
	 * @param f
	 *            格式化对象
	 * @return 处理后的数据表
	 * @throws ReportException
	 */
	public Table formatData(Table t, CrossTable crossTab, Formatter f)
			throws ReportException {
		return formatData(t, crossTab.getColHeader().length, t.getRowCount(),
				crossTab.getRowHeader().length, t.getColCount(), f);
	}

	/**
	 * 格式化数据.
	 * 
	 * @param t
	 *            要处理的表
	 * @param fromRow
	 *            开始行,包含此行
	 * @param toRow
	 *            终止行,不包含此行
	 * @param fromCol
	 *            开始列,包含此列
	 * @param toCol
	 *            终止列,不包含此列
	 * @param f
	 *            格式化对象
	 * @return 处理完的表
	 * @throws ReportException
	 */
	public Table formatData(Table t, int fromRow, int toRow, int fromCol,
			int toCol, Formatter f) throws ReportException {
		String str;
		for (int i = fromRow; i < toRow; i++) {
			for (int j = fromCol; j < toCol; j++) {
				try {
					str = f.format((String) t.getCell(i, j).getContent());
					t.getCell(i, j).setContent(str);
				} catch (IllegalArgumentException e) {
					// 忽略并继续
				} catch (ParseException e) {
					// 忽略并继续
				}
			} // for
		}
		return t;

	}

	/**
	 * 格式化表格数据.
	 * 
	 * @param t
	 *            表格
	 * @param cols
	 *            在此数组里的列才进行格式化
	 * @param f
	 *            格式化对象
	 */
	public Table formatData(Table t, int[] cols, Formatter f)
			throws ReportException {
		for (int i = 0; i < cols.length; i++) {
			formatData(t, 0, t.getRowCount(), cols[i], cols[i] + 1, f);
		}
		return t;
	}

	/**
	 * 操作一个行或列，合并相邻且内容相同的单元。
	 * 
	 * @param line
	 *            要操作的列对象。
	 * @return
	 */
	public TableLine mergeSameCell(TableLine line) throws ReportException {

		String curr = null; // 当前单元内容
		String pre = null; // 上一单元内容
		int pointer = 0; // 位置指针，指向相邻同值单元组的第一个单元
		int count = 1; // 相邻同值单元组内单元数
		for (int i = 0; i < line.getCellCount(); i++) { // 从上至下扫描
			curr = (String) line.getCell(i).getContent();
			if (i > 0) {
				if ((curr != null && ObjectUtils.equals(curr, pre))
						|| (curr == null && pre == null)) { // 若当前单元与上一个单元值相等，
					count++;
					// 则让第一个同值单元的span值加1;
					line.setSpan(line.getCell(pointer), count);
					line.getCell(i).setIsHidden(true); // 且不显示本单元。
				} else { // 若不相等
					pointer = i; // 则指针指向本单元
					count = 1; // 且清空计数器
				}
			}
			pre = curr;
		}
		return line;
	}

	public TableLine mergeCrossSameCell(TableLine line,int fromRowOrCol) throws ReportException {

		String curr = null; // 当前单元内容
		String pre = null; // 上一单元内容
		int pointer = 0; // 位置指针，指向相邻同值单元组的第一个单元
		int count = 1; // 相邻同值单元组内单元数
		for (int i = fromRowOrCol; i < line.getCellCount(); i++) { // 从上至下扫描
			curr = (String) line.getCell(i).getContent();
			if (i > 0) {
				if ((curr != null && ObjectUtils.equals(curr, pre))
						|| (curr == null && pre == null)) { // 若当前单元与上一个单元值相等，
					count++;
					// 则让第一个同值单元的span值加1;
					line.setSpan(line.getCell(pointer), count);
					line.getCell(i).setIsHidden(true); // 且不显示本单元。
				} else { // 若不相等
					pointer = i; // 则指针指向本单元
					count = 1; // 且清空计数器
				}
			}
			pre = curr;
		}
		return line;
	}
	
	/*纵向合并列头,横向合并行头*/
	public TableLine mergeCrossSameCell(TableLine line,int fromRowOrCol,int toRowOrCol) throws ReportException {

		String curr = null; // 当前单元内容
		String pre = null; // 上一单元内容
		int pointer = 0; // 位置指针，指向相邻同值单元组的第一个单元
		int count = 1; // 相邻同值单元组内单元数
		for (int i = fromRowOrCol; i < toRowOrCol; i++) { // 从上至下扫描
			curr = (String) line.getCell(i).getContent();
			if (i > 0) {
				if ((curr != null && ObjectUtils.equals(curr, pre))
						|| (curr == null && pre == null)) { // 若当前单元与上一个单元值相等，
					count++;
					// 则让第一个同值单元的span值加1;
					line.setSpan(line.getCell(pointer), count);
					line.getCell(i).setIsHidden(true); // 且不显示本单元。
				} else { // 若不相等
					pointer = i; // 则指针指向本单元
					count = 1; // 且清空计数器
				}
			}
			pre = curr;
		}
		return line;
	}
	
	/**
	 * 操作一个表格对象，合并相邻且内容相同的单元。
	 * 
	 * @param t
	 *            表格
	 * @param lines
	 *            要合并的列的列号。
	 * @param orientation
	 *            操作的行或列方向。接受本类中后缀为_ORIENTATION的常数。
	 */
	public Table mergeSameCells(Table t, int[] lines, int orientation)
			throws ReportException {
		for (int i = 0; i < lines.length; i++) {
			if (orientation == ReportManager.ROW_ORIENTATION) {
				t
						.setRow(lines[i], (TableRow) mergeSameCell(t
								.getRow(lines[i])));
			} else if (orientation == ReportManager.COLUMN_ORIENTATION) {
				t.setCol(lines[i], (TableColumn) mergeSameCell(t
						.getCol(lines[i])));
			}
		}
		return t;
	}


	public Table mergeCrossSameCells(Table t, int[] lines, int orientation,int fromNumber)
			throws ReportException {
		for (int i = 0; i < lines.length; i++) {
			if (orientation == ReportManager.ROW_ORIENTATION) {
				t
						.setRow(lines[i], (TableRow) mergeCrossSameCell(t
								.getRow(lines[i]),fromNumber));
			} else if (orientation == ReportManager.COLUMN_ORIENTATION) {
				t.setCol(lines[i], (TableColumn) mergeCrossSameCell(t
						.getCol(lines[i]),fromNumber));
			}
		}
		return t;
	}	
	
	/*纵向合并列头或横向合并行头*/
	public Table mergeCrossSameCells(Table t, int[] lines, int orientation,int fromNumber,int toNumber)
			throws ReportException {
		for (int i = 0; i < lines.length; i++) {
			if (orientation == ReportManager.ROW_ORIENTATION) {
				t
						.setRow(lines[i], (TableRow) mergeCrossSameCell(t
								.getRow(lines[i]),fromNumber,toNumber));
			} else if (orientation == ReportManager.COLUMN_ORIENTATION) {
				t.setCol(lines[i], (TableColumn) mergeCrossSameCell(t
						.getCol(lines[i]),fromNumber,toNumber));
			}
		}
		return t;
	}	

	/**
	 * 获得不重复的元素集合，并按指定顺序排序。
	 * 
	 * @param line
	 *            要处理的集合。
	 * @param seq
	 *            用于指定排序的元素集合。 如：给出D，B，C元素集合，则结果按这个序列排序，不在此序列中的不排序。
	 * @return
	 * @throws ReportException
	 */
	public Set getDistinctSet(TableLine line, Vector seq)
			throws ReportException {
		Comparator comp = new SortComparator(seq);
		Vector contents = new Vector();
		for (int i = 0; i < line.getCellCount(); i++) {
			contents.add(line.getCell(i).getContent());
		}
		// 去除重复元素
		TreeSet temp = new TreeSet(contents);
		// 按指定顺序排序
		TreeSet result = new TreeSet(comp);
		result.addAll(temp);
		return result;
	}

	/**
	 * 在表的指定若干列中查找这几列的单元都匹配的行号
	 * 
	 * @param col
	 *            列号数组
	 * @param val
	 *            要比较的值数组,应该和col的长度相同
	 * @param t
	 *            表
	 */
	private int searchRow(int[] col, Object[] val, Table t)
			throws ReportException {
		if (col.length != val.length) {
			throw new ReportException("输入的参数有误.");
		}
		int rt = -1;
		if (t == null || t.getRowCount() <= 0) {
			return rt;
		}

		for (int i = 0; i < t.getRowCount(); i++) {
			boolean tag = true; // 在每一行的查找前，置为true
			for (int j = 0; j < col.length; j++) {
				if (debug) {
					System.out.println(t.getCell(i, col[j]).getContent() + "--"
							+ val[j]);
				}
				if (!ObjectUtils.equals(t.getCell(i, col[j]).getContent(),
						val[j])) {

					tag = false;
					if (debug) {
						System.out.println("不相等:" + tag);
					}
					break;
				}
			}
			if (tag == true) { // 如果在这一行的比较中，都匹配，则返回行号。
				if (debug)
					System.out.println("kk:" + tag);

				return i;
			}
		}
		if (debug) {
			System.out.println("searchRow end-------------");
		}
		return rt;
	}

	/**
	 * 生成交叉表的列汇总统计。 <BR>
	 * TODO 仅用旋转方式来验证算法，应该直接使用列汇总算法，应该比较容易做到。
	 * 
	 * @param t
	 *            数据表
	 * @param crossTab
	 *            交叉表对象
	 * @param isSubTotal
	 *            是否小计。true则进行小计。
	 * @param isPercent
	 *            是否显示百分比。true则显示百分比。
	 * @param arith
	 *            统计算法
	 * @return 处理后的表格
	 * @throws ReportException
	 */
	public Table generateCrossTabColTotal(Table t, CrossTable crossTab,
			boolean isSubTotal, boolean isPercent, GroupArithmetic arith) throws ReportException {
		//应该把百分比列去除
		int percentColCount=0;
		for(int i=crossTab.getColHeader().length;i<t.getRowCount();i++){
			TableRow tr=t.getRow(i);
			if(tr.getType()==Report.GROUP_PERCENT_TYPE ||tr.getType()==Report.GROUP_TOTAL_TYPE || tr.getType()==Report.TOTAL_PERCENT_TYPE|| tr.getType()==Report.TOTAL_TYPE){
				percentColCount=percentColCount+1;
			}
		}
		
		//设置应该计算的百分比列,去掉百分比的列和合计小计的列
		int[] cols_percent = new int[t.getRowCount() - crossTab.getColHeader().length - percentColCount];
		int j = 0;
		for(int i=crossTab.getColHeader().length;i<t.getRowCount();i++){
			TableRow tr=t.getRow(i);
			if(tr.getType()!=Report.GROUP_PERCENT_TYPE &&tr.getType()!=Report.GROUP_TOTAL_TYPE && tr.getType()!=Report.TOTAL_PERCENT_TYPE&& tr.getType()!=Report.TOTAL_TYPE){
				cols_percent[j] = i;
				j =j+1;
			}
		}

		//计算百分比有多少行
		int m = 0;
		for(int i=crossTab.getColHeader().length;i<t.getRowCount();i++){
			TableRow tr=t.getRow(i);
			if(tr.getType()==Report.GROUP_PERCENT_TYPE || tr.getType()==Report.TOTAL_PERCENT_TYPE){
				m = m+1;
			}
		}
		
		//设置应该计算的值列,忽略百分比的列
		int[] cols = new int[t.getRowCount() - crossTab.getColHeader().length -m];
		int k = crossTab.getColHeader().length;
		for (int i = 0; i < cols.length; i++) {
			if (k>=t.getRowCount()) break;
			TableRow tr=t.getRow(k);
			if(tr.getType()!=Report.GROUP_PERCENT_TYPE && tr.getType()!=Report.TOTAL_PERCENT_TYPE){
				cols[i] = k ;
			}else{
				i--;	//停在当前行
			}
				
			k = k+1;
		}
		Table result = CssEngine.applyCss(t);
		result = result.getRotateTable();

		result = generateRowTotal(result, crossTab.getRowHeader().length,
				result.getRowCount(), cols,cols_percent, isSubTotal, isPercent, arith);
		result = CssEngine.applyCss(result);
		result = result.getRotateTable();
		return result;
	}

	/**
	 * 对交叉表做行汇总统计。
	 * 
	 * @param t
	 *            数据表
	 * @param crossTab
	 *            交叉表
	 * @param isSubTotal
	 *            是否小计，true则小计
	 * @param isPercent
	 *            是否显示百分比，true则显示百分比
	 * @param arith
	 *            统计算法
	 * @return 处理后的表格
	 * @throws ReportException
	 */
	public Table generateCrossTabRowTotal(Table t, CrossTable crossTab,
			boolean isSubTotal, boolean isPercent, GroupArithmetic arith) throws ReportException {
		int[] totalCols = new int[t.getColCount()
				- crossTab.getRowHeader().length];
		for (int i = 0; i < totalCols.length; i++) {
			totalCols[i] = i + crossTab.getRowHeader().length;
		}

		Table result = generateRowTotal(t, crossTab.getColHeader().length, t
				.getRowCount(), totalCols,totalCols, isSubTotal, isPercent, arith);
		return result;
	}

	/**
	 * 生成行汇总统计。包括小计、总计。
	 * 
	 * @param t
	 *            数据表
	 * @param totalCols
	 *            统计的列
	 * @param isSubTotal
	 *            是否小计
	 * @param isPercent
	 *            是否显示百分比
	 * @param arith
	 *            统计算法
	 * @return 处理后的数据表
	 * @throws ReportException
	 */
	public Table generateRowTotal(Table t, int[] totalCols, boolean isSubTotal, boolean isPercent,
			GroupArithmetic arith) throws ReportException {
		return generateRowTotal(t, 0, t.getRowCount(), totalCols,totalCols, isSubTotal, isPercent,
				arith);
	}

	public Table generateRowTotal(Table t, int[] totalCols, boolean isSubTotal, boolean isPercent,
			String[] arith) throws ReportException {
		return generateRowTotal(t, 0, t.getRowCount(), totalCols,totalCols, isSubTotal, isPercent,
				arith);
	}

	/**
	 * 生成行汇总统计。包括小计、总计。
	 * 
	 * @param t
	 *            数据表
	 * @param fromRow
	 *            要统计的行开始的行号，包括此行。
	 * @param toRow
	 *            要统计的行结束的行号，不包括此行。
	 * @param totalCols
	 *            统计的列
	 * @param isSubTotal
	 *            是否小计
	 * @param isPercent
	 *            是否显示百分比
	 * @param arith
	 *            统计算法
	 * @return 处理后的数据表
	 * @throws ReportException
	 */
	private Table generateRowTotal(Table t, int fromRow, int toRow,
			int[] totalCols,int[] totalColsPercent, boolean isSubTotal, boolean isPercent , GroupArithmetic arith)
			throws ReportException {
		if (t == null || t.getRowCount() == 0) {
			return t;
		}
		Table tempTable = (Table) t.cloneAll(); // 用来生成总计
		int tempToRow = toRow; // 截止行号。由于会动态插入行，所以这个行号是要不断增大的。

		// 生成小计
		if (isSubTotal || isPercent) {
			int pointer = fromRow; // 扫描行时的指针
			int prePointer = pointer; // 指向当前组的第一个元素的指针
			while (pointer <= tempToRow) { // 从上至下扫描
				if (debug)
					System.out.println("p:" + pointer + " pre:" + prePointer
							+ " rowCount:" + t.getRowCount());
				TableColumn tc = t.getCol(0); // 由于t是动态增长的(后面要添加行),所以这个语句放在循环里
				if (pointer > 0) {
					// 如果到了最后一行,则得到一组
					if (pointer == t.getRowCount()) {

						TableRow insertTR = getTotalRow(t, prePointer, pointer,
									LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total":"小计", totalCols, arith);
							insertTR.setType(Report.GROUP_TOTAL_TYPE);
						if(isSubTotal){
							t.insertRow(pointer, insertTR);
						}

						//如果计算百分比
						if(isPercent){
							if(isSubTotal){
								tempToRow++; // 已经加了行，所以截止行号也要加1
								pointer++;
							}
							TableRow insertPercentTR = getTotalRowPercent(insertTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total Per":"小计百分比",t.getColCount(),totalColsPercent);
							insertPercentTR.setType(Report.GROUP_PERCENT_TYPE);
							t.insertRow(pointer, insertPercentTR);
						}
						if (debug)
							System.out.println("get one group while pre="
									+ prePointer + " and p=" + pointer);
						break; // 结束扫描
					}
					// 如果当前单元不等于前一行同列的单元，则得到一组
					else if (!ObjectUtils.equals(tc.getCell(pointer)
							.getContent(), tc.getCell(prePointer).getContent())) {

						TableRow insertTR = getTotalRow(t, prePointer, pointer,
								LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total":"小计", totalCols, arith);
							insertTR.setType(Report.GROUP_TOTAL_TYPE);
						if(isSubTotal){
							t.insertRow(pointer, insertTR);
						}
						
						//如果计算百分比
						if(isPercent){
							if(isSubTotal){
								tempToRow++; // 已经加了行，所以截止行号也要加1
								pointer++;
							}
							TableRow insertPercentTR = getTotalRowPercent(insertTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total Per":"小计百分比",t.getColCount(),totalColsPercent);
							insertPercentTR.setType(Report.GROUP_PERCENT_TYPE);
							t.insertRow(pointer, insertPercentTR);
						}

						if (debug)
							System.out.println("get one group while pre="
									+ prePointer + " and p=" + pointer);
						// 准备开始新一轮扫描。由于前面插入了一行，所以要跳到下一条的记录
						tempToRow++; // 已经加了行，所以截止行号也要加1
						pointer++;
						prePointer = pointer;
					}
				}
				pointer++;
			}
		}

		// 生成总计
		TableRow insertTotalTR = getTotalRow(tempTable, fromRow, toRow, LOCALE_EN.equalsIgnoreCase(this.locale)?"Total":"总计",
				totalCols, arith);
		insertTotalTR.setType(Report.TOTAL_TYPE);
		t.addRow(insertTotalTR);
		if(isPercent){
			TableRow insertTotalPercentTR = getTotalRowPercent(insertTotalTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Total Per":"总计百分比",t.getColCount(),totalColsPercent);
			insertTotalPercentTR.setType(Report.TOTAL_PERCENT_TYPE);
			t.addRow(insertTotalPercentTR);
		}
		return t;
	}

	private Table generateRowTotal(Table t, int fromRow, int toRow,
			int[] totalCols,int[] totalColsPercent, boolean isSubTotal, boolean isPercent , String[] arith)
			throws ReportException {
		if (t == null || t.getRowCount() == 0) {
			return t;
		}
		Table tempTable = (Table) t.cloneAll(); // 用来生成总计
		int tempToRow = toRow; // 截止行号。由于会动态插入行，所以这个行号是要不断增大的。

		// 生成小计
		if (isSubTotal) {
			int pointer = fromRow; // 扫描行时的指针
			int prePointer = pointer; // 指向当前组的第一个元素的指针
			while (pointer <= tempToRow) { // 从上至下扫描
				if (debug)
					System.out.println("p:" + pointer + " pre:" + prePointer
							+ " rowCount:" + t.getRowCount());
				TableColumn tc = t.getCol(0); // 由于t是动态增长的(后面要添加行),所以这个语句放在循环里
				if (pointer > 0) {
					// 如果到了最后一行,则得到一组
					if (pointer == t.getRowCount()) {
						TableRow insertTR = getTotalRow(t, prePointer, pointer,
								LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total":"小计", totalCols, arith);
						insertTR.setType(Report.GROUP_TOTAL_TYPE);
						t.insertRow(pointer, insertTR);

						//如果计算百分比
						if(isPercent){
							tempToRow++; // 已经加了行，所以截止行号也要加1
							pointer++;
							TableRow insertPercentTR = getTotalRowPercent(insertTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total Per":"小计百分比",t.getColCount(),totalColsPercent);
							insertPercentTR.setType(Report.GROUP_PERCENT_TYPE);
							t.insertRow(pointer, insertPercentTR);
						}
						if (debug)
							System.out.println("get one group while pre="
									+ prePointer + " and p=" + pointer);
						break; // 结束扫描
					}
					// 如果当前单元不等于前一行同列的单元，则得到一组
					else if (!ObjectUtils.equals(tc.getCell(pointer)
							.getContent(), tc.getCell(prePointer).getContent())) {
						TableRow insertTR = getTotalRow(t, prePointer, pointer,
								LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total":"小计", totalCols, arith);
						insertTR.setType(Report.GROUP_TOTAL_TYPE);
						t.insertRow(pointer, insertTR);
						
						//如果计算百分比
						if(isPercent){
							tempToRow++; // 已经加了行，所以截止行号也要加1
							pointer++;
							TableRow insertPercentTR = getTotalRowPercent(insertTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Sub Total Per":"小计百分比",t.getColCount(),totalColsPercent);
							insertPercentTR.setType(Report.GROUP_PERCENT_TYPE);
							t.insertRow(pointer, insertPercentTR);
						}

						if (debug)
							System.out.println("get one group while pre="
									+ prePointer + " and p=" + pointer);
						// 准备开始新一轮扫描。由于前面插入了一行，所以要跳到下一条的记录
						tempToRow++; // 已经加了行，所以截止行号也要加1
						pointer++;
						prePointer = pointer;
					}
				}
				pointer++;
			}
		}

		// 生成总计
		TableRow insertTotalTR = getTotalRow(tempTable, fromRow, toRow, LOCALE_EN.equalsIgnoreCase(this.locale)?"Total":"总计",
				totalCols, arith);
		insertTotalTR.setType(Report.TOTAL_TYPE);
		t.addRow(insertTotalTR);
		if(isPercent){
			TableRow insertTotalPercentTR = getTotalRowPercent(insertTotalTR,LOCALE_EN.equalsIgnoreCase(this.locale)?"Total Per":"总计百分比",t.getColCount(),totalColsPercent);
			insertTotalPercentTR.setType(Report.TOTAL_PERCENT_TYPE);
			t.addRow(insertTotalPercentTR);
		}
		return t;
	}

	/**
	 * 生成交叉表。
	 * 
	 * @param srcTab
	 *            原始数据表
	 * @param crossTab
	 *            交叉表定义对象。
	 * @return 结果表
	 */
	public Table generateCrossTab(Table srcTab, CrossTable crossTab,String isMergeZero,String isShowZero,String is_merge_vertical)
			throws ReportException {
		Table result = new Table();
		// 生成交叉表的行头和列头
		// 获得distinct的行头,如果有多个行头，则生成迪卡尔积
		Vector tempRH = new Vector(); // 保存行头信息
		for (int i = 0; i < crossTab.getRowHeader().length; i++) {
			tempRH.add(getDistinctSet(srcTab.getCol(crossTab.getRowHeader()[i]
					.getIndex()), crossTab.getRowHeader()[i].getSortSeq()));
		}
		// 获得distinct的列头
		Vector tempCH = new Vector(); // 保存列头信息
		for (int i = 0; i < crossTab.getColHeader().length; i++) {
			tempCH.add(getDistinctSet(srcTab.getCol(crossTab.getColHeader()[i]
					.getIndex()), crossTab.getColHeader()[i].getSortSeq()));
		}

		TableLine[] rowHead = getHeadForCross(tempRH, TableColumn.class);
		TableLine[] colHead = getHeadForCross(tempCH, TableRow.class);

		// 加行头；
		for (int i = 0; i < rowHead.length; i++) {
			result.addCol((TableColumn) rowHead[i]);
		}

		// 补齐空白列、数据区域；
		for (int i = 0; i < ((TableLine) colHead[0]).getCellCount(); i++) {
			result.addCol(new TableColumn(rowHead[0].getCellCount()));
		}

		// 再加列头；(要从后往前加,才能获得正确的顺序)
		for (int i = colHead.length - 1; i >= 0; i--) {
			TableRow tempTR = (TableRow) colHead[i];
			for (int j = 0; j < rowHead.length; j++) {
				TableCell cell = new TableCell("");
				tempTR.insertCell(cell, 0);
			}
			result.insertRow(0, tempTR);
		}

		// 按行头列头生成交叉部分数据
		for (int i = colHead.length; i < result.getRowCount(); i++) {
			for (int j = rowHead.length; j < result.getColCount(); j++) {
				String val = getCrossValByHead(srcTab, result, crossTab, i, j);
				TableCell cellStyle = getCrossCellByHead(srcTab, result, crossTab, i, j);
				if(cellStyle == null){
					cellStyle = result.getCell(i, j);
				}
				if(!"0.0".equals(val)&&!"0".equals(val)){
					cellStyle.setContent(val);
				}else{
					if("1".equals(isShowZero))
						cellStyle.setContent("0");
					else
						cellStyle.setContent("");
				}
				result.setCell(i, j, cellStyle);
			}
		}
		
		// 设置行头和列头的说明文字。
		for (int i = 0; i < colHead.length; i++) {
			for (int j = 0; j < rowHead.length; j++) {
				if (!(i == 0 && j == 0)) {
					result.getCell(i, j).setIsHidden(true);
				}
			}
		}
		
		//默认列都删除空列
		result = trimDataY(result,crossTab);
		//删除空行(先删空行,再合并分组,再输出交叉表头)
		if("1".equals(isMergeZero))
			result = trimDataX(result,crossTab);
		
		int[] cols = new int[colHead.length-1];	//标题列最后一行不合并
		int[] rows = new int[rowHead.length-1]; //标题行最后一列不合并
		int[] cols_rest = new int[result.getColCount()-rowHead.length];	//标题列最后一行不合并
		int[] rows_rest = new int[result.getRowCount()-colHead.length]; //标题行最后一列不合并
		for(int i=0;i<colHead.length-1;i++){
			cols[i]=i;
		}
		for(int i=0;i<rowHead.length-1;i++){
			rows[i]=i;
		}
		
		/*合并列头*/
		for(int i=rowHead.length;i<result.getColCount();i++){
			cols_rest[i-rowHead.length]=i;
		}

		/*合并行头*/
		for(int i=colHead.length;i<result.getRowCount();i++){
			rows_rest[i-colHead.length]=i;
		}
		
		if(rowHead.length>1){
			result = mergeCrossSameCells(result, rows, COLUMN_ORIENTATION,colHead.length);	//对列头进行合并,行头数为除重起始点
			if("1".equals(is_merge_vertical))
				result = mergeCrossSameCells(result, rows_rest, ROW_ORIENTATION,0,rowHead.length);
		}
		if(colHead.length>1){
			result = mergeCrossSameCells(result, cols, ROW_ORIENTATION,rowHead.length);  //对行头进行合并,列头数为除重起始点
			if("1".equals(is_merge_vertical))
				result = mergeCrossSameCells(result, cols_rest, COLUMN_ORIENTATION,0,colHead.length);
		}
		//按列的先后顺序,完成分组
		//result = split(result, cols);

		
		int len = crossTab.getColHeader().length
				+ crossTab.getRowHeader().length;

		// Table headHeadTable = new Table(len, len);//交叉表头的表头
		// headHeadTable.setType(Report.CROSS_HEAD_HEAD_TYPE);
		// headHeadTable.setBorder(0);
		// headHeadTable.setWidth(100);
		// headHeadTable.setHeight(100);
		// headHeadTable.setCellpadding(0);
		// headHeadTable.setCellspacing(0);
		// HeadCol[] hhVal = new HeadCol[len];
		// System.arraycopy(crossTab.getColHeader(), 0, hhVal, 0,
		// crossTab.getColHeader().length);
		// System.arraycopy(crossTab.getRowHeader(), 0, hhVal,
		// crossTab.getColHeader().length,
		// crossTab.getRowHeader().length);
		// for (int i = 0; i < len; i++) {
		// headHeadTable.getCell(i, len - 1 -
		// i).setContent(hhVal[i].getHeaderText());
		// }
		
		
		TableCell headHeadCell = result.getCell(0, 0);
		headHeadCell.setColSpan(rowHead.length);
		headHeadCell.setRowSpan(colHead.length);
		headHeadCell.setCssStyle("margin: 0px;padding: 0px;");
		headHeadCell.setCssClass(Report.CROSS_HEAD_HEAD_TYPE);
		headHeadCell.setContent(crossTab);
		

		// 在本方法完毕后，可以进行行、列汇总。

		return result;
	}
	
	/*删除空行
	 * 
	 * 
	 */

	public Table trimDataY(Table srcTab, CrossTable crossTab)
			throws ReportException {
		Table result = new Table();
		result = srcTab;
		//System.out.println();
		
		//先删列,再删除,不出错
		//删除空例
		for (int i = result.getColCount() - 1; i >= crossTab.getRowHeader().length; i--) {
			//TableRow tmpRow = result.getRow(i);
			boolean is_empty=false;
			
			for (int j = result.getRowCount()-1; j >= crossTab.getColHeader().length; j--) {
				//System.out.println("i:"+i+"j:"+j);
				if(!("".equals(result.getCell(j, i).toString())||"0.00".equals(result.getCell(j, i).toString())||"0.0".equals(result.getCell(j, i).toString())||"0".equals(result.getCell(j, i).toString()))){
					is_empty = false;
					break;
				}else{
					is_empty = true;
					//System.out.println("aa:"+result.getCell(j, i).toString());
				}
			}
			if(is_empty){
				for(int k = result.getRowCount()-1; k>=0; k--){
					TableCell tmpCell= result.getRow(k).getCell(i);
					result.getRow(k).deleteCell(tmpCell);
				}
				//TableRow tmpRow = result.getRow(k);
				//result.deleteRow(result.getRow(i));
			}
		}
		return result;
	}
	
	public Table trimDataX(Table srcTab, CrossTable crossTab)
			throws ReportException {
		Table result = new Table();
		result = srcTab;
		//System.out.println();
		
		//删除空行
		for (int i = result.getRowCount()-1; i >= crossTab.getColHeader().length; i--) {
			//TableRow tmpRow = result.getRow(i);
			boolean is_empty=false;
			
			for (int j = result.getColCount()-1; j >= crossTab.getRowHeader().length; j--) {
				//System.out.println("aa:"+result.getCell(i, j).toString());
				if(!("".equals(result.getCell(i, j).toString())||"0.00".equals(result.getCell(i, j).toString())||"0.0".equals(result.getCell(i, j).toString())||"0".equals(result.getCell(i, j).toString()))){
					is_empty = false;
					break;
				}else{
					is_empty = true;
				}
			}
			if(is_empty){
				/*for(int k = result.getColCount()-1; k>=0; k--){
					TableCell tmpCell= result.getRow(i).getCell(k);
					result.getRow(i).deleteCell(tmpCell);
				}*/
				result.deleteRow(result.getRow(i));
			}
		}
		
		return result;
	}
	/**
	 * 找出符合条件的行,返回所有行中要取的值。可以在子类覆盖，以使用别的查找算法。
	 * 
	 * @param t
	 *            原始数据表
	 * @param cols
	 *            列的序号
	 * @param cmpVals
	 *            进行比较的值，应该跟cols大小一样。1
	 * @param valueCol
	 *            要取得的值的列号.
	 * @return
	 */
	private String[] findRows(Table t, int[] cols, String[] cmpVals,
			int valueCol) throws ReportException {
		if (cols == null || cmpVals == null) {
			throw new ReportException("cols或cmpValues为空。");
		}
		if (cols.length != cmpVals.length) {
			throw new ReportException("cols和cmpVals的大小应该一致。");
		}
		Vector tmpResult = new Vector();
		for (int i = 0; i < t.getRowCount(); i++) {
			boolean pass = true;
			for (int j = 0; j < cols.length; j++) {
				if (!ObjectUtils.equals(t.getCell(i, cols[j]).getContent(),
						cmpVals[j])) {
					// 默认为通过，执行一票否决制
					pass = false;
					break;
				}
			}
			if (pass) {
				tmpResult.add(t.getRow(i).getCell(valueCol).getContent());
			}
		}
		String[] result = new String[tmpResult.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (String) tmpResult.elementAt(i);
		}

		return result;
	}

	private TableCell findTableCellRows(Table t, int[] cols, String[] cmpVals,
			int valueCol) throws ReportException {
		if (cols == null || cmpVals == null) {
			throw new ReportException("cols或cmpValues为空。");
		}
		if (cols.length != cmpVals.length) {
			throw new ReportException("cols和cmpVals的大小应该一致。");
		}
		for (int i = 0; i < t.getRowCount(); i++) {
			boolean pass = true;
			for (int j = 0; j < cols.length; j++) {
				if (!ObjectUtils.equals(t.getCell(i, cols[j]).getContent(),
						cmpVals[j])) {
					// 默认为通过，执行一票否决制
					pass = false;
					break;
				}
			}
			if (pass) {
				try{
					return (TableCell)t.getRow(i).getCell(valueCol).clone();
				}catch(CloneNotSupportedException e){
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 获得目标表中某行某列的交叉值。
	 * 
	 * @param srcTab
	 *            数据表
	 * @param destTab
	 *            目标表，是一个数据交叉表。
	 * @param crossTab
	 *            交叉表定义对象
	 * @param row
	 *            行号
	 * @param col
	 *            列号
	 * @return 行号列号的交叉值
	 * @throws ReportException
	 */
	private String getCrossValByHead(Table srcTab, Table destTab,
			CrossTable crossTab, int row, int col) throws ReportException {
		// 确定查找条件
		HeadCol[] headCols = crossTab.getColHeader();
		HeadCol[] headRows = crossTab.getRowHeader();

		String[] cmpVals = new String[headCols.length + headRows.length];
		int[] cols = new int[headCols.length + headRows.length];

		for (int i = 0; i < headCols.length; i++) {
			cols[i] = headCols[i].getIndex();
			cmpVals[i] = (String) destTab.getCell(i, col).getContent();

		}
		for (int i = 0; i < headRows.length; i++) {
			cols[i + headCols.length] = headRows[i].getIndex();

			cmpVals[i + headCols.length] = (String) destTab.getCell(row, i)
					.getContent();

		}

		// 找出符合条件的所有单元
		String[] values = findRows(srcTab, cols, cmpVals, crossTab
				.getCrossCol().getIndex());
		return crossTab.getCrossCol().getArith().getResult(values);

	}

	/**
	 * 传递TableCell样式
	 */
	private TableCell getCrossCellByHead(Table srcTab, Table destTab,
			CrossTable crossTab, int row, int col) throws ReportException {
		// 确定查找条件
		HeadCol[] headCols = crossTab.getColHeader();
		HeadCol[] headRows = crossTab.getRowHeader();

		String[] cmpVals = new String[headCols.length + headRows.length];
		int[] cols = new int[headCols.length + headRows.length];

		for (int i = 0; i < headCols.length; i++) {
			cols[i] = headCols[i].getIndex();
			cmpVals[i] = (String) destTab.getCell(i, col).getContent();

		}
		for (int i = 0; i < headRows.length; i++) {
			cols[i + headCols.length] = headRows[i].getIndex();

			cmpVals[i + headCols.length] = (String) destTab.getCell(row, i)
					.getContent();

		}

		// 找出符合条件的所有单元
		TableCell cell = findTableCellRows(srcTab, cols, cmpVals, crossTab.getCrossCol().getIndex());

		return cell;

	}

	/**
	 * 为交叉表生成行头或列头。
	 * 
	 * @param orglLine
	 *            原始数据序列。包含多个序列，每个序列都是一个不重复值的集合。
	 * @param cls
	 *            指定返回值数组中的类型。
	 * @return 行头或列头。
	 * @throws ReportException
	 */
	private TableLine[] getHeadForCross(Vector orglLine, Class cls)
			throws ReportException {
		TableLine[] lines = new TableLine[orglLine.size()];
		try {
			for (int i = 0; i < orglLine.size(); i++) {

				int span = 1; // 单元格的跨越数
				// 跨越数等于本序列后的所有序列包含单元数的乘积
				for (int j = i + 1; j < orglLine.size(); j++) {
					span *= ((Set) orglLine.elementAt(j)).size();
				}

				int repeat = 1; // 序列重复的次数。
				// 重复次数等于本序列之前所有序列包含单元数的乘积
				for (int j = 0; j < i; j++) {
					repeat *= ((Set) orglLine.elementAt(j)).size();
				}

				lines[i] = (TableLine) cls.newInstance();

				// 按重复数添加重复序列
				for (int j = 0; j < repeat; j++) {
					Iterator itr = ((Set) orglLine.elementAt(i)).iterator();
					while (itr.hasNext()) {
						String value = (String) itr.next();
						TableCell cell = new TableCell(value);
//						lines[i].setSpan(cell, span);		//为了删除空行列,暂时不合并
						lines[i].addCell(cell);
						// 创建隐藏的cell，补足cell的个数。
						for (int k = 0; k < span - 1; k++) {
							TableCell hiddenCell = new TableCell(value);
//							hiddenCell.setIsHidden(true);	//为了删除空行列,暂时不合并
							lines[i].addCell(hiddenCell);
						} // for
					} // while
				} // for
				lines[i].setType(Report.HEAD_TYPE); // 一定要放在所有单元都加入后再设置

				// 由于交叉表有翻转的方法，所以要将样式赋给单元
				for (int k = 0; k < lines[i].getCellCount(); k++) {
					lines[i].getCell(k).setCssClass(lines[i].getType());
				}

				if (debug)
					System.out.println("count:" + lines[i].getCellCount()
							+ " repeat:" + repeat + " span:" + span + " size:"
							+ ((Set) orglLine.elementAt(i)).size());
			} // for
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ReportException(ex.toString());
		}
		return lines;
	}
}