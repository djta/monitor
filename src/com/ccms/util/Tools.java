package com.ccms.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.net.ftp.FTPClient;

import dinamica.Base64;

public class Tools {
	
	public final static String PATH_SPLIT_WINDOWS = "\\";
	public final static String PATH_SPLIT_UNIX = "/";
	public final static String PERCENT_FORMAT = "###,###.##%";
	
	public Tools() {
	}

	public static String getPercentFormatted(Integer mole,Integer demo ) {
		Double percent=0.00;
		if(demo>0){
			Double dbMole=Double.parseDouble(String.valueOf(mole));
			Double dbDemo=Double.parseDouble(String.valueOf(demo));
			percent = dbMole/dbDemo;
		}
		String percentFormatted=Tools.formatPercent(percent, PERCENT_FORMAT,Locale.CHINA);
		return percentFormatted;
	}
	
	public static String formatPercent(double d, String pattern, Locale l) {
		String s = "";
		try {
			DecimalFormat df = (DecimalFormat) NumberFormat.getPercentInstance(l);
			df.applyPattern(pattern);
			s = df.format(d);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	public static String replaceIllegalCharOfFilName(String srcFileName) {
		String destFileName = srcFileName;
		destFileName = destFileName.replace("\\", "");
		destFileName = destFileName.replace("/", "");
		destFileName = destFileName.replace(":", "");
		destFileName = destFileName.replace("*", "");
		destFileName = destFileName.replace("?", "");
		destFileName = destFileName.replace("\"", "");
		destFileName = destFileName.replace("<", "");
		destFileName = destFileName.replace(">", "");
		destFileName = destFileName.replace("|", "");
		return destFileName;
	}

	public static String getLocalIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			InetAddress[] allAddrs = InetAddress.getAllByName(addr.getHostName());
			String strAllIPs = "";
			for (int i = 0; i < allAddrs.length; i++) {
				String ip = addr.getHostAddress().toString();
				strAllIPs = strAllIPs + ip + ";";
			}
			return strAllIPs;
		} catch (Exception ex) {
			System.out.println("\nerror getLocalIP:" + ex.toString());
			return null;
		}
	}

	public static String decodeText(String text) throws Exception {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb"))
			text = MimeUtility.decodeText(text);
		else
			text = new String(text.getBytes("ISO8859_1"));
		return text;
	}

	public static String encodeText(String text) {
		return encodeText(text, "gb2312");
	}

	/**
	 * 以给定的字符集进行编码处理.
	 * @param text - String, 输入的字符串
	 * @param text - String, 输入的字符集
	 */
	public static String encodeText(String text, String encoding) {
		//首先判断是否全部丄1�7 ASCII 砄1�7, 如果昄1�7, 就返囄1�7.
		boolean isAllAscii = true;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) <= ' ' || text.charAt(i) >= '~') {
				isAllAscii = false;
				break;
			}
		}
		if (isAllAscii)
			return text;
		//然后在尝试进行编砄1�7, 首先进行 BASE64 编码, 然后进行 Mime 标题的编码设罄1�7
		try {
			byte[] bytes = text.getBytes();
			if (encoding == null || encoding.length() == 0) {
				encoding = "gb2312";
			}
			bytes = text.getBytes(encoding);
			return "=?" + encoding + "?B?" + Base64.encodeToString(bytes, true) + "?=";
		} catch (Exception ex) {
			return text;
		}
	}

	/**
	 * 从ftp服务器上读取指定文件
	 * @param fileName
	 * @param ftpServerIP
	 * @param ftpServerPort
	 * @param ftpUserName
	 * @param ftpPassword
	 * @throws Exception
	 */
	public static String downFileFromFTPServer(String srcFileName, String destFilePath, String ftpServerIP, int ftpServerPort, String ftpUserName, String ftpPassword) throws Exception {

		boolean bIsFTPConnectionOpen = false;
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpServerIP, ftpServerPort);// 参数为ftp服务器IP和端叄1�7
			bIsFTPConnectionOpen = true;
			ftpClient.login(ftpUserName, ftpPassword);// 参数为用户名和密砄1�7

			ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); 
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); //丄1�7定要使用二进制模弄1�7

			InputStream ftpIn = ftpClient.retrieveFileStream(srcFileName);
			byte[] buf = new byte[204800];
			int bufsize = 0;

			//去掉原始文件的路径名
			int nPos=srcFileName.lastIndexOf(PATH_SPLIT_WINDOWS);
			if(nPos==-1){
				nPos=srcFileName.lastIndexOf(PATH_SPLIT_UNIX);
			}
			if(nPos>=0){
				srcFileName=srcFileName.substring(nPos+1);
			}
			if(destFilePath.charAt(destFilePath.length()-1)!=PATH_SPLIT_WINDOWS.charAt(0) && 
					destFilePath.charAt(destFilePath.length()-1)!=PATH_SPLIT_UNIX.charAt(0)){
				destFilePath += PATH_SPLIT_UNIX;
			}
			String destFileName = destFilePath + srcFileName;

			FileOutputStream ftpOut = new FileOutputStream(destFileName);
			while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
				ftpOut.write(buf, 0, bufsize);
			}
			ftpOut.close();
			ftpIn.close();
			
			ftpClient.logout(); 
			ftpClient.disconnect();
			bIsFTPConnectionOpen = false;
			return destFileName;
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			try {
				if (bIsFTPConnectionOpen) {
					ftpClient.logout(); 
					ftpClient.disconnect();
				}
			} catch (Exception ex1) {
				throw new Exception(ex1);
			}
		}
	}

	/**
	 * 将文件数据上传到FTP服务器
	 * @param srcFileData
	 * @param fileDataSize
	 * @param destFileName
	 * @param ftpServerIP
	 * @param ftpServerPort
	 * @param ftpUserName
	 * @param ftpPassword
	 * @param oldFileName 原来文件名称,如果传入该名称，将自动删除
	 * @throws Exception
	 */
	public static boolean uploadFileToFTPServer(byte[] srcFileData, int fileDataSize, String destFileName, String ftpServerIP, int ftpServerPort, String ftpUserName, String ftpPassword,String oldFileName) throws Exception {

		boolean bIsFTPConnectionOpen = false;
		boolean bOsOpen = false;
		FTPClient ftpClient = null;
		BufferedOutputStream bos = null;
		boolean bTransferFlag = false;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpServerIP, ftpServerPort);// 参数为ftp服务器IP和端叄1�7
			bIsFTPConnectionOpen = true;
			ftpClient.login(ftpUserName, ftpPassword);// 参数为用户名和密砄1�7

			//判断目录存不存在，不存在则创建
			ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); 
			ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
			//ftpClient.ascii();
            StringTokenizer s = new StringTokenizer(destFileName.substring(0,destFileName.lastIndexOf(PATH_SPLIT_UNIX)), PATH_SPLIT_UNIX); //sign  
            s.countTokens();  
            String pathName = ftpClient.printWorkingDirectory();  
            while(s.hasMoreElements()){  
            	String folder = (String) s.nextElement();
            	if(folder == null || folder.length() == 0) continue;
                pathName = pathName + "/" + folder;  
                String pwd = "";  
                try {  
                    pwd = ftpClient.printWorkingDirectory();
                    ftpClient.changeWorkingDirectory(folder);
                    ftpClient.changeWorkingDirectory(pwd);  
                }catch(Exception e){  
                	try {  
                        ftpClient.makeDirectory(pathName);  
                        //ftpClient.readServerResponse();  
                    } catch (Exception e1) {
                    	e1.printStackTrace();
                    }   
                    e.printStackTrace();
                }
            }  
			
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			OutputStream os = ftpClient.storeFileStream(destFileName);

			bos = new BufferedOutputStream(os);
			bOsOpen = true;
			bos.write(srcFileData, 0, fileDataSize);
			bos.close();
			bOsOpen = false;
			ftpClient.logout();
			ftpClient.disconnect();
			bIsFTPConnectionOpen = false;
			bTransferFlag = true;
		} 
		
		catch (Exception ex) {
			ex.printStackTrace();
			
			try {
				if (bOsOpen) {
					bos.close();
					bOsOpen=false;
				}
				if (bIsFTPConnectionOpen) {
					ftpClient.logout();
					ftpClient.disconnect();
					bIsFTPConnectionOpen = false;
				}
			} catch (Exception ex1) {
				throw new Exception(ex1);
			}
			throw new Exception(ex);
		} 
		
		finally {
			try {
				if (bOsOpen) {
					bos.close();
				}
				if (bIsFTPConnectionOpen) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (Exception ex1) {
				throw new Exception(ex1);
			}
		}
		
		if((oldFileName!=null)&&(oldFileName.length()>0)){
			deleteFileToFTPServer(oldFileName,ftpServerIP,ftpServerPort,ftpUserName,ftpPassword);
		}
		return bTransferFlag;
	}

	public static boolean deleteFileToFTPServer(String fileName, String ftpServerIP, int ftpServerPort, String ftpUserName, String ftpPassword){
		boolean bIsFTPConnectionOpen = false;
		FTPClient ftpClient = null;
		boolean bTransferFlag = false;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpServerIP, ftpServerPort);// 参数为ftp服务器IP和端叄1�7
			bIsFTPConnectionOpen = true;
			ftpClient.login(ftpUserName, ftpPassword);// 参数为用户名和密砄1�7

			ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); 
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

			ftpClient.deleteFile(fileName);

			ftpClient.logout();
			ftpClient.disconnect();
			bIsFTPConnectionOpen = false;
			bTransferFlag = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bIsFTPConnectionOpen) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (Exception ex1) {
				;
			}
		}
		return bTransferFlag;
	}

	/**
	 * 获得要存储文件在web服务器上的真实路径
	 * @param request
	 * @param saveSubPath 要保存的子路径
	 * 当bPersist为true时,该目录位于应用的同级目录，存储在目录中的数据不会因为应用的重新发布而丢失
	 * 当bPersist为false时,该目录位于应用的同级下级，存储在目录中的数据会因为应用的重新发布而丢失
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getRealSavePath(HttpServletRequest request, String parentPath, String subPath, boolean bPersist) {
		//获得上传路径
		String contextPath = request.getContextPath();

		//获得物理路径
		String strCurPath = request.getRealPath(contextPath);

		String fileSeperator = System.getProperty("file.separator");

		int nPosSeperator = strCurPath.lastIndexOf(fileSeperator);
		String rootPath = strCurPath.substring(0, nPosSeperator);

		if (bPersist) {
			nPosSeperator = rootPath.lastIndexOf(fileSeperator);
			rootPath = strCurPath.substring(0, nPosSeperator);
		}

		//组织路径
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

		//创建路径
		String savePath = totalSubPath + fileSeperator;
		return savePath;
	}

	/**
	 * 获得当前操系统的路径间隔符号
	 * @param request
	 * @return 
	 */
	public static String getPathSplit() {
		String fileSeperator = System.getProperty("file.separator");
		return fileSeperator;
	}

	/**************************************************************
	 * 邮编校验：非0开头的6位数字
	 ***************************************************************/
	public static boolean validatePostCode(String postCodeValue) {
		if((postCodeValue!=null)&&(postCodeValue.length()>0)){
			Pattern pattern = Pattern.compile("^[0-9]{1}[0-9]{5}$");
			Matcher matcher = pattern.matcher(postCodeValue);
			return matcher.matches();
		}else{
			return false;
		}
	}

	/**************************************************************
	 * EMAIL校验
	 ***************************************************************/
	public static boolean validateEmail(String email) {
		if((null != email)&&(email.length()>0)){
			 Pattern pattern = Pattern.compile("^([\\.a-zA-Z0-9_-])+@([\\.a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-]){1,3})$");   
			 Matcher matcher = pattern.matcher(email);
			 return matcher.matches();
		}else{
			return false;
		}
	}


	/**************************************************************
	 * 固定电话校验:可带括号和-分割符可带2-6位分机号
	 * 
	 ***************************************************************/
	public static boolean validatePhone(String phone) {
		if((null != phone)&&(phone.length()>0)){
			Pattern pattern = Pattern.compile("^(0\\d{2,3}[-]?)?\\d{7,8}([-]\\d{1,6})?$");
			Matcher matcher = pattern.matcher(phone);
			 return matcher.matches();
		}else{
			return false;
		}
	}	
	/**************************************************************
	 * 电话校验:包括手机和固定电话
	 ***************************************************************/
	public static boolean validateCallDevice(String deviceNo) {
		if((null != deviceNo)&&(deviceNo.length()>0)){
			if(!validateMobile(deviceNo)){
				if(!validatePhone(deviceNo)){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	/**************************************************************
	 * 手机校验:可带长途号0,以13,15, 18,145,147开头
	 ***************************************************************/
	public static boolean validateMobile(String mobile) {
		if((null != mobile)&&(mobile.length()>0)){
			Pattern pattern = Pattern.compile("^0?13\\d{9}$|^0?15\\d{9}$|^0?18\\d{9}$|^0?145\\d{8}$|^0?147\\d{8}$");
			Matcher matcher = pattern.matcher(mobile);
			 return matcher.matches();
		}else{
			return false;
		}
	}
	
	/**************************************************************
	 * 日期校验
	 ***************************************************************/
	public static boolean validateDate(String dateValue) {
		if ((dateValue != null) && (dateValue.length() > 0)) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateFormat.parse(dateValue);
				return true;
			} catch (Exception ex) {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.parse(dateValue);
					return true;
				} catch (Exception ex1) {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	/**************************************************************
	 * 整数校验
	 ***************************************************************/
	public static boolean validateLong(String value) {
		try{
			Long.parseLong(value);
			return true;
		}catch(Exception ex){
			return false;
		}
	}

	/**************************************************************
	 * 数字校验,包括小数
	 ***************************************************************/
	public static boolean validateNumber(String number) {
		try{
			Double.parseDouble(number);
			return true;
		}catch(Exception ex){
			return false;
		}
	}

	/**************************************************************
	 * 身份证校验
	 ***************************************************************/
	public static boolean validateIdCard(String idcard) {
		if((null != idcard)&&(idcard.length()>0)){
			Pattern pattern = Pattern.compile("^[0-9]{17}[a-zA-Z]{1}|[0-9]{15}$");
			Matcher matcher = pattern.matcher(idcard);
			 return matcher.matches();
		}else{
			return false;
		}
	}
	/**************************************************************
	 * 验证正货币金额。要求小数点后有两位数字。
	 ***************************************************************/
	public static boolean validateMoney(String money) {
		String regex = "^-?\\d+(\\.\\d{2})?$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(money);
		return m.matches();
	}
	/**************************************************************
	 * IP地址校验
	 * IP地址的寻址规则 
		1.网络寻址规则 
		A、网络地址必须唯一。 
		B、网络标识不能以数字127开头。在A类地址中，数字127保留给内部回送函数（127.1.1.1用于回路测试）。 
		C、网络标识的第一个字节不能为255。数字255作为广播地址。 
		D、网络标识的第一个字节不能为“0”，“0”表示该地址是本地主机，不能传送。 
		2.主机寻址规则 
		A、主机标识在同一网络内必须是唯一的。 
		B、主机标识的各个位不能都为“1”，如果所有位都为“1”，则该机地址是广播地址，而非主机的地址。 
		C、主机标识的各个位不能都为“0”，如果各个位都为“0”，则表示“只有这个网络”，而这个网络上没有任何主机。 
		主机的地址就是IP地址 
	 ***************************************************************/
	public static boolean validateIPAddress(String ipaddress) {
		try{
			//10.4.52.29
			String[] segments=ipaddress.split("\\.");
			if(segments.length!=4){
				return false;
			}
			int nFirstSeg=Integer.parseInt(segments[0]);
			int nSecondSeg=Integer.parseInt(segments[1]);
			int nThirdSeg=Integer.parseInt(segments[2]);
			int nFourthSeg=Integer.parseInt(segments[3]);

			if((nFirstSeg<0)||(nFirstSeg>255)||
				(nSecondSeg<0)||(nSecondSeg>255)||
				(nThirdSeg<0)||(nThirdSeg>255)||
				(nFourthSeg<0)||(nFourthSeg>255)){
				return false;
			}
			if((nFirstSeg==127) || (nFirstSeg==255) || (nFirstSeg==0)){
				return false;
			}
			if((nFirstSeg==1)&&(nSecondSeg==1)&&(nThirdSeg==1)&&(nFourthSeg==1)){
				return false;
			}
			if((nFirstSeg==0)&&(nSecondSeg==0)&&(nThirdSeg==0)&&(nFourthSeg==0)){
				return false;
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	/**************************************************************
	 * 表名称校验:
	 * 1、要以字母开头
		 2、包含字母和数字下划线
		3、不能超过30个字符
	 * 
	 ***************************************************************/
	public static boolean validateTableName(String value) {
		if((null != value)&&(value.length()>0)){
			Pattern pattern = Pattern.compile("^TBL_[a-z0-9A-Z_]{1,24}$");
			Matcher matcher = pattern.matcher(value);
			 return matcher.matches();
		}else{
			return false;
		}
	}	
	/**************************************************************
	 * 字段名称校验:
	 * 1、要以字母开头
		 2、包含字母和数字下划线
		3、不能超过30个字符
	 * 
	 ***************************************************************/
	public static boolean validateColumnName(String value) {
		if((null != value)&&(value.length()>0)){
			Pattern pattern = Pattern.compile("^[A-Za-z]{1}[A-Za-z0-9_]{1,29}$");
			Matcher matcher = pattern.matcher(value);
			 return matcher.matches();
		}else{
			return false;
		}
	}	

    /**文件重命名
     * @param path 文件目录
     * @param oldname  原来的文件名
     * @param newname 新文件名
     */

    public void renameFile(String path,String oldname,String newname){

        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile=new File(path+"/"+oldname);
            File newfile=new File(path+"/"+newname);
            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newname+"已经存在");
            else{
                oldfile.renameTo(newfile);
            } 
        }         
    }
    
    /**************************************************************
  	 * 产生一个UUID
  	 ***************************************************************/
  	public static String getUUID() {
      String id = UUID.randomUUID().toString();   
      id = id.replace("-", "");   
      return id;   
 	}
  	
  	/**
     * 根据正则表达式替换字符串
     * @param regex 正则表达式
     * @param str 原字符串
     * @param replacement 替换字符串
     * @return
     */
  	public static String replaceString(String regex, String str, String replacement) {
    	String retString = "";
    	if(str != null)
    		retString = str.replaceAll(regex, replacement);
    	return retString;
    }
  	
  	/**
     * 提取地址（删除字段全部全角及半角下空格，删除"#""-"以外符号）
     * @param str
     * @return
     */
  	public static String getAddressString(String str){
    	return replaceString("[^\\u4e00-\\u9fa50-9a-zA-Z#-]",str,"");
    }
  	
  	/**
     * 提取汉字
     * @param str
     * @return
     */
  	public static String getCNString(String str) {
		return replaceString("[^\\u4e00-\\u9fa5]",str,"");
	}
    
    /**
     * 提取英文字母
     * @param str
     * @return
     */
  	public static String getENString(String str) {
		return replaceString("[^a-zA-Z]",str,"");
	}
  	
  	/**
     * 提取数字
     * @param str
     * @return
     */
  	public static String getNumber(String str) {
		return replaceString("[^0-9]",str,"");
	}
  	
  	/**
     * 去除换行符、空格、制表符和注入sql的特殊符号
     * @param str
     * @return
     */
  	public static String getRightString(String str) {
		return replaceString("[\t\n\r\\s',\\*\\?]",str,"");
	}
  	
  	public static boolean validatePassword(String pass) {
		if((null != pass)&&(pass.length()>0)){
			Pattern pattern = Pattern.compile("[a-z0-9A-Z_]{5,10}");
			Matcher matcher = pattern.matcher(pass);
			 return matcher.matches();
		}else{
			return false;
		}
	}
}
