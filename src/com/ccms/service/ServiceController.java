package com.ccms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static String _serviceUrl = null;
	private static String _requestEncoding = null;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String method = req.getMethod().toUpperCase();
		if (!method.equals("POST")) {
			res.sendError(501, method + " not supported");
			return;
		}
		if (_requestEncoding != null) {
			req.setCharacterEncoding(_requestEncoding);
		}

		try {
			String context = req.getContextPath();
			String uri = req.getRequestURI();
			//去掉/service
			uri = uri.substring(context.length()+8);
			if (uri.indexOf("?") > 0) {
				uri = uri.substring(0, uri.indexOf("?"));
			}
			StringBuffer sb = getQueryString(req);
			JSONObject rtnJSON = httpRequest(uri, sb.toString());
			String rtnStr = rtnJSON.toString();
			byte[] b = rtnStr.getBytes();
			res.setBufferSize(b.length);
			res.setContentLength(b.length);
			res.setHeader("Cache-Control","no-cache");
			res.setHeader("Pragma","no-cache");
			res.setContentType("application/json");
			ServletOutputStream out = res.getOutputStream();
			out.write(b);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void init() throws ServletException {
		try {
			ServletConfig sc = getServletConfig();
			_serviceUrl = sc.getInitParameter("service-url");
			if (_serviceUrl == null || _serviceUrl.length() == 0) {
				throw new ServletException("Configuration problem detected in WEB.XML: service-url context parameter cannot be undefined!");
			}
			_requestEncoding = sc.getInitParameter("request-encoding");
			if (_requestEncoding != null && _requestEncoding.trim().equals(""))
				_requestEncoding = null;
			super.init();
		} catch (Throwable e) {
			log("Controller servlet failed on init!");
			throw new ServletException(e);
		}
	}

	private JSONObject httpRequest(String requestUrl, String outputStr) {
		JSONObject jsonObject = null;
		HttpURLConnection httpUrlConn = null;
		try {
			// 建立连接
			URL url = new URL(_serviceUrl+requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestMethod("POST");
			httpUrlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// 数据提交
			OutputStream outputStream = httpUrlConn.getOutputStream(); // 注意编码格式，防止中文乱码
			outputStream.write(outputStr.getBytes());
			outputStream.flush();
			outputStream.close();

			int code = httpUrlConn.getResponseCode();
			if(code == 200){
				// 将返回的输入流转换成字符串
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConn.getInputStream(), "UTF-8"));
				StringBuffer buffer = new StringBuffer(256);
				String str = null;
				while ((str = bufferedReader.readLine()) != null) {
					buffer.append(str);
				}
				bufferedReader.close();
				if (buffer.indexOf("<oauth>") >= 0 && buffer.indexOf("<error>access_denied</error>") > 0) {
					jsonObject = new JSONObject();
					jsonObject.put("code", "1001");
					jsonObject.put("message", "未登录或登录超时");
				} else {
					jsonObject = new JSONObject(buffer.toString());
				}
			}else{
				jsonObject = new JSONObject();
				jsonObject.put("code", code);
				jsonObject.put("message", "系统繁忙");
			}
		} catch (Throwable e) {
			jsonObject = new JSONObject();
			try {
				jsonObject.put("code", "-1");
				jsonObject.put("message", "系统繁忙");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			System.out.println(e.getMessage());
		} finally {
			if (null != httpUrlConn) {
				httpUrlConn.disconnect();
			}
		}
		return jsonObject;
	}

	private StringBuffer getQueryString(HttpServletRequest request) throws Throwable {
		Enumeration<String> paramNames = request.getParameterNames();
		StringBuffer paramData = new StringBuffer(256);
		while (paramNames.hasMoreElements()) {
			String name = (String) paramNames.nextElement();
			String value = request.getParameter(name);
			value = URLDecoder.decode(value, "UTF-8");
			paramData.append(name).append("=").append(value).append("&");
		}
		if (paramData.length() > 0) // delete the last char '&'
			paramData.deleteCharAt(paramData.length() - 1);
		return paramData;
	}
	
	 /*
     * 得到所有的cookie，并把它们连接起来
     */
    private String getCookieString(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            String value = cookie.getValue();
            String name = cookie.getName();
            if (value != null && !value.equals("")) 
                buffer.append(name).append("=").append(value).append(";");
        }
        if (buffer.length() > 0)
            buffer.deleteCharAt(buffer.length() - 1);//delete the last char ';'
        return buffer.toString();
    }
}
