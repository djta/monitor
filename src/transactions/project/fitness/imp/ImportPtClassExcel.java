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
 * 私教课导入
 * @author C
 * 2016-08-06
 */
public class ImportPtClassExcel extends ImportUtil {
	
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
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/ptclass.xlsx";

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
				// 第一列为私教课名称
				// 第二列为课时(单位：分钟)
				// 第三列为预约间隔（00、15、30、45）
				// 第四列为是否赠送课
				// 第五列为单价
				// 第六列为提成金额
				// 第七列为是否启用
				// 第八列是否可延期
				// 第九列备注
				
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
					
					String intpattern = "^[0-9]{0,9}$";
					String doublepattern = "^([0-9]{0,9})|([0-9]{0,9}.[0-9]{1,2})$";
					//对()的用法总结：将()中的表达式作为一个整体进行处理，必须满足他的整体结构才可以。
					//(.[0-9]+)? ：表示()中的整体出现一次或一次也不出现
					Pattern intp = Pattern.compile(intpattern);
					Pattern doublep = Pattern.compile(doublepattern);

					String querytype = getResource("query-type.sql");
					querytype = getSQL(querytype, null);
					Recordset isType = db.get(querytype);

					// 是否赠送课
					String reatetype = "";

					if(iTemplateCurrentCol == 0){	//第一列为私教课名称
						try{
							// 私教课名称
							String ptlevelname = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptlevelname.length() > 0) {
								String _queryptlevelname = StringUtil.replace(getResource("query-ptlevelname.sql"), "${field_name}", "ptlevelname");
								_queryptlevelname = StringUtil.replace(_queryptlevelname, "${field_value}", ptlevelname);
								_queryptlevelname = getSQL(_queryptlevelname, null);
								Recordset _rsptlevelname = db.get(_queryptlevelname);
								_rsptlevelname.first();
								if(_rsptlevelname.getString("ptlevelname").equals("1")){
									validateError.append("私教课名称已存在；");
								}else{
									rs.setValue("ptlevelname", ptlevelname);
								}
							}else{
								validateError.append("私教课名称不能为空；");
							}
						} catch (Exception e) {
							validateError.append("私教课名称不能为空；");
						}


					}else if(iTemplateCurrentCol == 1){	//第二列为课时(单位：分钟)
						try{
							//课时(单位：分钟)
							String times = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (times.length() > 0) {
								Matcher m = intp.matcher(times);
								boolean b = m.matches();
								if(b){
									rs.setValue("times", times);
								}else{
									validateError.append("课时(单位：分钟)格式应为整数；");
								}
							}else{
								validateError.append("课时(单位：分钟)不能为空；");
							}
						} catch (Exception e) {
							validateError.append("课时(单位：分钟)不能为空；");
						}
					}else if(iTemplateCurrentCol == 2){	//第三列为预约间隔（00、15、30、45）
						try{
							//预约间隔
							String queryspacing = getResource("query-spacing.sql");
							queryspacing = getSQL(queryspacing, null);
							Recordset spacingType = db.get(queryspacing);
							String spacing = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (spacing.length() > 0) {
								int index = super.findRecordNumber(spacingType, "spacing", spacing);
								if (index < 0) {
									validateError.append("预约间隔为（00、15、30、45）；");
								}else{
									rs.setValue("spacing", spacing);
								}
							}else{
								validateError.append("预约间隔不能为空；");
							}
						} catch (Exception e) {
							validateError.append("预约间隔不能为空；");
						}

					}else if(iTemplateCurrentCol == 3){	//第四列为否赠送课
						try{
							reatetype = super.formatStringValue(dataRow.get(iDataCurrentCol));
							// 是否赠送课
							if (reatetype.length() > 0) {
								int index = super.findRecordNumber(isType, "type", reatetype);
								if (index < 0) {
									validateError.append("是否赠送课应该是或否；");
								}else{
									//0否，1是
									if(reatetype.equals("否"))
										rs.setValue("reatetype", 0);
									else 
										rs.setValue("reatetype", 1);
								}
							}else{
								validateError.append("是否赠送课不能为空；");
							}
						} catch (Exception e) {
							validateError.append("是否赠送课不能为空；");
						}
					}else if(iTemplateCurrentCol == 4){	//第五列为单价
						try{
							//单价
							String ptfee = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptfee.length() > 0) {
								Matcher m = doublep.matcher(ptfee);
								boolean b = m.matches();
								if(b){
									rs.setValue("ptfee", ptfee);
								}else{
									validateError.append("单价格式应为数字；");
								}
							}else{
								validateError.append("单价不能为空；");
							}
						} catch (Exception e) {
							validateError.append("单价不能为空；");
						}

					}else if(iTemplateCurrentCol == 5){	//第六列为提成金额
						try{
							//提成金额
							String scale = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (scale.length() > 0) {
								Matcher m = doublep.matcher(scale);
								boolean b = m.matches();
								if(b){
									rs.setValue("scale", scale);
								}else{
									validateError.append("提成金额格式应为数字；");
								}
							}else{
								validateError.append("提成金额不能为空；");
							}
						} catch (Exception e) {
							validateError.append("提成金额不能为空；");
						}

					}else if(iTemplateCurrentCol == 6){	//第七列为是否启用
						try{
							// 是否启用
							String status = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (status.length() > 0) {
								int index = super.findRecordNumber(isType, "type", status);
								if (index < 0) {
									validateError.append("是否启用应该是或否；");
								}else{
									//0否，1是
									if(reatetype.equals("否"))
										rs.setValue("status", 0);
									else 
										rs.setValue("status", 1);
								}
							}else{
								validateError.append("是否启用不能为空；");
							}
						} catch (Exception e) {
							validateError.append("是否启用不能为空；");
						}

					}else if(iTemplateCurrentCol == 7){	//第八列为是否可延期
						try{
							// 是否可延期
							String is_delay = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (is_delay.length() > 0) {
								int index = super.findRecordNumber(isType, "type", is_delay);
								if (index < 0) {
									validateError.append("是否可延期应该是或否；");
								}else{
									//是否可延期：0否，1是
									if(reatetype.equals("否"))
										rs.setValue("is_delay", 0);
									else 
										rs.setValue("is_delay", 1);
								}
							}else{
								validateError.append("是否可延期不能为空；");
							}
						} catch (Exception e) {
							validateError.append("是否可延期不能为空；");
						}

					}else if(iTemplateCurrentCol == 8){	//第九列为备注
						try{
							//备注
							rs.setValue("remark", super.formatStringValue(dataRow.get(iDataCurrentCol)));
						} catch (Exception e) {
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
					//添加私教类型
					String ptClassType = getResource("insert.sql");
					String _ptClassType = getSQL(ptClassType, rs);
					db.exec(_ptClassType);
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
		rs.append("ptlevelname", Types.VARCHAR);	// 私教课名称
		rs.append("reatetype", Types.INTEGER);	// 是否赠送课
		rs.append("ptfee", Types.VARCHAR);	// 单价
		rs.append("scale", Types.VARCHAR);	// 提成金额
		rs.append("status", Types.INTEGER);	// 是否启用
		rs.append("is_delay", Types.INTEGER);	// 是否可延期
		rs.append("times", Types.VARCHAR);	// 课时(单位：分钟)
		rs.append("spacing", Types.VARCHAR);	// 预约间隔（分钟）
		rs.append("remark", Types.VARCHAR);	// 备注
		
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
