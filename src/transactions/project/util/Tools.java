package transactions.project.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class Tools {

	private static String replace(String pos){
  		pos = StringUtil.replace(pos, "{", "");
  		pos = StringUtil.replace(pos, "}", "");
  		pos = pos.trim();
  		return pos;
  	}
	
	private static double EARTH_RADIUS = 6378.137*1000;	// M
  	private static double rad(double d) {
  		return d * Math.PI / 180.0;
  	}
  	/**
  	 * 获取2个坐标点之间的距离M
  	 * @param pos1
  	 * @param pos2
  	 * @return
  	 */
  	public static double getDistance(String longitude, String latitude, String pos2) {
  		pos2 = replace(pos2);
  		double x1 = Double.parseDouble(longitude);
  		double y1 = Double.parseDouble(latitude);
  		double x2 = Double.parseDouble(pos2.split(",")[0]);
  		double y2 = Double.parseDouble(pos2.split(",")[1]);
  		double radLat1 = rad(y1);
  		double radLat2 = rad(y2);
  		double a = radLat1 - radLat2;
  		double b = rad(x1) - rad(x2);
  		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
  		s = s * EARTH_RADIUS;
  		s = Math.round(s * 10000) / 10000;
  		return s;
    }
  	
  	public static String StringValueFormat(String input){
  		String str = "";
  		if(null != input){
  			str = input.trim();
  		}
  		return str;
  	}
  	
  	public static Double DoubleValueFormat(String input){
  		if(null != input && input.length()>0){
  			return Double.parseDouble(input);
  		}
  		return 0.00;
  	}
  
  	/**
  	 * Double相加
  	 * @param d1
  	 * @param d2
  	 * @return
  	 */
	public static double addDoubleValue(Double d1, Double d2){
		return (Math.round(d1*100.00) + Math.round(d2*100.00) )/100.00;
	}

	/**
	 * Double相减
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double reduceDoubleValue(Double d1, Double d2){
		return (Math.round(d1*100.00) - Math.round(d2*100.00) )/100.00;
	}
	
	public static long DoubleToLong(Double input){
		long output = 0L;
		if( null != input ){
			output = Math.round(input*100);
		}
		return output;
	}
	
	public static int IntegerValueFormat(String input){
  		if(null != input && input.length()>0){
  			return Integer.parseInt(input);
  		}
  		return 0;
  	}
	
	public static String convertDateToStr(java.util.Date date, String format) {
		if (null == date) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static java.util.Date convertStrToDate(String dateStr, String format) throws ParseException {
		java.util.Date date = null;
		if(null != dateStr && dateStr.length()>0){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				date = sdf.parse(dateStr);
			}catch(ParseException pex){
				
			}
		}
		return date;
	}
	
	/***
	 * 复制源Recordset中当前索引下的值
	 * @param rsTo
	 * @param rsFrom
	 * @param rsField
	 * @return
	 * @throws Throwable
	 */
	public static Recordset copyCurrentIdxValue(Recordset rsTo, Recordset rsFrom, Recordset rsField) throws Throwable{
		rsTo.addNew();
		rsField.top();
		while(rsField.next()){
			String name = rsField.getString("name");
			Integer typeid = rsField.getInteger("typeid");
			if( rsFrom.isNull(name) ){
				rsTo.setValue(name, null);
				continue;
			}
			if( typeid == Types.INTEGER ){
				rsTo.setValue(name, rsFrom.getInteger(name));
			}else if( typeid == Types.NUMERIC ){
				rsTo.setValue(name, rsFrom.getDouble(name));
			}else if( typeid == Types.NUMERIC ){
				rsTo.setValue(name, rsFrom.getDouble(name));
			}else if( typeid == Types.VARCHAR ){
				rsTo.setValue(name, rsFrom.getString(name));
			}else if( typeid == Types.TIMESTAMP || typeid == Types.DATE ){
				rsTo.setValue(name, rsFrom.getDate(name));
			}else{
				rsTo.setValue(name, rsFrom.getValue(name));
			}
		}
		return rsTo;
	}
	
	/**
	 * 获取IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * 判断当前是否为整数
	 * @param input
	 * @return
	 */
	public static boolean isInteger(String input){
		boolean isInt = false;
		if( StringUtils.isNotBlank(input) ){
			try{
				Integer.parseInt(input);
				isInt = true;
			}catch(NumberFormatException ex){
			}
		}
		return isInt;
	}
	
	/**
	 * 判断表是否存在
	 * @param db
	 * @param tablename
	 * @return
	 * @throws Throwable
	 */
	public static boolean isTableExists(Db db, String tablename) throws Throwable{
		boolean isExists = false;
		String query = "SELECT count(1) AS num FROM pg_class WHERE relname = '" + tablename + "'";
		Recordset rs = db.get(query);
		rs.first();
		if( rs.getInt("num") > 0 ){
			isExists = true;
		}
		return isExists;
	}
	
	
	/**
	 * 下载文件至本地
	 * @param url
	 * @param savePath
	 * @param filename
	 * @throws IOException 
	 * @throws Throwable
	 */
	public static void downloadFile(String uri, String savePath, String filename) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try{
			URL url = new URL(uri);	// 构造URL
			URLConnection con = url.openConnection();	// 打开连接
			is = con.getInputStream();	// 输入流
			byte[] bs = new byte[1024*200];	// 200K的数据缓冲
			int len;	// 读取到的数据长度
			os = new FileOutputStream(new File(savePath + filename));	// 输出的文件流
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		}catch(IOException ex){
			throw ex;
		}finally{
			if( null != os ){
				os.close();
			}
			if( null != is ){
				is.close();
			}
		}
	}
}
