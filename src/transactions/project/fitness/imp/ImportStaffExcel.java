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

import dinamica.Base64;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;

/**
 * 员工导入数据验证
 * @author Administrator
 *
 */
public class ImportStaffExcel extends ImportUtil {
	
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
		InputStream inTemplate = null;
		Recordset rsFailed = this.initRecordset();
		try {
			db = getDb();
			if (inputParams.isNull("file.filename")) {
				throw new Throwable("导入文件不能为空!");
			}
			String file = inputParams.getString("file");
			// 解决excel文件名中文乱码
			String fileName = inputParams.getString("file.filename");
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/staff.xlsx";

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
			String ext = fileNameTemplate.lastIndexOf(".") == -1 ? "" : fileNameTemplate.substring(fileNameTemplate.lastIndexOf(".") + 1);
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
				// 第一列为工号
				// 第二列为别名
				// 第三列为登录帐号
				// 第四列为姓名
				// 第五列为手机号
				// 第六列为性别
				// 第七列为岗位
				
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
					
					if(iTemplateCurrentCol == 0){	//第一列为工号
						try{
							//工号
							String user_pinyin = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (user_pinyin.length() <= 0) {
								validateError.append("工号不能为空；");
							}
							rs.setValue("user_pinyin", user_pinyin);
						} catch (Exception e) {
							validateError.append("工号不能为空；");
						}

					}else if(iTemplateCurrentCol == 1){	//第二列为别名
						try{
							//别名
							String name_en = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (name_en.length() <= 0) {
								validateError.append("别名不能为空；");
							}
							rs.setValue("name_en", name_en);
						} catch (Exception e) {
							validateError.append("别名不能为空；");
						}
					}else if(iTemplateCurrentCol == 2){	//第三列为登录帐号
						try{
							String userloginpattern = "[a-zA-Z0-9_@.]{3,64}$";
							Pattern loginpatternp = Pattern.compile(userloginpattern);
							//登录账号
							String userlogin = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (userlogin.length() > 0) {
								Matcher m = loginpatternp.matcher(userlogin);
								boolean b = m.matches();
								String _queryorgmemberhead = StringUtil.replace(getResource("query-orgmemberhead.sql"), "${field_name}", "org_id");
								_queryorgmemberhead = getSQL(_queryorgmemberhead, null);
								Recordset _rsorgmemberhead = db.get(_queryorgmemberhead);
								_rsorgmemberhead.first();
								
								String memberhead = _rsorgmemberhead.getString("memberhead");
								if(!userlogin.substring(0, 2).equals(memberhead)){
									userlogin = memberhead+userlogin;
								}
								if(b){
									String _querystaffuserlogin = StringUtil.replace(getResource("query-staffuserlogin.sql"), "${field_name}", "userlogin");
									_querystaffuserlogin = StringUtil.replace(_querystaffuserlogin, "${field_value}", userlogin);
									_querystaffuserlogin = getSQL(_querystaffuserlogin, null);
									Recordset _rsstaffuserlogin = db.get(_querystaffuserlogin);
									_rsstaffuserlogin.first();
									if(_rsstaffuserlogin.getString("userlogin").equals("1")){
										validateError.append("登录账号已存在；");
									}else{
										rs.setValue("userlogin", userlogin);

										//create MD5 hash using the string: userlogin:passwd
										java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
										byte[] bt = (userlogin + ":" + "123456").getBytes();
										byte[] hash = md.digest(bt);
										String pwd = Base64.encodeToString( hash, true );
				
										//set the "passwd" parameter value to the MD5 hash 
										rs.setValue("passwd", pwd);
									}
								}else{
									validateError.append("登录账号格式不正确；");
								}
							}else{
								validateError.append("登录账号不能为空；");
							}
						} catch (Exception e) {
							validateError.append("登录账号不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 3){	//第四列为姓名
						try{
							//真实姓名
							String name = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (name.length() <= 0) {
								validateError.append("真实姓名不能为空；");
							}
							rs.setValue("name", name);
						} catch (Exception e) {
							validateError.append("真实姓名不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 4){	//第五列为手机号
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
									validateError.append("手机号已存在；");
								}else{
									rs.setValue("mobile", mobile);
								}
							}else{
							}
						} catch (Exception e) {
						}
						
					}else if(iTemplateCurrentCol == 5){	//第六列为性别
						try{
							//性别
							String querysex = getResource("query-staffsex.sql");
							querysex = getSQL(querysex, null);
							Recordset sexType = db.get(querysex);
							String sex = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (sex.length() > 0) {
								int index = super.findRecordNumber(sexType, "sex", sex);
								if (index < 0) {
									validateError.append("性别应为男、女；");
								}else{
									if(sex.equals("男")){
										rs.setValue("sex", "1");
									}else{
										rs.setValue("sex", "0");
									}
								}
							}else{
								validateError.append("性别不能为空；");
							}
						} catch (Exception e) {
							validateError.append("性别不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 6){	//第六列为岗位
						try{
							//岗位
							String queryskill = getResource("query-skill.sql");
							queryskill = getSQL(queryskill, null);
							Recordset skillType = db.get(queryskill);
							String skill = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (skill.length() > 0) {
								int index = super.findRecordNumber(skillType, "skill", skill);
								if (index < 0) {
									validateError.append("系统中不存在该岗位；");
								}else{
									String _queryskill = StringUtil.replace(getResource("query-skillvalue.sql"), "${field_name}", "skill_name");
									_queryskill = StringUtil.replace(_queryskill, "${field_value}", skill);
									_queryskill = getSQL(_queryskill, null);
									Recordset _rsskill = db.get(_queryskill);
									_rsskill.first();
									rs.setValue("skill", _rsskill.getInt("skill"));
								}
							}else{
								validateError.append("岗位不能为空；");
							}
						} catch (Exception e) {
							validateError.append("岗位不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 7){	//第七列为状态
						try{
							//岗位
							String status = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (status.length() > 0) {
								if(status.equals("在职")){
									rs.setValue("status", 1);
								}else if(status.equals("离职")){
									rs.setValue("status", 0);
								}else{
									validateError.append("状态应为离职或在职；");
								}
							}else{
								validateError.append("状态不能为空；");
							}
						} catch (Exception e) {
							validateError.append("状态不能为空；");
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
					
					//添加员工
					String insertStaff = getResource("insert.sql");
					String _insertStaff = getSQL(insertStaff, rs);
					_insertStaff = getSQL(_insertStaff, null);
					db.exec(_insertStaff);

					String insertSkill = getResource("insertskill.sql");
					String _insertSkill = getSQL(insertSkill, rs);
					_insertSkill = getSQL(_insertSkill, null);
					db.exec(_insertSkill);

					String insertUser = getResource("insert-user.sql");
					String _insertUser = getSQL(insertUser, rs);
					_insertUser = getSQL(_insertUser, null);
					db.exec(_insertUser);

					String insertRoles = getResource("insert-roles.sql");
					String _insertRoles = getSQL(insertRoles, rs);
					_insertRoles = getSQL(_insertRoles, null);
					db.exec(_insertRoles);

					String insertPasslog = getResource("insert-passlog.sql");
					String _insertPasslog = getSQL(insertPasslog, rs);
					_insertPasslog = getSQL(_insertPasslog, null);
					db.exec(_insertPasslog);

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
		rs.append("skill", Types.INTEGER);	// 岗位
		rs.append("userlogin", Types.VARCHAR);	// 登录账号
		rs.append("sex", Types.VARCHAR);	// 性别
		rs.append("user_pinyin", Types.VARCHAR);	// 工号
		rs.append("name_en", Types.VARCHAR);	// 别名
		rs.append("mobile", Types.VARCHAR);	// 手机
		rs.append("remark", Types.VARCHAR);	// 备注
		rs.append("passwd", Types.VARCHAR);	// 密码
		rs.append("status", Types.INTEGER);	// 状态
		
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
