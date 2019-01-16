package transactions.project.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.xml.xpath.XPathExpressionException;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import dinamica.AbstractExcelOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.xml.Document;
import dinamica.xml.Element;

/***
 * POI导出Excel
 * @author C
 * 2016-12-05
 */
public class POIExcelExport extends AbstractExcelOutput {

	Document doc = null;
	SimpleDateFormat dateFormat = null;
	
	int imgWidth = 0;		// 图片宽度超出单元格的宽度
	int imgHeight = 0;	// 图片高度超出单元格的高度
	int imgCols = 3;		// 单个图片所占列数
	int imgRows = 1;		// 单个图片所占行数
	int colWidth = 12;	// 列宽
	int rowHeight = 12;	// 行高
	boolean isTextWrap = false;	// 是否自动换行
	
	@Override
	public WritableWorkbook createWorkbook(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable {
		// obtener referencia a config.xml
		doc = getConfig().getDocument();

		// formato de la fecha
		String dateFmt = getContext().getInitParameter("def-format-date");
		String customDateFmt = doc.getElement("excel").getAttribute("date-format");
		if (customDateFmt == null) {
			customDateFmt = dateFmt;
		}
		dateFormat = new SimpleDateFormat(customDateFmt);
		
		imgWidth = this.getConfigIntegerValue("img-width", imgWidth);
		imgHeight = this.getConfigIntegerValue("img-height", imgHeight);
		imgCols = this.getConfigIntegerValue("img-cols", imgCols);
		imgRows = this.getConfigIntegerValue("img-rows", imgRows);
		colWidth = this.getConfigIntegerValue("cell-width", colWidth);
		rowHeight = this.getConfigIntegerValue("cell-height", rowHeight);
		isTextWrap = this.getConfigBooleanValue("text-wrap", isTextWrap);
		
		// elemento principal
		Element elemSheet = doc.getElement("excel");

		// 填充数据
		this.getExcelData(elemSheet, data, buf);

		WritableWorkbook wb = Workbook.createWorkbook(buf);
		wb.close();
		return wb;
	}

	@Override
	protected String getAttachmentString() {
		String fileName = "data.xls";
		try {
			fileName = getConfig().getDocument().getElement("excel").getAttribute("filename");
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				fileName = java.net.URLEncoder.encode(fileName,"utf-8"); 
				fileName = fileName.replaceAll("\\+", "%20"); //处理空格
			} else {   
				fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");   
			}
		} catch (XPathExpressionException e) {
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "attachment; filename=\"" + fileName + "\";";
	}

	/**
	 * 获取Excel内容
	 * @param elemSheet
	 * @param data
	 * @param buf
	 * @throws Throwable
	 */
	private void getExcelData(Element elemSheet, GenericTransaction data, ByteArrayOutputStream buf) throws Throwable {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(doc.getElement("excel").getAttribute("sheetname"));
		
		// obtener recordset de data
		Recordset rs1 = data.getRecordset(elemSheet.getAttribute("recordset"));
		Recordset rs = rs1.copy();

		// si tiene registros el recordset paginar a un tama�o de 65000
		// registros
		if (rs.getRecordCount() > 0){
			rs.setPageSize(64000);
		}

		// codigo que lee los nombres de las columnas, campos y recordset del
		// config.xml
		Element cols[] = doc.getElements(elemSheet);

		// 设置单元格样式
		HSSFCellStyle cellStyle = this.createCellStyle(wb);

		HSSFPatriarch patriarch = null;
		// recorrer cada paginas
		for (int j = 0; j < rs.getPageCount(); j++) {
			if (j >= 1) {
				sheet = wb.createSheet(elemSheet.getAttribute("sheetname") + j);
			}
			// 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
			patriarch = sheet.createDrawingPatriarch();
			// 添加表头
			int headerColnum = this.createExcelHeader(sheet, cols, wb);
			int curRow = 1;	// 当前图片开始行
			int nextRow = 1;	// 下一图片开始行
			// 添加数据
			HSSFRow row = null;
			int colnum = 0, rownum = 0;
			Recordset rsCopy = rs.getPage(j + 1);
			rsCopy.top();
			while (rsCopy.next()) {
				colnum = 0;
				rownum++;
				row = sheet.createRow(rownum); // 得到Excel工作表的行
				// Set the row's height or set to ff (-1) for undefined/default-height.
				// Set the height in "twips" or 1/20th of a point.
				row.setHeight((short) (rowHeight * 20));	// 设置行高
				for (int i = 0; i < cols.length; i++) {
					if (cols[i].getNodeName().equalsIgnoreCase("col")) {
						String colName = cols[i].getAttribute("id");
						HSSFCell cell = row.createCell(colnum);
						cell.setCellStyle(cellStyle);
						if (rsCopy.isNull(colName)) {
							cell.setCellValue("");
						} else {
							RecordsetField rf = rsCopy.getField(colName);
							switch (rf.getType()) {
							case java.sql.Types.DATE:
								String dateValue = dateFormat.format(rsCopy.getDate(colName));
								cell.setCellValue(dateValue);
								break;

							case java.sql.Types.INTEGER:	// numeric and integer
								Object integerValue = rsCopy.getValue(colName);
								cell.setCellValue(String.valueOf(integerValue));
								break;

							case java.sql.Types.DOUBLE:
								Double doubleValue = rsCopy.getDouble(colName);
								cell.setCellValue(doubleValue);
								break;

							default:
								String stringValue = rsCopy.getString(colName);
								cell.setCellValue(stringValue);
								break;
							}
						}
						colnum++;
					}
				}
				
				Recordset rsImg = rsCopy.getChildrenRecordset();
				if (null != rsImg && rsImg.getRecordCount() > 0) {
					int curCol = headerColnum;	// 当前图片开始列
					int nextCol = headerColnum;	// 下一图片开始列
					// 计算图片所在行
					curRow = nextRow;	// 当前图片开始行
					nextRow += imgRows;	// 下一图片开始行
					BufferedImage bufferImg = null;
					while (rsImg.next()) {
						ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
						bufferImg = ImageIO.read(new File(rsImg.getString("file_path")));
						ImageIO.write(bufferImg, "jpg", byteArrayOut);
						// 计算图片所在列
						curCol = nextCol;	// 当前图片开始列
						nextCol += imgCols;	// 下一图片开始列
						
						// anchor主要用于设置图片的属性
						HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, imgWidth, imgHeight, (short)curCol, (short)curRow, (short)nextCol, (short)nextRow);
						anchor.setAnchorType(3);
						// 插入图片
						patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
					}
				} else{
					// 计算图片所在行
					curRow++;	// 当前图片开始行
					nextRow++;	// 下一图片开始行
				}
			}
		}
		wb.write(buf); // 写入excel文件
	}
	
	/**
	 * 创建单元格样式
	 * @param wb
	 * @return
	 */
	private HSSFCellStyle createCellStyle(HSSFWorkbook wb){
		// 设置单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 设置边框
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN); // 左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN); // 上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); // 右边框
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 左右居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);	// 垂直居中
		cellStyle.setWrapText(isTextWrap); // 设置自动换行，默认为false不换行
		return cellStyle;
	}
	
	/**
	 * 添加表头
	 * @param sheet
	 * @param cols
	 * @param cellStyle
	 * @return 列数
	 */
	private int createExcelHeader(HSSFSheet sheet, Element cols[], HSSFWorkbook wb){
		HSSFCellStyle cellStyle = this.createCellStyle(wb);
		cellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);	// 设置表头背景色
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// 添加表头
		HSSFRow row = sheet.createRow(0); // 得到Excel工作表的行
		// obtener todos los label para definir las etiquetas del config.xml
		int colnum = 0;
		for (int i = 0; i < cols.length; i++) {
			if (cols[i].getNodeName().equalsIgnoreCase("col")) {
				HSSFCell cell = row.createCell(colnum);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(cols[i].getAttribute("label"));
				// (in units of 1/256th of a character width)
				sheet.setColumnWidth(colnum, colWidth*256);	// 设置列宽
				colnum++;
			}
		}
		return colnum;
	}
	
	/**
	 * 获取xml配置参数
	 * @param paramname
	 * @param defaultValue
	 * @return
	 * @throws Throwable
	 */
	private int getConfigIntegerValue(String paramname, Integer defaultValue) throws Throwable{
		Integer value = defaultValue;
		String str = getConfig().getConfigValue(paramname, "");
		if( StringUtils.isNotBlank(str) ){
			try{
				value = Integer.parseInt(str);
			}catch(NumberFormatException ex){
			}
		}
		return value;
	}
	
	/**
	 * 获取xml配置参数
	 * @param paramname
	 * @param defaultValue
	 * @return
	 * @throws Throwable
	 */
	private boolean getConfigBooleanValue(String paramname, Boolean defaultValue) throws Throwable{
		Boolean value = defaultValue;
		String str = getConfig().getConfigValue(paramname, "");
		if( StringUtils.isNotBlank(str) ){
			try{
				value = Boolean.parseBoolean(str);
			}catch(Exception ex){}
		}
		return value;
	}
}
