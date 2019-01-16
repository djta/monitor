package transactions.project.util.sms;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 平台(畅卓)
 * 2015-4-21
 */
public class SDKClient {
	private String username = null;
	private String password = null;
	private static final String baseURL = "http://sms.chanzor.com:8001/"; // 查询余额地址
	private static final String sendURL = "sms.aspx?action=send"; // 发送即时短信地址
	
	public SDKClient(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * 短信发送
	 * @param mobile 手机号
	 * @param msg 短信内容
	 * @param sendTime 期望发送时间
	 * @param extno 扩展号
	 * @return
	 */
	public Map<String, String> sendSMS(String mobile, String msg, String sendTime, String extno) {
		Map<String, String> map = null;
		try {
			StringBuilder buf = new StringBuilder();
			buf.append("&account=").append(username);
			buf.append("&password=").append(password);
			buf.append("&mobile=").append(mobile);
			buf.append("&content=").append(URLEncoder.encode(msg, "UTF-8"));
			if( StringUtils.isNotBlank(extno) ){
				buf.append("&extno=").append(extno);
			}
			if( StringUtils.isNotBlank(sendTime) ){
				buf.append("&sendTime=").append(sendTime);
			}
			String rtn = new URLHander().actionPost(baseURL + sendURL, buf.toString());
			map = this.formatSendResponse(rtn);
		} catch (Throwable e) {
			map = new HashMap<String, String>();
			map.put("error_message", "程序异常");
			e.printStackTrace();
		}
		return map;
	}
	
	private Map<String, String> formatSendResponse(String response) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", response);
		if( StringUtils.isNotBlank(response) ){
			try {
				SAXReader saxReader = new SAXReader();
				Document rDoc = saxReader.read(new ByteArrayInputStream(response.getBytes()));
				Element rEle = rDoc.getRootElement();
				Iterator<?> iter = rEle.elementIterator();
				while(iter.hasNext()){
					Element field = (Element) iter.next();
					String name = field.getName();
					String value = field.getText();
					map.put(name, value);
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
