package transactions.project.fitness.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.ccms.imp.ExcelImportFor2k3;
import com.ccms.imp.ExcelImportFor2k7;
import com.ccms.imp.ExcelImportUtil;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;

/***
 * 资源导入
 * @author C
 * 2016-08-06
 */
public class ImportGuestExcel extends ImportUtil {
	
	Map<String, Map<String, String>> domainMap = null;// 查询出参数表
	Map<String, Map<String, String>> mappingMap = null;// 查询出错误信息对照表

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = null;
		int result = -1;
		String desc = "";
		String importLogTuid = "";
		String importBatch = "";
		InputStream in = null;
		Recordset rsFailed = this.initRecordset();
		try {
			db = getDb();
			if (inputParams.isNull("file.filename")) {
				throw new Throwable("导入文件不能为空!");
			}
			String file = inputParams.getString("file");
			// 解决excel文件名中文乱码
			String fileName = inputParams.getString("file.filename");
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/guest.xlsx";

			fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
			fileName = super.formatRequestEncoding(fileName);
			inputParams.setValue("file.filename", fileName);
			// 获取excel文件在服务器上的地址,如果不存在则创建文件夹
			String savePath = super.getRealSavePath("upload", "engineer", false);
			if ("\\".equals(File.separator)) {
				savePath = StringUtil.replace(savePath, "\\", "\\\\");
			}
			// 生成批次号
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			importBatch = "G_" + sdf.format(new Date());
			// 获取文件扩展名
			String ext = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
			// 保存的文件重命名
			fileName = super.renameSaveFile(fileName, importBatch, ext);
			// 导入前记录日志
			importLogTuid = super.insertImportLog(db, importBatch, fileName, savePath + fileName);
			File f = new File(file);
			File ft = new File(fileNameTemplate);

			// 数据处理成list
			ExcelImportUtil excel;
			if ("xls".equalsIgnoreCase(ext)) {
				if (f.length() > ExcelImportUtil.MAX_LENGTH_2K3) {
					throw new Throwable(ExcelImportUtil.ERROR_MAX_SIZE_ILLEGAL);
				}
				excel = new ExcelImportFor2k3(f);
			} else if ("xlsx".equalsIgnoreCase(ext)) {
				excel = new ExcelImportFor2k7(f);
			} else {
				throw new Throwable(ExcelImportUtil.ERROR_FORMAT_ILLEGAL);
			}
			
			//模版
			ExcelImportUtil excelTemplate;
			if ("xls".equalsIgnoreCase(ext)) {
				if (f.length() > ExcelImportUtil.MAX_LENGTH_2K3) {
					throw new Throwable(ExcelImportUtil.ERROR_MAX_SIZE_ILLEGAL);
				}
				excelTemplate = new ExcelImportFor2k3(ft);
			} else if ("xlsx".equalsIgnoreCase(ext)) {
				excelTemplate = new ExcelImportFor2k7(ft);
			} else {
				throw new Throwable(ExcelImportUtil.ERROR_FORMAT_ILLEGAL);
			}

			List<List<String>> dataList = excel.getExcelData();
			List<List<String>> dataListTemplate = excelTemplate.getExcelData();
			if (dataList.size() == 0) {
				throw new Throwable(ExcelImportUtil.ERROR_FILE_EMPTY);
			}
			//模版为空
			if (dataListTemplate.size() == 0) {
				throw new Throwable(ExcelImportUtil.ERROR_TEMPLATE_FILE_EMPTY);
			}

			int byteread = 0;
			File _file = new File(savePath);
			if (_file.exists()) {
				in = new FileInputStream(file); // 读入原文件
				@SuppressWarnings("resource")
				FileOutputStream fs = new FileOutputStream(savePath + fileName);
				byte[] buffer = new byte[1024 * 100];
				while ((byteread = in.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
			}
			f.delete();
			
			String insertGuest = getResource("insert-guest.sql");

			int rowsNum = dataList.size(); // 获取行数
			int title_line_num = 0;
			Recordset rs = this.initRecordset();
			StringBuffer validateError = new StringBuffer();
			// 读取模版，比对表头部分@oasahi 20180515
			List<String> dataRowTitle = dataList.get(0);	//第一行，标题行
			List<String> dataRowTemplateTitle = dataListTemplate.get(0);	//模版第一行，标题行
			String titleNameTemplate;
			boolean isTitleFound = false;
			for (int j = 0; j < (null == dataRowTemplateTitle ? 0 : dataRowTemplateTitle.size()); j++) {
				titleNameTemplate = super.formatStringValue(dataRowTemplateTitle.get(j));
				isTitleFound = false; //模版的表头在文件中是否匹配到了
				for (int k = 0; k < (null == dataRowTitle ? 0 : dataRowTitle.size()); k++) {
					if(super.formatStringValue(dataRowTitle.get(k)).equals(titleNameTemplate)){
						isTitleFound = true;
					}
				}
				if(!isTitleFound){
					//模版中，应该有的列，在数据文件中不存在。
					validateError.append("导入的Excel缺少列项："+titleNameTemplate+"<br>");
				}
				isTitleFound = false;
			}
			
			for (int j = (title_line_num + 1); j < rowsNum; j++) {
				rs.addNew();
				List<String> dataRow = dataList.get(j);
				// 模版中，列的约定如下：
				// 第一列为姓名
				// 第二列为性别（男、女）
				// 第三列为手机
				// 第四列为年龄范围（18岁以下、18-24、25-30、31-40、41-50、51-60、60以上）
				// 第五列为会籍
				// 第六列为获客渠道（外宣收集(DM)、其他(other)、访客介绍(VR)    、购买名单、来访(DI)、访客(WI)、其他介绍(OR)、电话咨询(TI)、会员介绍(BR)）
				
				// 序号
				rs.setValue("row_number", j);
				int iDataCurrentCol = 0;	//当前列号
				//定位数据文件中，表头项的位置
				for (int iTemplateCurrentCol = 0; iTemplateCurrentCol < (null == dataRowTemplateTitle ? 0 : dataRowTemplateTitle.size()); iTemplateCurrentCol++) {
					titleNameTemplate = super.formatStringValue(dataRowTemplateTitle.get(iTemplateCurrentCol));
					for (int m = 0; m < (null == dataRowTitle ? 0 : dataRowTitle.size()); m++) {
						if(super.formatStringValue(dataRowTitle.get(m)).equals(titleNameTemplate)){
							iDataCurrentCol = m;
							break;
						}
					}
					
					if(iTemplateCurrentCol == 0){	//第一列为
						try{
							// 姓名
							String name = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (name.length() <= 0) {
								validateError.append("姓名不能为空;");
							}
							rs.setValue("name", name);
						} catch (Exception e) {
							validateError.append("姓名不能为空;");
						}
					}else if(iTemplateCurrentCol == 1){	//第二列为
						try{
							// 性别
							String querysex = getResource("query-sex.sql");
							Recordset sexType = db.get(querysex);
							String sex = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (sex.length() > 0) {
								int index = super.findRecordNumber(sexType, "sex", sex);
								if (index < 0) {
									validateError.append("性别应为男或女;");
								}else{
									if(sex.equals("男")){
										sex = "1";
									}else if(sex.equals("女")){
										sex = "0";
									}else{
										sex = "2";
									}
									rs.setValue("sex", sex);
								}
							}else{
								validateError.append("性别不能为空;");
							}
						} catch (Exception e) {
							validateError.append("性别不能为空;");
						}
					}else if(iTemplateCurrentCol == 2){	//第三列为
						try{
							// 手机号
							String mobile = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (mobile.length() > 0) {
								String _querymobile = StringUtil.replace(getResource("query-mobile.sql"), "${field_name}", "mobile");
								_querymobile = StringUtil.replace(_querymobile, "${field_value}", mobile);
								_querymobile = getSQL(_querymobile, null);
								Recordset _rsmobile = db.get(_querymobile);
								_rsmobile.first();
								if(_rsmobile.getString("mobile").equals("1")){
									validateError.append("该手机号码已存在;");
								}else{
									if(isPhoneLegal(mobile))
										rs.setValue("mobile", mobile);
									else
										validateError.append("该手机号码格式不正确;");
								}
							}else{
								validateError.append("手机不能为空;");
							}
						} catch (Exception e) {
							validateError.append("手机不能为空;");
						}

					}else if(iTemplateCurrentCol == 3){	//第四列为
						try{
							// 年龄范围
							String queryage = getResource("query-age.sql");
							Recordset ageType = db.get(queryage);
							String age = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (age.length() > 0) {
								int index = super.findRecordNumber(ageType, "age", age);
								if (index < 0) {
									validateError.append("系统中不存在该年龄范围;");
								}else{
									String _queryage = StringUtil.replace(getResource("query-agevlue.sql"), "${field_name}", "domain_text_cn");
									_queryage = StringUtil.replace(_queryage, "${field_value}", age);
									_queryage = getSQL(_queryage, null);
									Recordset _rsage = db.get(_queryage);
									_rsage.first();
									rs.setValue("age", _rsage.getString("age"));
								}
							}else{
								validateError.append("年龄范围不能为空;");
							}
						} catch (Exception e) {
							validateError.append("年龄范围不能为空;");
						}
					}else if(iTemplateCurrentCol == 4){	//第五列为
						try{
							// 会籍
							String querymc = getResource("query-mc.sql");
							querymc = getSQL(querymc, null);
							Recordset mcType = db.get(querymc);
							String mc = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (mc.length() > 0) {
								int index = super.findRecordNumber(mcType, "mc", mc);
								if (index < 0) {
									validateError.append("系统中不存在该会籍;");
								}else{
									String _querymc = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querymc = StringUtil.replace(_querymc, "${field_value}", mc);
									_querymc = getSQL(_querymc, null);
									Recordset _rsmc = db.get(_querymc);
									_rsmc.first();
									rs.setValue("mc", _rsmc.getString("mc"));
								}
							}else{
							}
						} catch (Exception e) {
						}

					}else if(iTemplateCurrentCol == 5){	//第六列为
						try{
							// 获客渠道
							String type = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if(type.indexOf("（")>=1){
								type = type.substring(0, type.indexOf("（"));
							}
							if(type.indexOf("(")>=1){
								type = type.substring(0, type.indexOf("("));
							}
							if (type.length() > 0) {
								String _querytype = StringUtil.replace(getResource("query-typevalue.sql"), "${field_name}", "param_text");
								_querytype = StringUtil.replace(_querytype, "${field_value}", type);
								_querytype = getSQL(_querytype, null);
								Recordset _rstype = db.get(_querytype);
								if(_rstype.getRecordCount()==0){
									validateError.append("获客渠道不存在；");
								}else{
									_rstype.first();
									rs.setValue("type", _rstype.getString("type"));
								}
							}else{
								validateError.append("获客渠道不能为空;");
							}
						} catch (Exception e) {
							validateError.append("获客渠道不能为空;");
						}

					}else{
						validateError.append("模版文件中存在无法处理的列项："+titleNameTemplate);
					}
				}
					
				// 数据保存
				if( null != validateError && validateError.length() > 0 ){
					rs.setValue("validate_error", validateError.toString());
					rsFailed.addNew();
					rs.copyValues(rsFailed);
					validateError.delete(0, validateError.length());
				}else{
					db.beginTrans();
					String _insertGuest = getSQL(insertGuest, rs);
					db.exec(_insertGuest);
					db.commit();
				}
			}
			result = 1;
			desc = "";
		} catch (Throwable t) {
			result = 0;
			desc = "导入失败：" + t.getMessage();
			t.printStackTrace();
		} finally {
			try{
				if (null != in) {
					in.close(); // 关闭流
				}
				super.updateImportLog(db, importLogTuid, result, StringUtil.replace(desc, "'", "''"));
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		publish("query-result", this.createResultRecordset(result, desc));
		publish("query-failed", rsFailed);
		return rc;
	}
	
	private Recordset initRecordset() throws RecordsetException {
		Recordset rs = new Recordset();
		rs.append("row_number", Types.INTEGER);
		rs.append("name", Types.VARCHAR);	// 姓名
		rs.append("sex", Types.VARCHAR);	// 性别
		rs.append("mobile", Types.VARCHAR);	// 手机
		rs.append("age", Types.VARCHAR);	// 年龄范围
		rs.append("mc", Types.VARCHAR);	// 会籍
		rs.append("type", Types.VARCHAR);	// 获客渠道
		rs.append("resultcode", Types.INTEGER);
		rs.append("resultdesc", Types.VARCHAR);
		rs.append("validate_error", Types.VARCHAR);
		return rs;
	}

	private Recordset createResultRecordset(int resultcode, String resultdesc) throws RecordsetException {
		Recordset rs = new Recordset();
		rs.append("resultcode", Types.INTEGER);
		rs.append("resulterror", Types.VARCHAR);
		rs.addNew();
		rs.setValue("resultcode", resultcode);
		rs.setValue("resulterror", resultdesc);
		return rs;
	}
	/**  
     * 大陆号码或香港号码均可  
     */    
    public static boolean isPhoneLegal(String str)throws PatternSyntaxException {    
        return isChinaPhoneLegal(str);    
    }    
    
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {    
        String regExp = "^[0-9]{11}$";       
        Pattern p = Pattern.compile(regExp);    
        Matcher m = p.matcher(str);    
        return m.matches();    
    }    
    
}
