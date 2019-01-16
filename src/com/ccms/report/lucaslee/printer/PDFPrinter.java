package com.ccms.report.lucaslee.printer;

import java.io.IOException;
import java.io.OutputStream;

import com.ccms.report.lucaslee.CssEngine;
import com.ccms.report.lucaslee.ReportException;
import com.ccms.report.lucaslee.model.Rectangle;
import com.ccms.report.lucaslee.model.Report;
import com.ccms.report.lucaslee.model.ReportBody;
import com.ccms.report.lucaslee.model.Table;
import com.ccms.report.lucaslee.model.TableCell;
import com.ccms.report.lucaslee.model.TableRow;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;

/**
 * PDF格式打印机。
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
 * Company:Lucas-lee Soft
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class PDFPrinter implements com.ccms.report.lucaslee.Printer {

	/**
	 * 默认字体
	 */
	private Font defaultFont;

	public PDFPrinter() throws DocumentException, IOException {
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		defaultFont = new Font(bfChinese, 12);

	}

	/**
	 * 打印表格单元。
	 * 
	 * @param tableCell
	 *            表格单元
	 * @param css
	 *            样式表
	 * @param table
	 *            itext表格
	 * @param haveBorder
	 *            是否有边框
	 * @throws BadElementException
	 * @throws com.lowagie.text.DocumentException
	 * @throws IOException
	 * @throws ReportException
	 */
	private void print(TableCell tableCell,
			PDFCss css, com.lowagie.text.Table table, boolean haveBorder)
			throws BadElementException, com.lowagie.text.DocumentException,
			IOException, ReportException {

		if (tableCell.getIsHidden() == false) {
			Cell cell = null;

			// 应用样式表
			PDFCssItem item = null;
			if (Report.DATA_TYPE.equals(tableCell.getCssClass())) {
				item = css.getData();
			} else if (Report.GROUP_TOTAL_TYPE.equals(tableCell.getCssClass())) {
				item = css.getGroupTotal();
			} else if (Report.HEAD_TYPE.equals(tableCell.getCssClass())) {
				item = css.getHead();
			} else if (Report.TITLE_TYPE.equals(tableCell.getCssClass())) {
				item = css.getTitle();
			} else if (Report.TOTAL_TYPE.equals(tableCell.getCssClass())) {
				item = css.getTotal();
			} else if (Report.CROSS_HEAD_HEAD_TYPE.equals(tableCell
					.getCssClass())) {
				item = css.getCrossHeadHead();
			}
			if (item != null) {
				cell = getCell(tableCell, item.getFont());
				cell.setBackgroundColor(item.getBackgroudColor());
			} else {
				cell = getCell(tableCell, defaultFont);
			}
			cell.setHorizontalAlignment(getAlign(tableCell.getAlign()));

			if (Report.CROSS_HEAD_HEAD_TYPE.equals(tableCell.getCssClass())) {
				cell.setVerticalAlignment(com.lowagie.text.Table.ALIGN_MIDDLE);
			} else {
				cell.setVerticalAlignment(getVAlign(tableCell.getValign()));
			}

			cell.setColspan(tableCell.getColSpan());
			cell.setRowspan(tableCell.getRowSpan());

			// 设置是否允许换行。（itext的setNoWrap有bug，不用它。）
			if (tableCell.getNoWrap() == true
					&& Report.CROSS_HEAD_HEAD_TYPE.equals(tableCell
							.getCssClass())) {
				cell.setMaxLines(1);
			} else {
				cell.setMaxLines(Integer.MAX_VALUE);
			}
			if (haveBorder == false) {
				cell.setBorder(Cell.NO_BORDER);
			}

			table.addCell(cell);
		}

	}

	/**
	 * 从report的TableCell获得itext的Cell对象。根据tc的CssClass不同做处理。
	 * 
	 * @param tc
	 *            TableCell
	 * @param font
	 *            pdf字体
	 * @return itext的Cell对象
	 * @throws BadElementException
	 */
	private Cell getCell(TableCell tc, Font font) throws BadElementException {
		Phrase phrase = new Phrase();
		Cell c = null;
		if (Report.CROSS_HEAD_HEAD_TYPE.equals(tc.getCssClass())) {// 是交叉表表头的表头

			String[] strs = PrinterUtil.getCrossHeadHeadContent(tc);
			String blank = "";
			for (int i = strs.length - 1; i >= 0; i--) {
				blank = " ";
				if (strs[i] != null) {
					Chunk chunk = new Chunk(strs[i] + blank, font);
					chunk.setTextRise((float) -1.7 * i + 4);
					phrase.add(chunk);
				}
			}
			c = new Cell(phrase);
		} else {
			phrase = new Phrase((String) tc.getContent(), font);
			c = new Cell(phrase);
		}
		return c;
	}

	/**
	 * 打印表格行。
	 * 
	 * @param tableRow
	 *            表格行
	 * @param css
	 *            样式表
	 * @param table
	 *            itext表格
	 * @param haveBorder
	 *            是否有边框
	 * @throws BadElementException
	 * @throws DocumentException
	 * @throws IOException
	 * @throws ReportException
	 */
	private void print(TableRow tableRow, PDFCss css,
			com.lowagie.text.Table table, boolean haveBorder)
			throws BadElementException, DocumentException, IOException,
			ReportException {
		for (int j = 0; j < tableRow.getCellCount(); j++) {
			print(tableRow.getCell(j), css, table, haveBorder);
		}
	}

	/**
	 * 获得水平对齐方式
	 * 
	 * @param i
	 *            水平对齐常数
	 * @return
	 */
	private int getAlign(int i) {
		switch (i) {
		case Rectangle.ALIGN_LEFT:
			return com.lowagie.text.Table.ALIGN_LEFT;
		case Rectangle.ALIGN_CENTER:
			return com.lowagie.text.Table.ALIGN_CENTER;
		case Rectangle.ALIGN_RIGHT:
			return com.lowagie.text.Table.ALIGN_RIGHT;
		default:
			throw new RuntimeException("无法识别的ALIGN参数。");
		}
	}

	/**
	 * 获得垂直对齐方式
	 * 
	 * @param i
	 *            垂直对齐方式常数
	 * @return
	 */
	private int getVAlign(int i) {
		switch (i) {
		case Rectangle.VALIGN_TOP:
			return com.lowagie.text.Table.ALIGN_TOP;
		case Rectangle.VALIGN_MIDDLE:
			return com.lowagie.text.Table.ALIGN_MIDDLE;
		case Rectangle.VALIGN_BOTTOM:
			return com.lowagie.text.Table.ALIGN_BOTTOM;
		default:
			throw new RuntimeException("无法识别的VALIGN参数。");
		}

	}

	/**
	 * 设置矩形的多个参数
	 * 
	 * @param itextRect
	 * @param reportRect
	 */
	private void setMultiParam(com.lowagie.text.Rectangle itextRect,
			Rectangle reportRect) {
		itextRect.setBorderWidth(reportRect.getBorder() - 1); // 因为pdf的边框比较粗
		itextRect.setBorderColor(reportRect.getBordercolor());
		itextRect.setBackgroundColor(reportRect.getBackgroudColor());
	}

	/**
	 * 打印报表到输出流
	 * 
	 * @param r
	 *            报表
	 * @param result
	 *            输出流
	 * @throws ReportException
	 * @throws IOException
	 */
	public void print(Report r, OutputStream result)
			throws ReportException, IOException {
		print(r, new PDFCss(), result);
	}

	/**
	 * 打印报表到输出流
	 * 
	 * @param r
	 *            报表
	 * @param css
	 *            样式表
	 * @param result
	 *            输出流
	 * @throws ReportException
	 * @throws IOException
	 */
	public void print(Report r, PDFCss css,
			OutputStream result) throws ReportException, IOException {
		try {
			Document document = new Document();
			setHeaderFooter(document); // 必须在document.open之前设置
			document.open();

			if (r.getHeaderTable() != null) {
				print(r.getHeaderTable(), css, document);
			}
			if (r.getBody() != null) {
				print(r.getBody(), css, document);
			}
			if (r.getFooterTable() != null) {
				print(r.getFooterTable(), css, document);
			}
			document.close(); // 关闭文档
		} catch (DocumentException ex) {
			ex.printStackTrace();
			throw new ReportException(ex.getMessage());
		}
	}

	/**
	 * 设置首注，脚注。
	 * 
	 * @param document
	 */
	private void setHeaderFooter(Document document) {
		HeaderFooter footer = new HeaderFooter(new Phrase("页码：", defaultFont),
				true);
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(com.lowagie.text.Rectangle.TOP);
		document.setFooter(footer);
	}

	/**
	 * 打印报表主体到输出流
	 * 
	 * @param body
	 *            报表主体
	 * @param css
	 *            样式表
	 * @param document
	 *            itext文档对象
	 * @throws ReportException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void print(ReportBody body, PDFCss css,
			Document document) throws ReportException, IOException,
			DocumentException {
		Table data = body.getData().cloneAll();
		Table header = body.getTableColHeader();
		header = CssEngine.applyCss(header);
		if (header != null) {
			for (int i = header.getRowCount() - 1; i >= 0; i--) {
				data.insertRow(0, header.getRow(i));
			}
		}
		print(data, css, document);

	}

	/**
	 * 打印表格
	 * 
	 * @param t
	 *            表格
	 * @param css
	 *            样式表
	 * @param document
	 *            pdf文档对象
	 * @return
	 * @throws ReportException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void print(Table t, PDFCss css,
			Document document) throws ReportException, IOException,
			DocumentException {
		// 将样式属性解释到每个单元格
		t = CssEngine.applyCss(t);

		// 表格
		// 确定表格的列数
		com.lowagie.text.Table table = new com.lowagie.text.Table(t
				.getColCount());
		if (t.getWidths() != null)
			table.setWidths(t.getWidths());
		table.setWidth(t.getWidth());
		table.setSpacing(t.getCellspacing());
		table.setPadding(t.getCellpadding());
		table.setAlignment(getAlign(t.getAlign()));
		setMultiParam(table, t);

		boolean haveBorder = false;
		if (t.getBorder() <= 0) {
			haveBorder = false;
		} else {
			haveBorder = true;
		}
		for (int i = 0; i < t.getRowCount(); i++) {
			print(t.getRow(i), css, table, haveBorder);
		}
		document.add(table);
	}

}
