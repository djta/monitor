package transactions.project.util.qcloud.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ccms.context.InitializerServlet;

import dinamica.Base64;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Marker;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class QCloudTools {
	/***
	 * reponse 输出
	 * @param response
	 * @param json
	 */
	public static void responseOut(HttpServletResponse response, Object json) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(json);
		} catch (IOException e) {
			throw e;
		}
	}

	/*
	 * 获取指定位置文件
	 */
	public static String getLocalResource(String path) throws Throwable {
		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];
		InputStream in = null;
		in = new QCloudTools().getClass().getResourceAsStream(path);
		try {
			if (in != null) {
				while (true) {
					int len = in.read(data);
					if (len != -1) {
						buf.append(new String(data, 0, len));
					} else {
						break;
					}
				}
				return buf.toString();
			} else {
				throw new Throwable("Invalid path to resource: " + path);
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	/**
	 * 获取数据连接
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	public static Db getDB(HttpServletRequest request) throws Throwable{
		String _dataSource = request.getServletContext().getInitParameter("def-datasource");
		String _jndiPrefix = request.getServletContext().getInitParameter("jndi-prefix");
		return getDB(_jndiPrefix, _dataSource);
	}
	
	public static Db getDB(String jndiPrefix, String dataSource) throws Throwable{
		String jndiName = jndiPrefix + dataSource;
		DataSource _ds = null;
		Connection con = null;
		Db db = null;
		try {
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
			db = new Db(con);
			return db;
		} catch (Throwable e1) {
			throw e1;
		}
	}
	
	/**
	 * 获取request body内容
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestBody(HttpServletRequest request) throws IOException {
		BufferedReader br = null;
		String inputLine = null;
		String str = "";
		try {
			request.setCharacterEncoding("UTF-8");
			br = request.getReader();
			while ((inputLine = br.readLine()) != null) {
				str += inputLine;
			}
		    return str;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if( null != br ){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getSQL(String _template, Recordset rs, HttpServletRequest request) throws Throwable{
		try{
			ServletContext _ctx = InitializerServlet.getContext();
			TemplateEngine t = new TemplateEngine(_ctx, request, _template);
			return t.getSql(rs);
		}catch (Throwable e){
			String msg = "[TemplateEngine].\n Template:" + _template + "\n";
			String data = "";
			if (rs!=null){
				data = rs.toString();
				System.err.println(msg + data);
			}
			throw e;
		}
	}
	
	protected static String replaceDefaultValues(String _template) throws Throwable{
		if ( _template.indexOf("${def:") < 0 ){
			return _template;
		}
		String markers[] = {"${def:date}", "${def:time}", "${def:timestamp}"};
		String values[] = new String[markers.length];
		java.util.Date d = new java.util.Date();
		values[0] = StringUtil.formatDate(d, "yyyy-MM-dd");
		values[1] = StringUtil.formatDate(d, "HH:mm:ss");
		values[2] = StringUtil.formatDate(d, "yyyy-MM-dd HH:mm:ss.SSS");
		for (int i=0;i<markers.length;i++){
			_template = StringUtil.replace(_template,markers[i],values[i]);
		}
		return _template;
	}
	
	protected static ArrayList<Marker> getMarkers(String _template, String prefix) throws Throwable{	
		int pos = 0;
		ArrayList<Marker> l = new ArrayList<Marker>();
		
		/* search markers */
		while ( pos >= 0 ){
			int pos1 = 0;
			int pos2 = 0;
			int newPos = 0;
			
			/* find start of marker */
			pos1 = _template.indexOf("${" + prefix + ":", pos);
			if (pos1>=0){
				/* find end of marker */
				newPos = pos1 + 6;
				pos2 = _template.indexOf("}", newPos);
				
				if (pos2>0){
					/* get marker string */
					String fld = _template.substring(newPos, pos2);
					Marker m = new Marker(fld,null,pos1,pos2);
					
					/* search for etra attribute (format or sequence name) */
					int pos3 = fld.indexOf("@");
					if (pos3>0){
						String name = fld.substring(0, pos3);
						String extraInfo = fld.substring(pos3 + 1, fld.length());
						
						if ( (name.indexOf(" ")>=0) || (name.indexOf("\r")>=0) || (name.indexOf("\n")>=0) || (name.indexOf('\t')>=0) ){
							String args[] = {name};
							String msg = "Invalid Marker ID - must be a contiguous string of letters and numbers, no spaces or special characters: {0}";
							msg = MessageFormat.format(msg, (Object[])args);
							throw new Throwable(msg);
						}
						m.setName(name);
						m.setExtraInfo(extraInfo);
					}
					l.add(m);
				}else{
					throw new Throwable( "Marker is not properly closed with with a brace '}'." );
				}
				pos = pos2 + 1;
			}else{
				pos = -1;
			}
		}
		return l;
	}
	
	public static String getRealSavePath(HttpServletRequest request, String parentPath, String subPath, boolean bPersist) {
		// 获得物理路径
		String strCurPath = request.getSession().getServletContext().getRealPath("/");
		String fileSeperator = System.getProperty("file.separator");
		int nPosSeperator = strCurPath.lastIndexOf(fileSeperator);
		String rootPath = strCurPath.substring(0, nPosSeperator);

		if (bPersist) {
			nPosSeperator = rootPath.lastIndexOf(fileSeperator);
			rootPath = strCurPath.substring(0, nPosSeperator);
		}

		// 组织路径
		String totalParentPath = null;
		String totalSubPath = null;
		if ((parentPath != null) && (parentPath.length() > 0)) {
			totalParentPath = rootPath + fileSeperator + parentPath;
			File file = new File(totalParentPath);
			file.mkdirs();
			file = null;

			totalSubPath = rootPath + fileSeperator + parentPath;
			if ((subPath != null) && (subPath.length() > 0)) {
				totalSubPath = totalSubPath + fileSeperator + subPath;
			}
			file = new File(totalSubPath);
			file.mkdirs();
			file = null;
		} else {
			totalSubPath = rootPath;
			if ((subPath != null) && (subPath.length() > 0)) {
				totalSubPath = totalSubPath + fileSeperator + subPath;
			}
			File file = new File(totalSubPath);
			file.mkdirs();
			file = null;
		}
		// 创建路径
		String savePath = totalSubPath + fileSeperator;
		return savePath;
	}

	/***
	 * 保存图片并返回路径
	 * @param request
	 * @param userlogin
	 * @param value
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public static String uploadImage(HttpServletRequest request, String userlogin, String value, String folderName) throws IOException{
		String url = "";
        Date curDate = new Date();
        try{
			// 保存签名图片
			String savePath = getRealSavePath(request, folderName, null, false);
			if ("\\".equals(File.separator)) {
				savePath = StringUtil.replace(savePath, "\\", "\\\\");
			}
			@SuppressWarnings("unused")
			int bytesum = 0;
	        int byteread = 0;
	        String fileName = userlogin + "_" + String.valueOf(curDate.getTime())+"_"+String.valueOf(Math.random()) + ".jpg";
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(value));
			@SuppressWarnings("resource")
			FileOutputStream fs = new FileOutputStream( savePath + fileName );
			byte[] buffer = new byte[ 1024 * 100];
			while ( ( byteread = bais.read( buffer ) ) != -1 ){
				bytesum += byteread; //字节数 文件大小
				fs.write( buffer, 0, byteread );
			}
			url = request.getContextPath() + File.separator + folderName + File.separator + fileName;
			return url;
        }catch(IOException e){
        	throw e;
        }
	}

	public static Integer getSeq(Db db, String seqName, HttpServletRequest request) throws Throwable{
		String querySeq = "SELECT ${seq:nextval@${seq_name}} AS seq FROM dual";
		querySeq = StringUtils.replace(querySeq, "${seq_name}", seqName);
		querySeq = getSQL(querySeq, null, request);
		Recordset rs = db.get(querySeq);
		rs.first();
		return rs.getInteger("seq");
	}
	
	public static String replaceRemark(String input){
		String output = "";
		if( StringUtils.isNotBlank(input) ){
			String regex = "<!--(.*)-->";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			output = matcher.replaceAll("");
		}
		return output;
	}
	
	public static String replaceBlank(String input){
		String output = "";
		if( StringUtils.isNotBlank(input) ){
			output = input.replaceAll("[\\t\\n\\r]", "");
		}
		return output;
	}

	/**
	 * 查询返回JSONArray
	 * @param db
	 * @param sql
	 * @return
	 * @throws Throwable
	 */
	public static JSONArray getJSONArrayData(Db db, String sql) throws Throwable{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONArray arr = new JSONArray();
		Recordset rs = db.get(sql);
		while(rs.next()){
			Recordset _rs = rs.getMetaData();
			_rs.top();
			JSONObject json = new JSONObject();
			while(_rs.next()){
				String colName = _rs.getString("name");
				int typeid = _rs.getInteger("typeid");
				String colValue = "";
				if( !rs.isNull(colName) ){
					if( typeid == Types.TIMESTAMP || typeid == Types.DATE ){
						colValue = sdf.format(rs.getDate(colName));
					} else if( typeid == Types.INTEGER || typeid == Types.NUMERIC ) {
						colValue = String.valueOf(rs.getValue(colName));
					} else {
						colValue = String.valueOf(rs.getValue(colName));
					}
				}
				json.put(colName, colValue);
			}
			arr.put(json);
		}
		return arr;
	}
	
	/**
	 * 查询返回JSONObject
	 * @param db
	 * @param sql
	 * @return
	 * @throws Throwable
	 */
	public static JSONObject getJSONObjectData(Db db, String sql) throws Throwable{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json = new JSONObject();
		Recordset rs = db.get(sql);
		while(rs.next()){
			Recordset _rs = rs.getMetaData();
			_rs.top();
			while(_rs.next()){
				String colName = _rs.getString("name");
				int typeid = _rs.getInteger("typeid");
				String colValue = "";
				if( !rs.isNull(colName) ){
					if( typeid == Types.TIMESTAMP || typeid == Types.DATE ){
						colValue = sdf.format(rs.getDate(colName));
					} else if( typeid == Types.INTEGER || typeid == Types.NUMERIC ) {
						colValue = String.valueOf(rs.getValue(colName));
					} else {
						colValue = String.valueOf(rs.getValue(colName));
					}
				}
				json.put(colName, colValue);
			}
			break;
		}
		return json;
	}
	
	/**
	 * 根据key获取json值
	 * @param json
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public static String getJsonValue(JSONObject json, String key) throws JSONException{
		String value = null;
		if( !json.isNull(key) ){
			value = String.valueOf(json.get(key));
		}
		return value;
	}
	
	/**
	 * 判断json值是否为空
	 * @param json
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public static boolean isJsonStringValueEmpty(JSONObject json, String key) throws JSONException{
		String value = getJsonValue(json, key);
		return StringUtils.isBlank(value);
	}
}

