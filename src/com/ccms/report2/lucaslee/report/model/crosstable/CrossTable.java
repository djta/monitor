package com.ccms.report2.lucaslee.report.model.crosstable;

/**
 * 定义一个交叉表。
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Lucas lee
 * @version 1.0
 */
public class CrossTable {
	public CrossTable() {
	}

	/**
	 * 
	 * @param colHeader
	 *            列头
	 * @param rowHeader
	 *            行头
	 * @param crossCol
	 *            交叉部分
	 */
	public CrossTable(HeadCol[] colHeader, HeadCol[] rowHeader,
			CrossCol crossCol) {
		this.colHeader = colHeader;
		this.rowHeader = rowHeader;
		this.crossCol = crossCol;
	}

	/**
	 * 列头。
	 */
	private HeadCol[] colHeader;

	/**
	 * 行头。
	 */
	private HeadCol[] rowHeader;

	/**
	 * 交叉部分。
	 */
	private CrossCol crossCol;

	/**
	 * 获得行头。
	 * 
	 * @return
	 */
	public HeadCol[] getRowHeader() {
		return rowHeader;
	}

	/**
	 * 获得交叉部分
	 * 
	 * @return
	 */
	public CrossCol getCrossCol() {
		return crossCol;
	}

	/**
	 * 获得列头
	 * 
	 * @return
	 */
	public HeadCol[] getColHeader() {
		return colHeader;
	}

	/**
	 * 设置行头
	 * 
	 * @param rowHeader
	 */
	public void setRowHeader(HeadCol[] rowHeader) {
		this.rowHeader = rowHeader;
	}

	/**
	 * 设置交叉部分
	 * 
	 * @param crossCol
	 */
	public void setCrossCol(CrossCol crossCol) {
		this.crossCol = crossCol;
	}

	/**
	 * 设置列头
	 * 
	 * @param colHeader
	 */
	public void setColHeader(HeadCol[] colHeader) {
		this.colHeader = colHeader;
	}

}