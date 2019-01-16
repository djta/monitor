package com.ccms.imp;

import java.util.List;

//excel导入接口
public interface ExcelImportUtil{
	
	public final static String ERROR_FILE_NULL = "导入文件不能为空!";
	public final static String ERROR_FILE_EMPTY = "excel文件内容为空!";
	public final static String ERROR_TEMPLATE_FILE_EMPTY = "模版excel文件内容为空!";
	public final static String ERROR_FORMAT_ILLEGAL = "不支持的文件类型";
	
	public final static String ERROR_TITLE_NULL = "标题行列头(%1$d列)不能为空，请检查！";
	public final static String ERROR_TITLE_REPEAT = "标题行列头(%1$)有重复，请检查！";
	public final static String ERROR_PK_REPEAT = "主键别名(%1$)不能重复，请检查！";
	public final static String ERROR_PK_EXISTS = "主键别名(%1$)在标题行列头已存在，请检查！";
	
	public final static String INFO_NO_MAPPING = "(无映射)";
	
	public final static String ERROR_NOT_NULL = "值不能为空";
	public final static String ERROR_LENGTH_OUT = "长度不能大于%1$d";
	public final static String ERROR_DATE_ILLEGAL = "日期格式不正确";
	public final static String ERROR_DATETIME_ILLEGAL = "日期时间格式不正确";
	public final static String ERROR_INTEGER_ILLEGAL = "整数格式不正确";
	public final static String ERROR_DOUBLE_ILLEGAL = "数字格式不正确";
	public final static String ERROR_NAMESPACE_ILLEGAL = "参数值(%1$)匹配不到";
	public final static String ERROR_FK_ILLEGAL = "外键关联参数值(%1$)匹配不到";
	
	public final static Long MAX_LENGTH_2K3 = 5*1024*1024l;
	public final static String ERROR_MAX_SIZE_ILLEGAL = "当文件大于5M时，请转换成2007格式导入";
	
	public List<List<String>> getExcelData();
}
