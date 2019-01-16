package com.ccms.util.attachment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.xml.Document;

public class DownloadFile_  extends GenericOutput{
	public final static String PATH_SPLIT_WINDOWS = "\\";
	public final static String PATH_SPLIT_UNIX = "/";

	public void print(GenericTransaction data) throws Throwable {
		
		Document doc = getConfig().getDocument();
		String file_recordset = doc.getElement("file-recordset").getValue();
		
		BufferedInputStream in =null ;
		ByteArrayOutputStream buf = null;
			Recordset rsFile = data.getRecordset(file_recordset);
			rsFile.first();
			String fileName=rsFile.getString("file_name");
			
			String filePath = rsFile.getString("file_path");
			String scale_size=getRequest().getParameter("scale_size");

			if(!"".equals(scale_size)&&null!=scale_size){
				filePath=filePath.replace(fileName, "")+(scale_size+"_"+fileName);
			}else{
				filePath = rsFile.getString("file_path");
			}
			String fileType = rsFile.getString("file_type");
			File file = new File(filePath);
			//File file = new File(filePath);
			if (!file.exists() || file.isDirectory()){
				String picture_not_found = getContext().getInitParameter("picture-not-found");
				file = new File(picture_not_found);
			}
			
			byte[] temp = new byte[1024 * 1024];
			int size = -1;
			int nStartPos = 0;
			int fileLength = new Long(file.length()).intValue();
			in=new BufferedInputStream(new FileInputStream(file));
			buf=new ByteArrayOutputStream();
			while ((size = in.read(temp)) > 0 && nStartPos < fileLength) {
				buf.write(temp, 0, size);
				nStartPos += size;
			}
			in.close();
			byte[] b = buf.toByteArray();
			getResponse().setBufferSize(b.length);
			getResponse().setContentLength(b.length);
			getResponse().setHeader("Cache-Control","no-cache");
			getResponse().setHeader("Pragma","no-cache");
			String type = getRequest().getParameter("type");
			fileType = getMimeType(fileName);
			if("1".equals(type) && fileType !=null && fileType.indexOf("image")>=0){//图片预览
				getResponse().setContentType(fileType);
			}else{
				getResponse().setContentType(fileType==null?"application/zip":fileType);
				getResponse().setHeader("Content-Disposition", getAttachmentString(fileName));
			}
			ServletOutputStream out = getResponse().getOutputStream();
			out.write(b);
	}

	protected String getAttachmentString(String excelFileName) {
		String filename = excelFileName;
		try {
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				filename = java.net.URLEncoder.encode(filename,"utf-8");
				filename = filename.replaceAll("\\+", "%20"); //处理空格
			} else {   
				filename = new String(filename.getBytes("utf-8"),"iso-8859-1");   
			}
		} catch (UnsupportedEncodingException e) { }
		return "attachment; filename=\"" + filename + "\";";
	}

	/**
	 * 获取文件mini type
	 * @param ext
	 * @return
	 * @throws JSONException
	 */
	private static String getMimeType(String filename) throws JSONException{
		String ext = filename.substring(filename.lastIndexOf("."), filename.length());
		String miniType = "";
		final JSONObject miniTypesJson = new JSONObject("{\"apk\":\"application/vnd.android.package-archive\",\"3gp\":\"video/3gpp\",\"ai\":\"application/postscript\",\"aif\":\"audio/x-aiff\",\"aifc\":\"audio/x-aiff\",\"aiff\":\"audio/x-aiff\",\"asc\":\"text/plain\",\"atom\":\"application/atom+xml\",\"au\":\"audio/basic\",\"avi\":\"video/x-msvideo\",\"bcpio\":\"application/x-bcpio\",\"bin\":\"application/octet-stream\",\"bmp\":\"image/bmp\",\"cdf\":\"application/x-netcdf\",\"cgm\":\"image/cgm\",\"class\":\"application/octet-stream\",\"cpio\":\"application/x-cpio\",\"cpt\":\"application/mac-compactpro\",\"csh\":\"application/x-csh\",\"css\":\"text/css\",\"dcr\":\"application/x-director\",\"dif\":\"video/x-dv\",\"dir\":\"application/x-director\",\"djv\":\"image/vnd.djvu\",\"djvu\":\"image/vnd.djvu\",\"dll\":\"application/octet-stream\",\"dmg\":\"application/octet-stream\",\"dms\":\"application/octet-stream\",\"doc\":\"application/msword\",\"docx\":\"application/vnd.openxmlformats-officedocument.wordprocessingml.document\",\"dtd\":\"application/xml-dtd\",\"dv\":\"video/x-dv\",\"dvi\":\"application/x-dvi\",\"dxr\":\"application/x-director\",\"eps\":\"application/postscript\",\"etx\":\"text/x-setext\",\"exe\":\"application/octet-stream\",\"ez\":\"application/andrew-inset\",\"flv\":\"video/x-flv\",\"gif\":\"image/gif\",\"gram\":\"application/srgs\",\"grxml\":\"application/srgs+xml\",\"gtar\":\"application/x-gtar\",\"gz\":\"application/x-gzip\",\"hdf\":\"application/x-hdf\",\"hqx\":\"application/mac-binhex40\",\"htm\":\"text/html\",\"html\":\"text/html\",\"ice\":\"x-conference/x-cooltalk\",\"ico\":\"image/x-icon\",\"ics\":\"text/calendar\",\"ief\":\"image/ief\",\"ifb\":\"text/calendar\",\"iges\":\"model/iges\",\"igs\":\"model/iges\",\"jnlp\":\"application/x-java-jnlp-file\",\"jp2\":\"image/jp2\",\"jpe\":\"image/jpeg\",\"jpeg\":\"image/jpeg\",\"jpg\":\"image/jpeg\",\"js\":\"application/x-javascript\",\"kar\":\"audio/midi\",\"latex\":\"application/x-latex\",\"lha\":\"application/octet-stream\",\"lzh\":\"application/octet-stream\",\"m3u\":\"audio/x-mpegurl\",\"m4a\":\"audio/mp4a-latm\",\"m4p\":\"audio/mp4a-latm\",\"m4u\":\"video/vnd.mpegurl\",\"m4v\":\"video/x-m4v\",\"mac\":\"image/x-macpaint\",\"man\":\"application/x-troff-man\",\"mathml\":\"application/mathml+xml\",\"me\":\"application/x-troff-me\",\"mesh\":\"model/mesh\",\"mid\":\"audio/midi\",\"midi\":\"audio/midi\",\"mif\":\"application/vnd.mif\",\"mov\":\"video/quicktime\",\"movie\":\"video/x-sgi-movie\",\"mp2\":\"audio/mpeg\",\"mp3\":\"audio/mpeg\",\"mp4\":\"video/mp4\",\"mpe\":\"video/mpeg\",\"mpeg\":\"video/mpeg\",\"mpg\":\"video/mpeg\",\"mpga\":\"audio/mpeg\",\"ms\":\"application/x-troff-ms\",\"msh\":\"model/mesh\",\"mxu\":\"video/vnd.mpegurl\",\"nc\":\"application/x-netcdf\",\"oda\":\"application/oda\",\"ogg\":\"application/ogg\",\"ogv\":\"video/ogv\",\"pbm\":\"image/x-portable-bitmap\",\"pct\":\"image/pict\",\"pdb\":\"chemical/x-pdb\",\"pdf\":\"application/pdf\",\"pgm\":\"image/x-portable-graymap\",\"pgn\":\"application/x-chess-pgn\",\"pic\":\"image/pict\",\"pict\":\"image/pict\",\"png\":\"image/png\",\"pnm\":\"image/x-portable-anymap\",\"pnt\":\"image/x-macpaint\",\"pntg\":\"image/x-macpaint\",\"ppm\":\"image/x-portable-pixmap\",\"pptx\":\"application/vnd.openxmlformats-officedocument.presentationml.presentation\",\"ppt\":\"application/vnd.ms-powerpoint\",\"ps\":\"application/postscript\",\"qt\":\"video/quicktime\",\"qti\":\"image/x-quicktime\",\"qtif\":\"image/x-quicktime\",\"ra\":\"audio/x-pn-realaudio\",\"ram\":\"audio/x-pn-realaudio\",\"ras\":\"image/x-cmu-raster\",\"rdf\":\"application/rdf+xml\",\"rgb\":\"image/x-rgb\",\"rm\":\"application/vnd.rn-realmedia\",\"roff\":\"application/x-troff\",\"rtf\":\"text/rtf\",\"rtx\":\"text/richtext\",\"sgm\":\"text/sgml\",\"sgml\":\"text/sgml\",\"sh\":\"application/x-sh\",\"shar\":\"application/x-shar\",\"silo\":\"model/mesh\",\"sit\":\"application/x-stuffit\",\"skd\":\"application/x-koan\",\"skm\":\"application/x-koan\",\"skp\":\"application/x-koan\",\"skt\":\"application/x-koan\",\"smi\":\"application/smil\",\"smil\":\"application/smil\",\"snd\":\"audio/basic\",\"so\":\"application/octet-stream\",\"spl\":\"application/x-futuresplash\",\"src\":\"application/x-wais-source\",\"sv4cpio\":\"application/x-sv4cpio\",\"sv4crc\":\"application/x-sv4crc\",\"svg\":\"image/svg+xml\",\"swf\":\"application/x-shockwave-flash\",\"t\":\"application/x-troff\",\"tar\":\"application/x-tar\",\"tcl\":\"application/x-tcl\",\"tex\":\"application/x-tex\",\"texi\":\"application/x-texinfo\",\"texinfo\":\"application/x-texinfo\",\"tif\":\"image/tiff\",\"tiff\":\"image/tiff\",\"tr\":\"application/x-troff\",\"tsv\":\"text/tab-separated-values\",\"txt\":\"text/plain\",\"ustar\":\"application/x-ustar\",\"vcd\":\"application/x-cdlink\",\"vrml\":\"model/vrml\",\"vxml\":\"application/voicexml+xml\",\"wav\":\"audio/x-wav\",\"wbmp\":\"image/vnd.wap.wbmp\",\"wbxml\":\"application/vnd.wap.wbxml\",\"webm\":\"video/webm\",\"wml\":\"text/vnd.wap.wml\",\"wmlc\":\"application/vnd.wap.wmlc\",\"wmls\":\"text/vnd.wap.wmlscript\",\"wmlsc\":\"application/vnd.wap.wmlscriptc\",\"wmv\":\"video/x-ms-wmv\",\"wrl\":\"model/vrml\",\"xbm\":\"image/x-xbitmap\",\"xht\":\"application/xhtml+xml\",\"xhtml\":\"application/xhtml+xml\",\"xls\":\"application/vnd.ms-excel\",\"xlsx\":\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\",\"xml\":\"application/xml\",\"xpm\":\"image/x-xpixmap\",\"xsl\":\"application/xml\",\"xslt\":\"application/xslt+xml\",\"xul\":\"application/vnd.mozilla.xul+xml\",\"xwd\":\"image/x-xwindowdump\",\"xyz\":\"chemical/x-xyz\",\"zip\":\"application/zip\"}");
		if( StringUtils.isNotBlank(ext) ){
			if( ext.startsWith(".") ){
				ext = ext.substring(ext.indexOf(".") + 1);
			}
			ext = ext.toLowerCase();
			if( miniTypesJson.has(ext) ){
				miniType = miniTypesJson.getString(ext);
			}
		}
		return miniType;
	}
}
