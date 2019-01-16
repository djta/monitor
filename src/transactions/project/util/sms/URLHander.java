package transactions.project.util.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 发送URL请求
 */
public class URLHander {

	private static final Log log = LogFactory.getLog(URLHander.class);
	
	private static String DECODE = "UTF-8"; // 编码方式

	private static int TIMEOUT = 60000; // 请求超时 单位 毫秒

	public static String getDecode() {
		return DECODE;
	}

	/**
	 * 设置编码方式传输
	 * @param decode
	 */
	public static void setDECODE(String dECODE) {
		DECODE = dECODE;
	}

	/**
	 * 发送POST请求
	 * @param urlStr
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public String actionPost(String urlStr, String content) throws Exception {
		HttpURLConnection con = null;
		String response;
		try {	
			con = getPostURLConn(urlStr, content, "POST");
			submit(con, content);
			response = response(con);
		} catch (Exception e) {
			log.error("connect error, address：" +urlStr + ",error code:" + e.getMessage());
			throw e;
		} finally {
			disconnect(con);
		}
		return response;
	}

	/**
	 * 发送GET请求
	 * @param urlStr
	 * @param content
	 * @return
	 */
	public String actionGet(String urlStr) {
		HttpURLConnection con = null;
		String response;
		try {
			con = getGetURLConn(urlStr, "GET");
			response = response(con);
		} catch (Exception e) {
			log.error("connect error, address：" +urlStr + ",error code:" + e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			disconnect(con);
		}
		return response;
	}

	public static String getDECODE() {
		return DECODE;
	}

	public static int getTIMEOUT() {
		return TIMEOUT;
	}

	public static void setTIMEOUT(int tIMEOUT) {
		TIMEOUT = tIMEOUT;
	}

	private HttpURLConnection getPostURLConn(String urlStr, String content, String reqMethod) throws IOException {
		HttpURLConnection con = (HttpURLConnection) getRul(urlStr).openConnection();
		con.setConnectTimeout(TIMEOUT);
		con.setDoOutput(true);
		con.setRequestMethod(reqMethod);
		con.setRequestProperty("httpclient.useragent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + DECODE);
		con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.5");
		con.setRequestProperty("Cache-Control", "max-age=0");
		con.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
		return con;
	}

	private HttpURLConnection getGetURLConn(String urlStr, String reqMethod) throws IOException {
		HttpURLConnection con = (HttpURLConnection) getRul(urlStr).openConnection();
		con.setConnectTimeout(TIMEOUT);
		con.setRequestMethod(reqMethod);
		con.connect();
		return con;
	}

	private URL getRul(String urlStr) throws MalformedURLException {
		return new URL(urlStr);
	}

	/**
	 * 提交
	 * @param con
	 * @param content
	 * @return
	 * @throws IOException
	 */
	private void submit(URLConnection con, String content) throws IOException {
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(con.getOutputStream(), DECODE);
			out.write(content);
		} catch (IOException e) {
			throw new IOException();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				throw new IOException();
			}
		}
	}

	/**
	 * 响应
	 * @param con
	 * @return
	 * @throws Exception 
	 */
	private String response(URLConnection con) throws Exception {
		return response(con,DECODE);
	}

	/**
	 * 响应
	 * @param con
	 * @return
	 * @throws Exception 
	 */
	private String response(URLConnection con,String charset) throws Exception {
		BufferedReader br = null;
		String line = "";
		String res = "";
		try {
			br = new BufferedReader(new InputStreamReader(con.getInputStream(),Charset.forName(charset)));
			for (line = br.readLine(); line != null; line = br.readLine()) {
				res += line;
			}
		} catch (IOException e) {
			throw new IOException();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				throw new Exception();
			}
		}
		return res;
	}
	
	private void disconnect(HttpURLConnection con) {
		if (con != null){
			con.disconnect();
		}
	}
}
