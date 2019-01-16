package transactions.project.weixin.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import dinamica.StringUtil;
import transactions.project.weixin.common.AesException;
import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.common.XMLParse;

/**
 * Servlet implementation class WeixinNotify
 */
public class WeixinNotify extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WeixinNotify() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String postString = WeixinUtil.getPostString(request);
		Element element = null;
		try {
			element = XMLParse.getElement(postString);
		} catch (AesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String appId = element.getElementsByTagName("AppId").item(0)
				.getTextContent();
		String errorType = element.getElementsByTagName("ErrorType").item(0)
				.getTextContent();
		String description = element.getElementsByTagName("Description")
				.item(0).getTextContent();
		String alarmContent = element.getElementsByTagName("AlarmContent")
				.item(0).getTextContent();
		String timeStamp = element.getElementsByTagName("TimeStamp").item(0)
				.getTextContent();
		String appSignature = element.getElementsByTagName("AppSignature")
				.item(0).getTextContent();
		String signMethod = element.getElementsByTagName("SignMethod").item(0)
				.getTextContent();
		String insertNotifyString = null;
		try {
			WeixinUtil
					.getLocalResource("/transactions/project/weixin/service/insert-notify.sql");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insertNotifyString = StringUtil.replace(StringUtil.replace(StringUtil
				.replace(StringUtil.replace(StringUtil.replace(StringUtil
						.replace(StringUtil.replace(insertNotifyString,
								"${app_id}", appId), "${error_type}",
								errorType), "${description}", description),
						"${alarm_content}", alarmContent), "${time_stamp}",
						timeStamp), "${app_signature}", appSignature),
				"${sign_method}", signMethod);
		// TODO Auto-generated method stub
	}

}
