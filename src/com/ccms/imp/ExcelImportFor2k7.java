package com.ccms.imp;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ExcelImportFor2k7 extends DefaultHandler implements ExcelImportUtil{

	private SharedStringsTable sst;
	private String lastContents;
	private boolean nextIsString;

	private List<String> rowlist = new ArrayList<String>();
	private int curRow = 0;
	private int curCol = 0;
	
	//日期标志
	private boolean dateFlag;
	//数字标志
	private boolean numberFlag;
	//日期格式化
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private int lastIndex = -1;//保留上一个有值的单元格下标
	private String nowCell = null;//记录当前单元格的列名
	
	private List<List<String>> data = new ArrayList<List<String>>();
	
	/**
	 * 默认读取第一个工作簿
	 * @param path
	 */
	public ExcelImportFor2k7(File file) throws Throwable {
		OPCPackage pkg = OPCPackage.open(file);		
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
			
		XMLReader parser = fetchSheetParser(sst);
			
		InputStream sheet = r.getSheet("rId1");

		InputSource sheetSource = new InputSource(sheet);
		parser.parse(sheetSource);
			
		sheet.close();
		pkg.close();
	}
	
	/**
	 * 该方法自动被调用，每读一行调用一次，在方法中写自己的业务逻辑即可
	 * @param sheetIndex 工作簿序号
	 * @param curRow 处理到第几行
	 * @param rowList 当前数据行的数据集合
	 */
	public void optRow(int curRow, List<String> rowList) {
		data.add(curRow, rowList);
	}
	
	
	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}
	
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		// c => 单元格
		if (name.equals("c")) {
			String r = attributes.getValue("r");
			if(r != null){
				nowCell = r;
			}
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
			String cellType = attributes.getValue("t");
			if (cellType != null && cellType.equals("s")) {
				nextIsString = true;
			} else {
				nextIsString = false;
			}
			//日期格式
			String cellDateType = attributes.getValue("s");
			if ("1".equals(cellDateType)){
				dateFlag = true;
			} else {
				dateFlag = false;
			}
			String cellNumberType = attributes.getValue("s");
			if("2".equals(cellNumberType)){
				numberFlag = true;
			} else {
				numberFlag = false;
			}
		}
		// 置空
		lastContents = "";
	}
	
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// 根据SST的索引值的到单元格的真正要存储的字符串
		// 这时characters()方法可能会被调用多次
		if (nextIsString) {
			try {
				int idx = Integer.parseInt(lastContents);
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
			} catch (Exception e) {

			}
		}

		// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
		// 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
		if (name.equals("v")) {
			String value = lastContents.trim();
			try{
				//日期格式处理
				if(dateFlag){
					 Date date = DateUtil.getJavaDate(Double.valueOf(value));
					 value = sdf.format(date);
				}
				//数字类型处理
				if(numberFlag){
					BigDecimal bd = new BigDecimal(value);
					value = bd.setScale(3,BigDecimal.ROUND_UP).toString();
				}
			}catch(Exception e){
			}
			addValue(value);
		} else {
			// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
			if (name.equals("row")) {
				optRow(curRow, rowlist);
				rowlist = new ArrayList<String>();
				curRow++;
				curCol = 0;
				lastIndex = -1;
			}
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// 得到单元格内容的值
		lastContents += new String(ch, start, length);
	}

	public void addValue(String value){
		int index = getCellIndex(nowCell);
		if((index - 1) > lastIndex){//说明中间有空的单元格，需要先用空的内容填充
			for(int i=0;i<(index-1-lastIndex);i++){
				rowlist.add(curCol, null);
				curCol++;
			}
		}
		rowlist.add(curCol, value);
		curCol++;
		lastIndex = index;
	}
	
	public int getCellIndex(String cellName){
		Pattern p=Pattern.compile("\\d+");
		Matcher m=p.matcher(nowCell);
		if(m.find())
		{
			String localName = cellName.substring(0,cellName.indexOf(m.group()));
			//从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
			int index = -1;
		   	char[] cs = localName.toCharArray();
		   	for(int i=0;i<cs.length;i++)
		   	{
			   index += (cs[i]-64 ) * Math.pow(26, cs.length-1-i);
		   	}
			return index;
		}
		return -1;
	}
	
	@Override
	public List<List<String>> getExcelData() {
		return data;
	}
}
