package transactions.project.fitness.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import transactions.project.util.qrcode.QRCodeUtil;
import transactions.project.weixin.common.WeixinUtil;

import com.ccms.context.InitializerServlet;

import dinamica.Base64;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Marker;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class ErpTools {
	private static Logger logger = Logger.getLogger(ErpTools.class.getName());

	public static final String yes = "1";
	public static final String no = "0";
	
	/***
	 * reponse 输出
	 * @param response
	 * @param json
	 */
	public static void responseOut(HttpServletResponse response, String json) throws IOException {
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
		in = new WeixinUtil().getClass().getResourceAsStream(path);
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
		String jndiName = _jndiPrefix + _dataSource;
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
	
	/**
	 * 根据生日获取年龄
	 * @param birthday
	 * @return
	 */
	public static int getAge(Date birthday){
		Calendar curCal = Calendar.getInstance();
		int curYear = curCal.get(Calendar.YEAR);
		int curMonth = curCal.get(Calendar.MONTH) + 1;
		curCal.setTime(birthday);
		int birthYear = curCal.get(Calendar.YEAR);
		int birthMonth = curCal.get(Calendar.MONTH) + 1;
		int year = curYear - birthYear;
		if( curMonth >= birthMonth ){
			year++;
		}
		return year;
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
		String querySeq = getLocalResource("/transactions/project/medical/sql/query-seq.sql");
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
	 * 根据身份证获取生日
	 * @param idCard
	 * @return
	 */
	public static String getBirthdayByIdcard(String idCard){
		String birthday = "";
		if( StringUtils.isNotBlank(idCard) ){
			String str = "";
			try{
				if ( idCard.length() == 15 ){
					str = idCard.substring(6, 12);
					str = "19" + str;
				} else if( idCard.length() == 18 ) {
					str = idCard.substring(6, 14);
				}
				if( StringUtils.isNotBlank(str) ){
					birthday = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6);
				}
			}catch(Exception e){
				logger.error(e);
			}
		}
		return birthday;
	}
	
	/**
	 * 根据生日获取年龄
	 * @param birthday
	 * @return
	 */
	public static Integer getAgeByBirthday(String birthday){
		Integer age = null;
		if( StringUtils.isNotBlank(birthday) ){
			try{
				String year = birthday.substring(0, 4);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				age = Integer.parseInt(sdf.format(new Date())) - Integer.parseInt(year);
				age = ( age < 0 ? null : age );
			}catch(Exception e){
				logger.error(e);
			}
		}
		return age;
	}
	
	/***
	 * 生成word报告
	 * @param request
	 * @param tempatepath	word文档模板路径
	 * @param subpath	子目录
	 * @param mapAll	需要替换的Map对象
	 * @return
	 */
	public static String replaceWordContent(HttpServletRequest request, String tempatepath, String subpath, 
			Map<String, Object> mapAll, String pk_value) {
		String docPath = null;
		try {
			String rootPath = request.getSession().getServletContext().getRealPath("");
			String docTemplatePath = rootPath + tempatepath;	// word文档模板路径
			rootPath = rootPath + File.separator + "erpclubdoc" + File.separator;	// 文件根目录
			String dataPath = rootPath + "data" + File.separator + subpath + File.separator;	// 文件保存目录
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String userlogin = request.getRemoteUser();
			String baseName = sdf.format(new Date()) + "_" + pk_value + "_" + (null == userlogin ? "" : userlogin);
			String docOutPath = dataPath + baseName + ".docx";	// word文档保存路径
			
			WordUtil wordutil = new WordUtil();
			wordutil.generateWordFromTemplate(docTemplatePath, docOutPath, mapAll);

			// word转pdf
			docPath = Word2Pdf.convert(docOutPath, dataPath, baseName);
			if( StringUtils.isNotBlank(docPath) ){
				docPath = "/erpclubdoc/data/" + subpath + "/" + baseName + ".pdf";
				// 删除word
				File docFile = new File(docOutPath);
				if( docFile.exists() ){
					docFile.delete();
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
			logger.error(e);
		} catch (Throwable e){
			logger.error(e);
		}
		return docPath;
	}
	
	/**
	 * 给图片添加水印字
	 * @param srcImgPath 源图片路径
	 * @param targerPath 目标图片路径
	 * @param addStr 水印内容
	 * @param imgWidth 目标图片宽度
	 * @param imgHeight 目标图片宽度
	 * @param iconPositionList 水印图片位置
	 */
	public static void addMarkImage(String srcImgPath, String targerPath, String addStr, 
			Integer imgWidth, Integer imgHeight, List<Map<String, Integer>> iconPositionList) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));
			imgWidth = ( null == imgWidth || imgWidth == 0 ? srcImg.getWidth(null) : imgWidth );
			imgHeight = ( null == imgHeight || imgHeight == 0 ? srcImg.getHeight(null) : imgHeight );
			BufferedImage buffImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
			// 得到画笔对象
			Graphics2D g = buffImg.createGraphics();
			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), 0, 0, null);
			g.setColor(Color.RED);	//设置字体颜色
			g.setFont(new Font("黑体", Font.BOLD, 13));	//设置字体和字号
			// 表示水印图片的位置
			int len = ( iconPositionList != null && iconPositionList.size() > 0 ? iconPositionList.size() : 0 );
			for( int i = 0; i < len; i++ ){
				Map<String, Integer> map = iconPositionList.get(i);
				g.drawString(addStr, map.get("x"), map.get("y"));
			}
			g.dispose();
			os = new FileOutputStream(targerPath);
			// 生成图片
			ImageIO.write(buffImg, "JPG", os);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (null != os){
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public final static long WX_TEMP_QRCODE_expire_seconds = 120;	// 二维码有效时长
	/**
	 * 生成微信临时二维码
	 * @param sid
	 * @param pkvalue
	 * @param sceneid 场景值id：0会员卡 1私教课
	 * @param expire_seconds
	 * @param db
	 * @throws Throwable
	 */
	public static String createWXTempQrcode(String sid, String pkvalue, int sceneid, Long expire_seconds, Db db) throws Throwable{
		if( null == sid || "".equals(sid) ){
			throw new Throwable("提交参数sid不能为空");
		}
		expire_seconds = (null != expire_seconds && expire_seconds > 0 ? expire_seconds : 120);	// 默认120秒
		String scenestr = transactions.project.fitness.util.Base64.encodeMore(pkvalue, 2);
		String accessToken = WeixinUtil.getAccessTokenForService(db, sid);
		JSONObject resultJson = WeixinUtil.createTempQrcode(sceneid, scenestr, expire_seconds, accessToken);
		String ticket = (resultJson.has("ticket") ? resultJson.getString("ticket") : "");
		if( null == ticket || "".equals(ticket) ){
			throw new Throwable("微信二维码生成失败：" + resultJson);
		}
		return WeixinUtil.QRCODE_SHOW_URL + ticket;
	}
	
	
	/**
	 * 生成普通二维码
	 * @param request
	 * @param qrcode_value
	 * @param pk_value
	 * @param org_id
	 */
	public static String createCommonQrcode(HttpServletRequest request, String qrcode_value, String pk_value, Integer org_id) {
		String filepath = "";
		try{
			String headpath = null;
			if( null == qrcode_value || "".equals(qrcode_value) ){
				throw new Throwable("提交参数qrcode_value不能为空");
			}else if( null == org_id || "".equals(org_id) ){
				throw new Throwable("提交参数org_id不能为空");
			}
			// 二维码保存路径
			String rootPath = request.getSession().getServletContext().getRealPath("");
			String savePath = File.separator + "upload" + File.separator + "qrcode" + File.separator + org_id + File.separator;
			File file = new File(rootPath + savePath);
			file.mkdirs();
			file = null;
			
			String filename = pk_value + ".jpg";
			QRCodeUtil.encode(qrcode_value, headpath, rootPath + savePath, filename, true);
			filepath = savePath + filename;
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return filepath;
	}
	
	public static synchronized String syncFormatDate(SimpleDateFormat sdf, Date date) {
        return sdf.format(date);
    }
	
	public static Recordset getOrgList(Db db) throws Throwable{
		if( null == db ){
			throw new Throwable("Db can not be null.");
		}
		String queryOrg = getLocalResource("/transactions/project/fitness/job/sql/query-org.sql");
		Recordset rsOrg = db.get(queryOrg);
		return rsOrg;
	}
	
	public static String formatDate(Date date, String pattern){
		if( null == date || null == pattern || "".equals(pattern) ){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static Date parseDate(String dateStr, String pattern){
		if( null == dateStr || "".equals(dateStr) || null == pattern || "".equals(pattern) ){
			return null;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Integer getIntegerValue(Recordset rs, String fieldname) throws Throwable{
		Integer value = null;
		if( rs.containsField(fieldname) && !rs.isNull(fieldname) ){
			value = rs.getInteger(fieldname);
		}
		return value;
	}

	public static Double getDoubleValue(Recordset rs, String fieldname) throws Throwable{
		Double value = null;
		if( rs.containsField(fieldname) && !rs.isNull(fieldname) ){
			value = rs.getDouble(fieldname);
		}
		return value;
	}
	
	/***
	 * 生成随机串（大写+小写+数字）
	 * @param length
	 * @return
	 */
	public static String getStringRandom(int length){
		String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++){
			int number = random.nextInt(62);
			buf.append(str.charAt(number));
		}
		return buf.toString();
	}

	/***
	 * 生成随机串（小写+数字）
	 * @param length
	 * @return
	 */
	public static String getStringSimpleRandom(int length){
		String str = "zxcvbnmlkjhgfdsaqwertyuiop1234567890";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++){
			int number = random.nextInt(36);
			buf.append(str.charAt(number));
		}
		return buf.toString();
	}
	
	/***
	 * 生成随机串（大写+数字）
	 * @param length
	 * @return
	 */
	public static String getUpperStringRandom(int length){
		String str = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++){
			int number = random.nextInt(36);
			buf.append(str.charAt(number));
		}
		return buf.toString();
	}

	/***
	 * 生成随机串（数字）
	 * @param length
	 * @return
	 */
	public static String getNumberRandom(int length){
		String str = "1234567890";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < length; i++){
			int number = random.nextInt(10);
			buf.append(str.charAt(number));
		}
		return buf.toString();
	}
	
	public static String lpad(String input, String charstr, int targetLength){
		String output = input;
		if( null == output || output.length() >= targetLength ){
			return output;
		}
		int len = targetLength - output.length();
		for(int i = 0; i < len; i++){
			output = charstr + output;
		}
		return output;
	}
}

