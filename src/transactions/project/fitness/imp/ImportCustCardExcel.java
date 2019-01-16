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
 * 会员卡导入
 * @author C
 * 2016-08-06
 */
public class ImportCustCardExcel extends ImportUtil {
	
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
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/card.xlsx";

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
				// 第一列为会员姓名
				// 第二列为会员卡号
				// 第三列卡内部号
				// 第四列为会员电话
				// 第五列为其他电话
				// 第六列为会籍顾问
				// 第七列为教练
				// 第八列为卡类型
				// 第九列卡有效期始
				// 第十列有效期止
				// 第十一列卡状态
				// 第十二列备注
				
				
				// 序号
				rs.setValue("row_number", j);
				int iDataCurrentCol = 0;	//当前列号
				
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd"); 
				String datepattern = "^\\d{4}\\D+\\d{2}\\D+\\d{2}$";
				Pattern datep = Pattern.compile(datepattern);
				Date sDate = null, edate = null;
				
				//定位数据文件中，表头项的位置
				for (int iTemplateCurrentCol = 0; iTemplateCurrentCol < (null == dataRowTemplateTitle ? 0 : dataRowTemplateTitle.size()); iTemplateCurrentCol++) {
					titleNameTemplate = super.formatStringValue(dataRowTemplateTitle.get(iTemplateCurrentCol));
					for (int m = 0; m < (null == dataRowTitle ? 0 : dataRowTitle.size()); m++) {
						if(super.formatStringValue(dataRowTitle.get(m)).equals(titleNameTemplate)){
							iDataCurrentCol = m;
							break;
						}
					}

					int count = 0;
					
					if(iTemplateCurrentCol == 0){	//第一列为
						try{
							// 姓名
							String name = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (name.length() <= 0) {
								validateError.append("会员姓名不能为空；");
							}
							rs.setValue("name", name);
						} catch (Exception e) {
							validateError.append("会员姓名不能为空；");
						}

					}else if(iTemplateCurrentCol == 1){	//第二列为
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
								String _querycardcode = StringUtil.replace(getResource("query-cardcode.sql"), "${field_name}", "code");
								_querycardcode = StringUtil.replace(_querycardcode, "${field_value}", cardcode);
								_querycardcode = getSQL(_querycardcode, null);
								Recordset _rscardcode = db.get(_querycardcode);
								_rscardcode.first();
								if(_rscardcode.getString("cardcode").equals("1")){
									validateError.append("会员卡号已存在；");
								}else{
									rs.setValue("cardcode", cardcode);
								}
							}else{
								validateError.append("会员卡号不能为空；");
							}
						} catch (Exception e) {
							validateError.append("会员卡号不能为空；");
						}

					}else if(iTemplateCurrentCol == 2){	//第二列为
						try{
							//卡内码
							String incode = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (incode.length() > 0) {
								/*int len = incode.length();
								String str = "";
								for (int i = 0; i < (10-len); i++) {
									str+="0";
								}
								incode = str+incode;*/
								String _queryincode = StringUtil.replace(getResource("query-incode.sql"), "${field_name}", "incode");
								_queryincode = StringUtil.replace(_queryincode, "${field_value}", incode);
								_queryincode = getSQL(_queryincode, null);
								Recordset _rsincode = db.get(_queryincode);
								_rsincode.first();
								if(_rsincode.getString("incode").equals("1")){
									validateError.append("卡内部号已存在；");
								}
							}
							rs.setValue("incode", incode);
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 3){	//第二列为
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
									validateError.append("会员电话已存在；");
								}else{
									rs.setValue("mobile", mobile);
									/*if(isPhoneLegal(mobile))
										rs.setValue("mobile", mobile);
									else
										validateError.append("会员电话格式不正确；");*/
								}
							}else{
							}
						} catch (Exception e) {
						}

					}else if(iTemplateCurrentCol == 4){	//第二列为
						try{
							// 其他电话
							String othertel = super.formatStringValue(dataRow.get(iDataCurrentCol));
							rs.setValue("othertel", othertel);
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 5){	//第二列为
						try{
							// 性别
							String sex = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if(sex.equals("男")){
								rs.setValue("sex", "1");
							}else if(sex.equals("女")){
								rs.setValue("sex", "0");
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 6){	//第二列为
						try{
							// 证件类型
							String idcardtype = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if(idcardtype.equals("身份证")){
								rs.setValue("idcardtype", "0");
							}else if(idcardtype.equals("驾驶证")){
								rs.setValue("idcardtype", "1");
							}else{
								rs.setValue("idcardtype", "2");
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 7){	//第二列为
						try{
							// 证件号
							String idcard = super.formatStringValue(dataRow.get(iDataCurrentCol));
							rs.setValue("idcard", idcard);
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 8){	//第二列为
						try{
							// 会籍
							String querymc = getResource("query-mc.sql");
							querymc = getSQL(querymc, null);
							Recordset mcType = db.get(querymc);
							String mc = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (mc.length() > 0) {
								int index = super.findRecordNumber(mcType, "mc", mc);
								if (index < 0) {
									validateError.append("系统中不存在该会籍顾问；");
								}else{
									String _querymc = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querymc = StringUtil.replace(_querymc, "${field_value}", mc);
									_querymc = getSQL(_querymc, null);
									Recordset _rsmc = db.get(_querymc);
									_rsmc.first();
									rs.setValue("mc", _rsmc.getString("userlogin"));
								}
							}
						} catch (Exception e) {
						}

					}else if(iTemplateCurrentCol == 9){	//第二列为
						try{
							// 私教
							String querypt = getResource("query-pt.sql");
							querypt = getSQL(querypt, null);
							Recordset ptType = db.get(querypt);
							String pt = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (pt.length() > 0) {
								int index = super.findRecordNumber(ptType, "pt", pt);
								if (index < 0) {
									//validateError.append("系统中不存在该私教；");
									rs.setValue("pt", "");
								}else{
									String _querypt = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querypt = StringUtil.replace(_querypt, "${field_value}", pt);
									_querypt = getSQL(_querypt, null);
									Recordset _rspt = db.get(_querypt);
									_rspt.first();
									rs.setValue("pt", _rspt.getString("userlogin"));
								}
							}else{
								//validateError.append("私教不能为空；");
								rs.setValue("pt", "");
							}
						} catch (Exception e) {
							rs.setValue("pt", "");
						}
						
					}else if(iTemplateCurrentCol == 10){	//第二列为
						try{
							// 会员卡类型
							String querycardtype = getResource("query-cardtype.sql");
							querycardtype = getSQL(querycardtype, null);
							Recordset cardtypeType = db.get(querycardtype);
							String cardtype = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardtype.length() > 0) {
								int index = super.findRecordNumber(cardtypeType, "cardtype", cardtype);
								if (index < 0) {
									validateError.append("系统中不存在该卡类型；");
								}else{
									String _querycardtype = StringUtil.replace(getResource("query-cardtypevalue.sql"), "${field_name}", "name");
									_querycardtype = StringUtil.replace(_querycardtype, "${field_value}", cardtype);
									_querycardtype = getSQL(_querycardtype, null);
									Recordset _rscardtype = db.get(_querycardtype);
									_rscardtype.first();
									rs.setValue("cardtype", _rscardtype.getString("cardtype"));
								}
							}else{
								validateError.append("卡类型不能为空；");
							}
						} catch (Exception e) {
							validateError.append("卡类型不能为空；");
						}

					}else if(iTemplateCurrentCol == 11){	//第二列为
						try{
							//启用状态
							String starttype = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (starttype.equals("是")) {
								rs.setValue("starttype", "1");
							}else{
								rs.setValue("starttype", "2");
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 12){	//第二列为
						//starttype等于是说明该卡初次训练启用，不需要填写日期
						if(!super.formatStringValue(dataRow.get(iDataCurrentCol-1)).equals("是")){
							try{
								//卡有效期始
								String startdate = super.formatStringValue(dataRow.get(iDataCurrentCol));
								if (startdate.length() > 0) {
									Matcher m = datep.matcher(startdate);
									boolean b = m.matches();
									if(b){
										count++;
										sDate = format1.parse(startdate);
										rs.setValue("startdate", sDate);
									}else{
										validateError.append("卡有效期始格式不正确，如（2018-01-01）；");
									}
								}else{
									validateError.append("卡有效期始不能为空；");
								}
							} catch (Exception e) {
								validateError.append("卡有效期始不能为空；");
							}
						}
					}else if(iTemplateCurrentCol == 13){	//第二列为
						//starttype等于是说明该卡初次训练启用，不需要填写日期
						if(!super.formatStringValue(dataRow.get(iDataCurrentCol-2)).equals("是")){
							try{
								//有效期止
								String enddate = super.formatStringValue(dataRow.get(iDataCurrentCol));
								if (enddate.length() > 0) {
									Matcher m = datep.matcher(enddate);
									boolean b = m.matches();
									if(b){
										count++;
										edate = format1.parse(enddate);
										rs.setValue("enddate", edate);
									}else{
										validateError.append("有效期止格式不正确，如（2018-01-01）；");
									}
								}else{
									validateError.append("有效期止不能为空；");
								}
								//count等于2说明两个时间都验证通过
								if(count==2){
									//判断开始时间是否大于截止时间
									if(sDate.getTime()>edate.getTime()){
										validateError.append("卡有效期始不能大于有效期止；");
									}
									//判断开始时间时间大于当前时间，大于卡状态为未启用
									if(sDate.getTime()>new Date().getTime()){
										rs.setValue("status", 2);
									}else{
										rs.setValue("status", 1);
									}
								}
							} catch (Exception e) {
								validateError.append("有效期止不能为空；");
							}
						}

					}else if(iTemplateCurrentCol == 14){	//第二列为
						try{
							//
							String cardstatus = super.formatStringValue(dataRow.get(iDataCurrentCol));
							//0无效、1正常、2未启用、3存卡中、4挂失中、5停卡中、6过期
							String querycardstatus = getResource("query-cardstatus.sql");
							querycardstatus = getSQL(querycardstatus, null);
							Recordset cardstatusType = db.get(querycardstatus);
							int index = super.findRecordNumber(cardstatusType, "cardstatus", cardstatus);
							if (index < 0) {
								validateError.append("系统中不存在该卡状态，应为（无效、正常、未启用、存卡中、挂失中、停卡中、过期）；");
							}else{
								//卡状态
								if(cardstatus.equals("无效"))
									rs.setValue("status", 0);
								if(cardstatus.equals("正常"))
									rs.setValue("status", 1);
								if(cardstatus.equals("未启用"))
									rs.setValue("status", 2);
								if(cardstatus.equals("存卡中"))
									rs.setValue("status", 3);
								if(cardstatus.equals("挂失中"))
									rs.setValue("status", 4);
								if(cardstatus.equals("停卡中"))
									rs.setValue("status", 5);
								if(cardstatus.equals("过期"))
									rs.setValue("status", 6);
							};
						} catch (Exception e) {
						}
					}
					/*else if(iTemplateCurrentCol == 14){	//应收金额
						try{
							String doublepattern = "^([0-9]{0,9})|([0-9]{0,9}.[0-9]{1,2})$";
							Pattern doublep = Pattern.compile(doublepattern);

							//应收金额
							String normalmoney = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (normalmoney.length() > 0) {
								Matcher m = doublep.matcher(normalmoney);
								boolean b = m.matches();
								if(b){
									rs.setValue("normalmoney", normalmoney);
								}else{
									validateError.append("应收金额格式应为数字；");
								}
							}
						} catch (Exception e) {
						}
					}*/
					else if(iTemplateCurrentCol == 15){	//实收金额
						try{
							String doublepattern = "^([0-9]{0,9})|([0-9]{0,9}.[0-9]{1,2})$";
							Pattern doublep = Pattern.compile(doublepattern);

							//实收金额
							String cardmoney = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardmoney.length() > 0) {
								Matcher m = doublep.matcher(cardmoney);
								boolean b = m.matches();
								if(b){
									rs.setValue("cardmoney", cardmoney);
								}else{
									validateError.append("实收格式应为数字；");
								}
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 16){	//第二列为
						try{
							String created = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if(created.equals(null)||created.equals("")){
						        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								created = sdfdate.format(new Date());
							}
							//办卡日期
							rs.setValue("created", created);
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 17){	//第二列为
						try{
							String intpattern = "^[0-9]{1,5}$";
							Pattern intp = Pattern.compile(intpattern);

							//次卡剩余次数
							String nowcount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (nowcount.length() > 0) {
								Matcher m = intp.matcher(nowcount);
								boolean b = m.matches();
								if(b){
									rs.setValue("nowcount", Integer.parseInt(nowcount));
								}else{
									validateError.append("次卡剩余次数应为数字；");
								}
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 18){	//第二列为
						try{
							//备注
							rs.setValue("remark", super.formatStringValue(dataRow.get(iDataCurrentCol)));
						} catch (Exception e) {
						}
					}else{
						validateError.append("模版文件中存在无法处理的列项："+titleNameTemplate);
					}
				}

				//办卡日期是否为空
				if(rs.getValue("created")==null||rs.getValue("created").equals(null)||rs.getValue("created").equals("")){
			        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					rs.setValue("created", sdfdate.format(new Date()));
				}
				
				// 数据保存
				if( null != validateError && validateError.length() > 0 ){
					rs.setValue("validate_error", validateError.toString());
					rsFailed.addNew();
					rs.copyValues(rsFailed);
					validateError.delete(0, validateError.length());
				}else{
					
					//会员号
					String _querycustcode = StringUtil.replace(getResource("query-custcode.sql"), "${field_name}", "");
					_querycustcode = StringUtil.replace(_querycustcode, "${field_value}", "");
					_querycustcode = getSQL(_querycustcode, null);
					Recordset _rscustcode = db.get(_querycustcode);
					_rscustcode.first();
					rs.setValue("custcode", _rscustcode.getString("custcode"));

					//合同编号
					String _querycontractcode = StringUtil.replace(getResource("query-contractcode.sql"), "${field_name}", "");
					_querycontractcode = StringUtil.replace(_querycontractcode, "${field_value}", "");
					_querycontractcode = getSQL(_querycontractcode, null);
					Recordset _rscontractcode = db.get(_querycontractcode);
					_rscontractcode.first();
					rs.setValue("contractcode", _rscontractcode.getString("contractcode"));

					//费用记录编号
					String _queryfinancecode = StringUtil.replace(getResource("query-financecode.sql"), "${field_name}", "");
					_queryfinancecode = StringUtil.replace(_queryfinancecode, "${field_value}", "");
					_queryfinancecode = getSQL(_queryfinancecode, null);
					Recordset _rsfinancecode = db.get(_queryfinancecode);
					_rsfinancecode.first();
					rs.setValue("financecode", _rsfinancecode.getString("financecode"));
					
					db.beginTrans();
					//添加资源
					String insertGuest = getResource("insert-guest.sql");
					String _insertGuest = getSQL(insertGuest, rs);
					db.exec(_insertGuest);
					//资源到访记录
					String insertVisit = getResource("insert-visit.sql");
					String _insertVisit = getSQL(insertVisit, rs);
					db.exec(_insertVisit);
					//会员
					String insertCust = getResource("insert-customer.sql");
					String _insertCust = getSQL(insertCust, rs);
					db.exec(_insertCust);
					//合同
					String insertContract = getResource("insert-contract.sql");
					String _insertContract = getSQL(insertContract, rs);
					db.exec(_insertContract);
					//会员卡
					String insertCard = getResource("insert-card.sql");
					String _insertCard = getSQL(insertCard, rs);
					db.exec(_insertCard);
					//卡内码
					String insertCardCode = getResource("insert-cardcode.sql");
					String _insertCardCode = getSQL(insertCardCode, rs);
					db.exec(_insertCardCode);
					//费用记录
					String insertFinance = getResource("insert-finance.sql");
					String _insertFinance = getSQL(insertFinance, rs);
					db.exec(_insertFinance);
					//操作日志
					String insertLog = getResource("insert-operatelog.sql");
					String _insertLog = getSQL(insertLog, rs);
					db.exec(_insertLog);
					//消息记录
					String insertMsg = getResource("insert-message.sql");
					String _insertMsg = getSQL(insertMsg, rs);
					db.exec(_insertMsg);
					
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
		rs.append("cardcode", Types.VARCHAR);	// 卡号
		rs.append("mobile", Types.VARCHAR);	// 手机
		rs.append("othertel", Types.VARCHAR);	// 其他号码
		rs.append("mc", Types.VARCHAR);	// 会籍
		rs.append("pt", Types.VARCHAR);	// 私教
		rs.append("cardtype", Types.VARCHAR);	// 卡类型
		rs.append("startdate", Types.DATE);	// 开始时间
		rs.append("enddate", Types.DATE);	// 结束时间
		rs.append("status", Types.INTEGER);	// 卡状态
		rs.append("incode", Types.VARCHAR);	// 卡内码
		rs.append("remark", Types.VARCHAR);	// 备注
		rs.append("custcode", Types.VARCHAR);	// 会员号
		rs.append("contractcode", Types.VARCHAR);	// 合同号
		rs.append("financecode", Types.VARCHAR);	// 费用记录编号
		rs.append("cardmoney", Types.VARCHAR);	// 实收金额
		rs.append("normalmoney", Types.VARCHAR);	// 应收金额
		rs.append("sex", Types.VARCHAR);	// 性别
		rs.append("idcardtype", Types.VARCHAR);	// 证件类型
		rs.append("idcard", Types.VARCHAR);	// 证件号
		rs.append("created", Types.VARCHAR);	// 证件号
		rs.append("starttype", Types.VARCHAR);	// 启用方式
		rs.append("nowcount", Types.INTEGER);	// 剩余次数
		
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
