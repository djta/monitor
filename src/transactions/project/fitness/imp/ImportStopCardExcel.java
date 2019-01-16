package transactions.project.fitness.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Types;
import java.text.DateFormat;
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
 * 停卡导入
 * @author C
 * 2016-08-06
 */
public class ImportStopCardExcel extends ImportUtil {
	
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
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/stopcard.xlsx";

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
				// 第一列为会员卡号
				// 第二列为起停日期
				// 第三列为预停天数
				// 第四列为停卡原因
				// 第五列为备注
				
				// 序号
				rs.setValue("row_number", j);
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd"); 

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
					
					if(iTemplateCurrentCol == 0){	//第一列为会员卡号
						try{
							// 会员卡号
							String cardcode = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardcode.length() > 0) {
								/*int len = cardcode.length();
								String str = "";
								for (int i = 0; i < (8-len); i++) {
									str+="0";
								}
								cardcode = str+cardcode;*/
								System.out.println("-----"+cardcode);
								String _querycardcode= StringUtil.replace(getResource("query-cardcode.sql"), "${field_name}", "code");
								_querycardcode = StringUtil.replace(_querycardcode, "${field_value}", cardcode);
								_querycardcode = getSQL(_querycardcode, null);
								Recordset _rscardcode = db.get(_querycardcode);
								_rscardcode.first();
								if(Integer.parseInt(_rscardcode.getString("cardcount"))==0){
									validateError.append("该会员卡号不存在；");
								}else{
									String _querycardstatus= StringUtil.replace(getResource("query-cardstatus.sql"), "${field_name}", "code");
									_querycardstatus = StringUtil.replace(_querycardstatus, "${field_value}", cardcode);
									_querycardstatus = getSQL(_querycardstatus, null);
									Recordset _rscardstatus = db.get(_querycardstatus);
									_rscardstatus.first();
									if(_rscardstatus.getString("status").equals("1")){
										String _querycardinfo= StringUtil.replace(getResource("query-cardinfo.sql"), "${field_name}", "code");
										_querycardinfo = StringUtil.replace(_querycardinfo, "${field_value}", cardcode);
										_querycardinfo = getSQL(_querycardinfo, null);
										Recordset _rscardinfo = db.get(_querycardinfo);
										_rscardinfo.first();
										rs.setValue("cardstartdate", format1.parse(_rscardinfo.getString("startdate")));
										rs.setValue("cardenddate", format1.parse(_rscardinfo.getString("enddate")));
										rs.setValue("daysremain", 0);
										rs.setValue("customercode", _rscardinfo.getString("customercode"));
										rs.setValue("cardtype", _rscardinfo.getString("cardtype"));
										
										rs.setValue("cardcode", cardcode);
									}else{
										validateError.append("该会员卡号不能停卡，请确认状态；");
									}
								}
							}else{
								validateError.append("会员卡号不能为空；");
							}
						} catch (Exception e) {
							validateError.append("会员卡号不能为空；");
						}

					}else if(iTemplateCurrentCol == 1){	//第二列为起停日期
						try{
							String datepattern = "^\\d{4}\\D+\\d{2}\\D+\\d{2}$";
							Pattern datep = Pattern.compile(datepattern);
							//起停日期
							String stopcarddate = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (stopcarddate.length() > 0) {
								Matcher m = datep.matcher(stopcarddate);
								boolean b = m.matches();
								if(b){
									if(format1.parse(stopcarddate).getTime()>new Date().getTime()){
										validateError.append("起停日期不能大于当前日期；");
									}else{
										rs.setValue("stopcarddate", format1.parse(stopcarddate));
									}
								}else{
									validateError.append("起停日期格式不正确，如（2018-01-01）；");
								}
							}else{
								validateError.append("起停日期不能为空；");
							}
						} catch (Exception e) {
							validateError.append("起停日期不能为空；");
						}

					}else if(iTemplateCurrentCol == 2){	//第三列为预停天数
						try{
							String intpattern = "^[0-9]{0,9}$";
							Pattern intp = Pattern.compile(intpattern);
							//预停天数
							String prestopdays = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (prestopdays.length() > 0) {
								Matcher m = intp.matcher(prestopdays);
								boolean b = m.matches();
								if(b){
									rs.setValue("prestopdays", Integer.parseInt(prestopdays));
								}else{
									validateError.append("预停天数格式应为整数；");
								}
							}else{
								validateError.append("预停天数不能为空；");
							}
						} catch (Exception e) {
							validateError.append("预停天数不能为空；");
						}

					}else if(iTemplateCurrentCol == 3){	//第四列为停卡原因
						try{
							//停卡原因
							rs.setValue("reason", super.formatStringValue(dataRow.get(iDataCurrentCol)));
						} catch (Exception e) {
						}

					}else if(iTemplateCurrentCol == 4){	//第五列为备注
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
					//停卡
					String stopcardType = getResource("insert.sql");
					String _stopcardType = getSQL(stopcardType, rs);
					db.exec(_stopcardType);

					String operaType = getResource("insert-opera.sql");
					String _operaType = getSQL(operaType, rs);
					db.exec(_operaType);
					
					String message = getResource("insert-message.sql");
					String _message = getSQL(message, rs);
					db.exec(_message);
					
					String mcmessage = getResource("insert-mc-message.sql");
					String _mcmessage = getSQL(mcmessage, rs);
					db.exec(_mcmessage);
					
					String card = getResource("update-card.sql");
					String _card = getSQL(card, rs);
					db.exec(_card);
					
					String cardrelatecode = getResource("update-cardrelatecode.sql");
					String _cardrelatecode = getSQL(cardrelatecode, rs);
					db.exec(_cardrelatecode);
					
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
		rs.append("cardcode", Types.VARCHAR);	// 会员卡号
		rs.append("customercode", Types.VARCHAR);	// 会员编号
		rs.append("cardtype", Types.VARCHAR);	// 卡类型
		rs.append("daysremain", Types.INTEGER);	// 剩余停卡天数
		rs.append("stopcarddate", Types.DATE);	// 起停日期
		rs.append("prestopdays", Types.INTEGER);	// 预停天数
		rs.append("reason", Types.VARCHAR);	// 停卡原因
		rs.append("cardstartdate", Types.DATE);	// 卡开始日期
		rs.append("cardenddate", Types.DATE);	// 卡结束日期
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
