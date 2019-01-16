package com.ccms;

public interface Constants {

	/**
	 * web.xml配置参数
	 */
	public final static String JNDI_PREFIX = "jndi-prefix";
	public final static String DEF_DATASOURCE = "def-datasource";
	public final static String DEF_DBNAME = "def-dbname";
	public final static String DEF_LANGUAGE = "def-language";
	public final static String DEF_FORMAT_DATE = "def-format-date";
	public final static String DEF_INPUT_DATE = "def-input-date";
	
	public final static String DB_TYPE = "db";
	public final static String SEQ_NEXTVAL = "sequence-nextval";
	public final static String SEQ_CURRVAL = "sequence-currval";
	
	public final static String UPLOAD_DIR = "upload-dir";
	public final static String UPLOAD_SIZE = "upload-size";
	public final static String INDEX_PATH = "index-path";
	public final static String SYSTEM_USER = "system-user";
	
	public final static String OPTIONAL_YES = "0";
	public final static String OPTIONAL_NO = "1";
	
	/**
	 * 操作权限相关
	 */
	public final static String PRIVILEDGE_ADD = "A";
	public final static String PRIVILEDGE_SEARCH = "B";
	public final static String PRIVILEDGE_DETAIL = "C";
	public final static String PRIVILEDGE_UPDATE = "D";
	public final static String PRIVILEDGE_DELETE = "E";
	public final static String PRIVILEDGE_EXCEL = "F";
	public final static String PRIVILEDGE_PDF = "G";
	public final static String PRIVILEDGE_PAGE = "H";
	public final static String PRIVILEDGE_ATTACHMENT = "I";
	
	public final static String NO_PERMISION = "${lbl:error_no_permision}";
	
	/**
	 * 表单修改操作权限（0：只改本人；1：可以改他人；）
	 */
	public final static String OPERATION_TYPE_OWNER = "0";
	public final static String OPERATION_TYPE_OTHER = "1";
	
	
	/**
	 * 登录相关
	 */
	public final static String DINAMICA_SECURITY_LOGIN = "dinamica.security.login";
	public final static String DINAMICA_USER_LOCALE = "dinamica.user.locale";
}
