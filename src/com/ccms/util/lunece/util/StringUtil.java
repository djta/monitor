/**
 * 
 */
package com.ccms.util.lunece.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;

import dinamica.Base64;

/**
 * @author 
 *  字符串帮助类
 */
public class StringUtil {

	public static final byte BOM[] = { -17, -69, -65 };

	public static final char HexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static final Pattern PTitle = Pattern.compile(
			"<title>(.+?)</title>", 34);
	public static Pattern patternHtmlTag = Pattern.compile("<[^<>]+>", 32);
	public static final Pattern PLetterOrDigit = Pattern.compile("^\\w*$", 34);
	public static final Pattern PLetter = Pattern.compile("^[A-Za-z]*$", 34);
	public static final Pattern PDigit = Pattern.compile("^\\d*$", 34);
	private static Pattern chinesePattern = Pattern.compile("[^一-龥]+", 34);
	private static Pattern idPattern = Pattern
			.compile("[\\w\\s\\_\\.\\,]*", 34);

	public StringUtil() {
	}

	public static String strStr(String str) {
		return str != null ? str.trim() : "";
	}

	public static String reqStr(HttpServletRequest req, String str) {
		String tmpStr = req.getParameter(str);

		return tmpStr != null ? tmpStr.trim() : "";
	}

	public static byte[] md5(String src) {
		byte md[] = null;

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md = md5.digest(src.getBytes());
		return md;

	}

	public static byte[] md5(byte src[]) {
		byte md[];
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md = md5.digest(src);
		return md;

	}

	public static String md5Hex(String src) {
		byte md[];
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		md = md5.digest(src.getBytes());
		return hexEncode(md);
	}

	public static String md5Base64(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return base64Encode(md5.digest(str.getBytes()));

	}

	public static String md5Base64FromHex(String md5str) {
		char cs[] = md5str.toCharArray();
		byte bs[] = new byte[16];
		for (int i = 0; i < bs.length; i++) {
			char c1 = cs[i * 2];
			char c2 = cs[i * 2 + 1];
			byte m1 = 0;
			byte m2 = 0;
			for (byte k = 0; k < 16; k++) {
				if (HexDigits[k] == c1)
					m1 = k;
				if (HexDigits[k] == c2)
					m2 = k;
			}

			bs[i] = (byte) (m1 << 4 | 0 + m2);
		}

		String newstr = base64Encode(bs);
		return newstr;
	}

	public static String md5HexFromBase64(String base64str) {
		return hexEncode(base64Decode(base64str));
	}

	public static String hexEncode(byte bs[]) {
		return new String((new Hex()).encode(bs));
	}

	public static byte[] hexDecode(String str) {
		char cs[];
		if (str.endsWith("\n"))
			str = str.substring(0, str.length() - 1);
		cs = str.toCharArray();
		try {
			return Hex.decodeHex(cs);
		} catch (DecoderException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String byteToBin(byte bs[]) {
		char cs[] = new char[bs.length * 9];
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			int j = i * 9;
			cs[j] = (b >>> 7 & 1) != 1 ? '0' : '1';
			cs[j + 1] = (b >>> 6 & 1) != 1 ? '0' : '1';
			cs[j + 2] = (b >>> 5 & 1) != 1 ? '0' : '1';
			cs[j + 3] = (b >>> 4 & 1) != 1 ? '0' : '1';
			cs[j + 4] = (b >>> 3 & 1) != 1 ? '0' : '1';
			cs[j + 5] = (b >>> 2 & 1) != 1 ? '0' : '1';
			cs[j + 6] = (b >>> 1 & 1) != 1 ? '0' : '1';
			cs[j + 7] = (b & 1) != 1 ? '0' : '1';
			cs[j + 8] = ',';
		}

		return new String(cs);
	}

	public static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
			resultSb.append(" ");
		}

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return String.valueOf(HexDigits[d1] + HexDigits[d2]);
	}

	public static boolean isUTF8(byte bs[]) {
		if (hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf"))
			return true;
		int lLen = bs.length;
		for (int i = 0; i < lLen;) {
			byte b = bs[i++];
			if (b < 0) {
				if (b < -64 || b > -3)
					return false;
				int c = b <= -4 ? ((int) (b <= -8 ? ((int) (b <= -16 ? ((int) (b <= -32 ? 1
						: 2))
						: 3))
						: 4))
						: 5;
				if (i + c > lLen)
					return false;
				for (int j = 0; j < c;) {
					if (bs[i] >= -64)
						return false;
					j++;
					i++;
				}

			}
		}

		return true;
	}

	public static String base64Encode(byte b[]) {
		if (b == null)
			return null;
		else
			return Base64.encodeToString(b, true);
	}

	public static byte[] base64Decode(String s) {
		if (s == null)
			return null;
		return Base64.decode(s);
	}

	public static String javaEncode(String txt) {
		if (txt == null || txt.length() == 0) {
			return txt;
		} else {
			txt = replaceEx(txt, "\\", "\\\\");
			txt = replaceEx(txt, "\r\n", "\n");
			txt = replaceEx(txt, "\r", "\\r");
			txt = replaceEx(txt, "\n", "\\n");
			txt = replaceEx(txt, "\"", "\\\"");
			txt = replaceEx(txt, "'", "\\'");
			return txt;
		}
	}

	public static String javaDecode(String txt) {
		if (txt == null || txt.length() == 0) {
			return txt;
		} else {
			txt = replaceEx(txt, "\\\\", "\\");
			txt = replaceEx(txt, "\\n", "\n");
			txt = replaceEx(txt, "\\r", "\r");
			txt = replaceEx(txt, "\\\"", "\"");
			txt = replaceEx(txt, "\\'", "'");
			return txt;
		}
	}

	public static String[] splitEx(String str, String spilter) {
		if (str == null)
			return null;
		if (spilter == null || spilter.equals("")
				|| str.length() < spilter.length()) {
			String t[] = { str };
			return t;
		}
		List<String> al = new ArrayList<String>();
		char cs[] = str.toCharArray();
		char ss[] = spilter.toCharArray();
		int length = spilter.length();
		int lastIndex = 0;
		for (int i = 0; i <= str.length() - length;) {
			boolean notSuit = false;
			for (int j = 0; j < length; j++) {
				if (cs[i + j] == ss[j])
					continue;
				notSuit = true;
				break;
			}

			if (!notSuit) {
				al.add(str.substring(lastIndex, i));
				i += length;
				lastIndex = i;
			} else {
				i++;
			}
		}

		if (lastIndex <= str.length())
			al.add(str.substring(lastIndex, str.length()));
		String t[] = new String[al.size()];
		for (int i = 0; i < al.size(); i++)
			t[i] = (String) al.get(i);

		return t;
	}

	public static String replaceEx(String str, String subStr, String reStr) {
		if (str == null)
			return null;
		if (subStr == null || subStr.equals("")
				|| subStr.length() > str.length() || reStr == null)
			return str;
		StringBuffer sb = new StringBuffer();
		int lastIndex = 0;
		do {
			int index = str.indexOf(subStr, lastIndex);
			if (index >= 0) {
				sb.append(str.substring(lastIndex, index));
				sb.append(reStr);
				lastIndex = index + subStr.length();
			} else {
				sb.append(str.substring(lastIndex));
				return sb.toString();
			}
		} while (true);
	}

	public static String replaceAllIgnoreCase(String source, String oldstring,
			String newstring) {
		Pattern p = Pattern.compile(oldstring, 34);
		Matcher m = p.matcher(source);
		return m.replaceAll(newstring);
	}

	public static String urlEncode(String str) {
		return urlEncode(str, Constant.CHARSET);
	}

	public static String urlDecode(String str) {
		return urlDecode(str, Constant.CHARSET);
	}

	public static String urlEncode(String str, String charset) {
		try {
			return (new URLCodec()).encode(str, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String urlDecode(String str, String charset) {
		try {
			return (new URLCodec()).decode(str, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (DecoderException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String htmlEncode(String txt) {
		return StringEscapeUtils.escapeHtml(txt);
	}

	public static String htmlDecode(String txt) {
		txt = replaceEx(txt, "&#8226;", "\267");
		return StringEscapeUtils.unescapeHtml(txt);
	}

	public static String quotEncode(String txt) {
		if (txt == null || txt.length() == 0) {
			return txt;
		} else {
			txt = replaceEx(txt, "&", "&amp;");
			txt = replaceEx(txt, "\"", "&quot;");
			return txt;
		}
	}

	public static String quotDecode(String txt) {
		if (txt == null || txt.length() == 0) {
			return txt;
		} else {
			txt = replaceEx(txt, "&quot;", "\"");
			txt = replaceEx(txt, "&amp;", "&");
			return txt;
		}
	}

	public static String escape(String src) {
		StringBuffer sb = new StringBuffer();
		sb.ensureCapacity(src.length() * 6);
		for (int i = 0; i < src.length(); i++) {
			char j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				sb.append(j);
			else if (j < '\u0100') {
				sb.append("%");
				if (j < '\020')
					sb.append("0");
				sb.append(Integer.toString(j, 16));
			} else {
				sb.append("%u");
				sb.append(Integer.toString(j, 16));
			}
		}

		return sb.toString();
	}

	public static String unescape(String src) {
		StringBuffer sb = new StringBuffer();
		sb.ensureCapacity(src.length());
		int lastPos = 0;
		int pos = 0;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					char ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					sb.append(ch);
					lastPos = pos + 6;
				} else {
					char ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					sb.append(ch);
					lastPos = pos + 3;
				}
			} else if (pos == -1) {
				sb.append(src.substring(lastPos));
				lastPos = src.length();
			} else {
				sb.append(src.substring(lastPos, pos));
				lastPos = pos;
			}
		}
		return sb.toString();
	}

	public static String leftPad(String srcString, char c, int length) {
		if (srcString == null)
			srcString = "";
		int tLen = srcString.length();
		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < iMax; i++)
			sb.append(c);

		sb.append(srcString);
		return sb.toString();
	}

	public static String subString(String src, int length) {
		if (src == null)
			return null;
		int i = src.length();
		if (i > length)
			return src.substring(0, length);
		else
			return src;
	}

	public static String rightPad(String srcString, char c, int length) {
		if (srcString == null)
			srcString = "";
		int tLen = srcString.length();
		if (tLen >= length)
			return srcString;
		int iMax = length - tLen;
		StringBuffer sb = new StringBuffer();
		sb.append(srcString);
		for (int i = 0; i < iMax; i++)
			sb.append(c);

		return sb.toString();
	}

	public static String rightTrim(String src) {
		if (src != null) {
			char chars[] = src.toCharArray();
			for (int i = chars.length - 1; i > 0; i--)
				if (chars[i] != ' ' && chars[i] != '\t')
					return new String(ArrayUtils.subarray(chars, 0, i + 1));

		}
		return src;
	}

	public static void printStringWithAnyCharset(String str) {
		Map<String,Charset> map = Charset.availableCharsets();
		Object keys[] = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				System.out.print("\t");
				try {
					System.out.println("From "
							+ keys[i]
							+ " To "
							+ keys[j]
							+ ":"
							+ new String(str.getBytes(keys[i].toString()),
									keys[j].toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static String toSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++)
			if (c[i] == ' ')
				c[i] = '\u3000';
			else if ((c[i] <= '@' || c[i] >= '[')
					&& (c[i] <= '`' || c[i] >= '{') && c[i] < '\177')
				c[i] = (char) (c[i] + 65248);

		return new String(c);
	}

	public static String toNSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++)
			if (c[i] == ' ')
				c[i] = '\u3000';
			else if (c[i] < '\177')
				c[i] = (char) (c[i] + 65248);

		return new String(c);
	}

	public static String toDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++)
			if (c[i] == '\u3000')
				c[i] = ' ';
			else if (c[i] > '\uFF00' && c[i] < '\uFF5F')
				c[i] = (char) (c[i] - 65248);

		return new String(c);
	}

	public static String getHtmlTitle(File f) {
		String html = FileUtil.readText(f);
		String title = getHtmlTitle(html);
		return title;
	}

	public static String getHtmlTitle(String html) {
		Matcher m = PTitle.matcher(html);
		if (m.find())
			return m.group(1).trim();
		else
			return null;
	}

	public static String clearHtmlTag(String html) {
		String text = patternHtmlTag.matcher(html).replaceAll("");
		if (isEmpty(text)) {
			return "";
		} else {
			text = htmlDecode(html);
			return text.replaceAll("[\\s　]{2,}", " ");
		}
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return str;
		else
			return (new StringBuffer(strLen))
					.append(Character.toTitleCase(str.charAt(0)))
					.append(str.substring(1)).toString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static final String noNull(String string, String defaultString) {
		return isEmpty(string) ? defaultString : string;
	}

	public static final String noNull(String string) {
		return noNull(string, "");
	}

	public static String join(Object arr[]) {
		return join(arr, ",");
	}

	public static String join(Object arr[][]) {
		return join(arr, "\n", ",");
	}

	public static String join(Object arr[], String spliter) {
		if (arr == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0)
				sb.append(spliter);
			sb.append(arr[i]);
		}

		return sb.toString();
	}

	public static String join(Object arr[][], String spliter1, String spliter2) {
		if (arr == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i != 0)
				sb.append(spliter2);
			sb.append(join(arr[i], spliter2));
		}

		return sb.toString();
	}

	public static String join(List<String> list) {
		return join(list, ",");
	}

	public static String join(List<String> list, String spliter) {
		if (list == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i != 0)
				sb.append(spliter);
			sb.append(list.get(i));
		}

		return sb.toString();
	}

	public static int count(String str, String findStr) {
		int lastIndex = 0;
		int length = findStr.length();
		int count = 0;
		for (int start = 0; (start = str.indexOf(findStr, lastIndex)) >= 0;) {
			lastIndex = start + length;
			count++;
		}

		return count;
	}

	public static boolean isLetterOrDigit(String str) {
		return PLetterOrDigit.matcher(str).find();
	}

	public static boolean isLetter(String str) {
		return PLetter.matcher(str).find();
	}

	public static boolean isDigit(String str) {
		if (isEmpty(str))
			return false;
		else
			return PDigit.matcher(str).find();
	}

	public static boolean containsChinese(String str) {
		return !chinesePattern.matcher(str).matches();
	}

	public static boolean checkID(String str) {
		if (isEmpty(str))
			return true;
		return idPattern.matcher(str).matches();
	}

	public static String getURLExtName(String url) {
		if (isEmpty(url))
			return null;
		int index1 = url.indexOf('?');
		if (index1 == -1)
			index1 = url.length();
		int index2 = url.lastIndexOf('.', index1);
		if (index2 == -1)
			return null;
		int index3 = url.indexOf('/', 8);
		if (index3 == -1)
			return null;
		String ext = url.substring(index2 + 1, index1);
		if (ext.matches("[^\\/\\\\]*"))
			return ext;
		else
			return null;
	}

	public static String getURLFileName(String url) {
		if (isEmpty(url))
			return null;
		int index1 = url.indexOf('?');
		if (index1 == -1)
			index1 = url.length();
		int index2 = url.lastIndexOf('/', index1);
		if (index2 == -1 || index2 < 8) {
			return null;
		} else {
			String ext = url.substring(index2 + 1, index1);
			return ext;
		}
	}

	public static String clearForXML(String str) {
		char cs[] = str.toCharArray();
		char ncs[] = new char[cs.length];
		int j = 0;
		for (int i = 0; i < cs.length; i++)
			if (cs[i] <= '\uFFFD'
					&& (cs[i] >= ' ' || !((cs[i] != '\t') & (cs[i] != '\n') & (cs[i] != '\r'))))
				ncs[j++] = cs[i];

		ncs = ArrayUtils.subarray(ncs, 0, j);
		return new String(ncs);
	}

	public static String Html2Text(String inputString) {
		String htmlStr = inputString;
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			// 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
			// 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
			// 定义HTML标签的正则表达式
			String regEx_html = "<[^>]+>";
			String regEx_html1 = "<[^>]+";
			// 过滤script标签
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");
			// 过滤style标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");
			// 过滤html标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
			// 过滤html标签
			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("");

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}
}