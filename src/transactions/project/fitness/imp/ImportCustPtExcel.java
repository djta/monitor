package transactions.project.fitness.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * 会员私教课导入
 * @author C
 * 2016-08-06
 */
public class ImportCustPtExcel extends ImportUtil {
	
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
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/pt.xlsx";

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
				// 第一列为会员编号
				// 第二列为私教课程名称
				// 第三列为教练名称
				// 第四列为购买课时
				// 第五列为销售员1
				// 第六列为销售员2
				// 第七列获客渠道（体验课、体侧、朋友转介绍、场开）
				// 第八列折扣金额
				// 第九列备注
				
				// 序号
				rs.setValue("row_number", j);
				int iDataCurrentCol = 0;	//当前列号
				String querysalemember = getResource("query-salemember.sql");
				querysalemember = getSQL(querysalemember, null);
				Recordset salememberType = db.get(querysalemember);

				String ptcount = "";
				
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
					Pattern intp = Pattern.compile(intpattern);
					
					if(iTemplateCurrentCol == 0){	//第一列为
						try{
							//会员卡号
							String cardcode = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardcode.length() > 0) {
								/*int len = cardcode.length();
								String str = "";
								for (int i = 0; i < (8-len); i++) {
									str+="0";
								}
								cardcode = str+cardcode;*/
								System.out.println("-----"+cardcode);
								String _querycardcode= StringUtil.replace(getResource("query-customercode.sql"), "${field_name}", "code");
								_querycardcode = StringUtil.replace(_querycardcode, "${field_value}", cardcode);
								_querycardcode = getSQL(_querycardcode, null);
								Recordset _rscardcode = db.get(_querycardcode);
								if(_rscardcode.getRecordCount()==0){
									validateError.append("会员卡号不存在；");
								}else{
									_rscardcode.first();
									rs.setValue("customercode", _rscardcode.getString("customercode"));
								}
							}else{
								validateError.append("会员卡号不能为空；");
							}
						} catch (Exception e) {
							validateError.append("会员卡号不能为空；");
						}
					}else if(iTemplateCurrentCol == 1){	//第二列为
						try{
							//私教课程名称
							String queryptlevelcode = getResource("query-ptlevelname.sql");
							queryptlevelcode = getSQL(queryptlevelcode, null);
							Recordset ptlevelcodeType = db.get(queryptlevelcode);
							String ptlevelcode = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptlevelcode.length() > 0) {
								int index = super.findRecordNumber(ptlevelcodeType, "ptlevelname", ptlevelcode);
								if (index < 0) {
									validateError.append("系统中不存在该私教课程；");
								}else{
									String _querypdcode = StringUtil.replace(getResource("query-ptlevelcode.sql"), "${field_name}", "ptlevelname");
									_querypdcode = StringUtil.replace(_querypdcode, "${field_value}", ptlevelcode);
									_querypdcode = getSQL(_querypdcode, null);
									Recordset _rspdcode = db.get(_querypdcode);
									_rspdcode.first();
									rs.setValue("ptlevelcode", _rspdcode.getString("code"));
									//单价
									rs.setValue("ptfee", _rspdcode.getString("ptfee"));
								}
							}else{
								validateError.append("私教课程名称不能为空；");
							}
						} catch (Exception e) {
							validateError.append("私教课程名称不能为空；");
						}
					}else if(iTemplateCurrentCol == 2){	//第三列为
						try{
							// 私教
							String querypt = getResource("query-pt.sql");
							querypt = getSQL(querypt, null);
							Recordset ptType = db.get(querypt);
							String pt = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (pt.length() > 0) {
								int index = super.findRecordNumber(ptType, "pt", pt);
								if (index < 0) {
									validateError.append("系统中不存在该私教；");
								}else{
									String _querypt = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querypt = StringUtil.replace(_querypt, "${field_value}", pt);
									_querypt = getSQL(_querypt, null);
									Recordset _rspt = db.get(_querypt);
									_rspt.first();
									rs.setValue("pt", _rspt.getString("userlogin"));
								}
							}else{
								validateError.append("私教不能为空；");
							}
						} catch (Exception e) {
							validateError.append("私教不能为空；");
						}

					}else if(iTemplateCurrentCol == 3){	//第四列为
						try{
							//判断该私教课教练能否上
							if(rs.getValue("pt")!=null&&rs.getValue("ptlevelcode")!=null){
								String _queryptdeflimit = StringUtil.replace(getResource("query-ptdeflimit.sql"), "${field_name}", "ptdefcode");
								_queryptdeflimit = StringUtil.replace(_queryptdeflimit, "${field_value}", rs.getValue("ptlevelcode").toString());
								_queryptdeflimit = StringUtil.replace(_queryptdeflimit, "${field_name1}", "pt");
								_queryptdeflimit = StringUtil.replace(_queryptdeflimit, "${field_value1}", rs.getValue("pt").toString());
								_queryptdeflimit = getSQL(_queryptdeflimit, null);
								Recordset _rsptdeflimit = db.get(_queryptdeflimit);
								_rsptdeflimit.first();
								if(Integer.parseInt(_rsptdeflimit.getString("count"))==0){
									validateError.append("该私教课程未分配该教练，不能授课；");
								}
							}
							
							//购买课时
							ptcount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptcount.length() > 0) {
								Matcher m = intp.matcher(ptcount);
								boolean b = m.matches();
								if(b){
									rs.setValue("ptcount", Integer.parseInt(ptcount));
								}else{
									validateError.append("购买课时格式应为整数；");
								}
							}else{
								validateError.append("购买课时不能为空；");
							}
						} catch (Exception e) {
							validateError.append("购买课时不能为空；");
						}

					}else if(iTemplateCurrentCol == 4){	//第四列为
						try{
							//剩余课时
							String ptleftcount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptleftcount.length() > 0) {
								Matcher m = intp.matcher(ptleftcount);
								boolean b = m.matches();
								if(b){
									if(Integer.parseInt(ptleftcount)>Integer.parseInt(ptcount)){
										validateError.append("剩余课时不能大于购买课时；");
									}else{
										rs.setValue("ptleftcount", Integer.parseInt(ptleftcount));
									}
								}else{
									validateError.append("剩余课时格式应为整数；");
								}
							}else{
								validateError.append("剩余课时不能为空；");
							}
						} catch (Exception e) {
							validateError.append("剩余课时不能为空；");
						}

					}else if(iTemplateCurrentCol == 5){	//第五列为
						try{
							// 销售员1
							String salemember = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (salemember.length() > 0) {
								int index = super.findRecordNumber(salememberType, "name", salemember);
								if (index < 0) {
									validateError.append("系统中不存在该销售员1；");
								}else{
									String _querysalemember = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querysalemember = StringUtil.replace(_querysalemember, "${field_value}", salemember);
									_querysalemember = getSQL(_querysalemember, null);
									Recordset _rssalemember = db.get(_querysalemember);
									_rssalemember.first();
									rs.setValue("salemember", _rssalemember.getString("userlogin"));
								}
							}else{
								validateError.append("销售员1不能为空；");
							}
						} catch (Exception e) {
							validateError.append("销售员1不能为空；");
						}

					}else if(iTemplateCurrentCol == 6){	//第六列为
						try{
							// 销售员2
							String salemember1 = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (salemember1.length() > 0) {
								int index = super.findRecordNumber(salememberType, "name", salemember1);
								if (index < 0) {
									validateError.append("系统中不存在该销售员2；");
								}else{
									String _querysalemember = StringUtil.replace(getResource("query-staffvalue.sql"), "${field_name}", "name");
									_querysalemember = StringUtil.replace(_querysalemember, "${field_value}", salemember1);
									_querysalemember = getSQL(_querysalemember, null);
									Recordset _rssalemember = db.get(_querysalemember);
									_rssalemember.first();
									rs.setValue("salemember1", _rssalemember.getString("userlogin"));
								}
							}else{
								rs.setValue("salemember1", salemember1);
							}
						} catch (Exception e) {
							rs.setValue("salemember1", "");
						}

					}else if(iTemplateCurrentCol == 7){	//第七列为
						try{
							// 获客渠道
							String querysource = getResource("query-source.sql");
							querysource = getSQL(querysource, null);
							Recordset sourceType = db.get(querysource);
							String source = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (source.length() > 0) {
								int index = super.findRecordNumber(sourceType, "source", source);
								if (index < 0) {
									validateError.append("系统中不存在该获客渠道；");
								}else{
									String _querysource = StringUtil.replace(getResource("query-sourcevalue.sql"), "${field_name}", "domain_text_cn");
									_querysource = StringUtil.replace(_querysource, "${field_value}", source);
									_querysource = getSQL(_querysource, null);
									Recordset _rssource = db.get(_querysource);
									_rssource.first();
									rs.setValue("source", _rssource.getString("domain_value"));
								}
							}else{
								validateError.append("获客渠道不能为空；");
							}
						} catch (Exception e) {
							validateError.append("获客渠道不能为空；");
						}

					}else if(iTemplateCurrentCol == 8){	//第七列为
						try{
							String doublepattern = "^([0-9]{0,9})|([0-9]{0,9}.[0-9]{1,2})$";
							Pattern doublep = Pattern.compile(doublepattern);
	
							//折扣金额
							String ptamount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptamount.length() > 0) {
								Matcher m = doublep.matcher(ptamount);
								boolean b = m.matches();
								if(b){
									rs.setValue("ptamount", Double.parseDouble(ptamount));
								}else{
									validateError.append("折扣金额格式应为数字；");
								}
							}else{
								validateError.append("折扣金额不能为空；");
							}
						} catch (Exception e) {
							validateError.append("折扣金额不能为空；");
						}

					}else if(iTemplateCurrentCol == 9){	//第七列为
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

					//费用记录编号
					String _queryfinancecode = StringUtil.replace(getResource("query-financecode.sql"), "${field_name}", "");
					_queryfinancecode = StringUtil.replace(_queryfinancecode, "${field_value}", "");
					_queryfinancecode = getSQL(_queryfinancecode, null);
					Recordset _rsfinancecode = db.get(_queryfinancecode);
					_rsfinancecode.first();
					rs.setValue("financecode", _rsfinancecode.getString("financecode"));
					
					//每节课多少天上完
					String _queryptday = StringUtil.replace(getResource("query-ptday.sql"), "${field_name}", "");
					_queryptday = StringUtil.replace(_queryptday, "${field_value}", "");
					_queryptday = getSQL(_queryptday, null);
					Recordset _rsptday = db.get(_queryptday);
					_rsptday.first();
					String ptday = _rsptday.getString("param_value");

					//合同编号
					String _querycontractcode = StringUtil.replace(getResource("query-contractcode.sql"), "${field_name}", "");
					_querycontractcode = StringUtil.replace(_querycontractcode, "${field_value}", "");
					_querycontractcode = getSQL(_querycontractcode, null);
					Recordset _rscontractcode = db.get(_querycontractcode);
					_rscontractcode.first();
					rs.setValue("contractcode", _rscontractcode.getString("contractcode"));
					
					//价格
					rs.setValue("money", (Double.parseDouble(rs.getValue("ptfee").toString())*Integer.parseInt(ptcount)));

					Calendar ca = Calendar.getInstance();
					ca.add(Calendar.DATE, (Integer.parseInt(ptday)*Integer.parseInt(ptcount)));// num为增加的天数，可以改变的
					rs.setValue("ptenddate", ca.getTime());
					db.beginTrans();
					//购买私教课
					String contractType = getResource("insert-contract.sql");
					String _contractTypeType = getSQL(contractType, rs);
					db.exec(_contractTypeType);

					//私教课
					String ptrestType = getResource("insert-ptrest.sql");
					String _ptrestType = getSQL(ptrestType, rs);
					db.exec(_ptrestType);

					//费用记录
					String financeType = getResource("insert-finance.sql");
					String _financeType = getSQL(financeType, rs);
					db.exec(_financeType);

					//操作日志
					String operatelogType = getResource("insert-operatelog.sql");
					String _operatelogType = getSQL(operatelogType, rs);
					db.exec(_operatelogType);

					//消息
					String messageType = getResource("insert-message.sql");
					String _messageType = getSQL(messageType, rs);
					db.exec(_messageType);
					
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
		rs.append("customercode", Types.VARCHAR);	// 会员编号
		rs.append("ptlevelcode", Types.VARCHAR);	// 私教类型
		rs.append("ptcount", Types.INTEGER);	// 购买课时
		rs.append("ptleftcount", Types.INTEGER);	// 剩余课时
		rs.append("ptenddate", Types.DATE);	// 私教课结束日期
		rs.append("ptfee", Types.VARCHAR);	// 单价
		rs.append("salemember", Types.VARCHAR);	// 销售员1
		rs.append("salemember1", Types.VARCHAR);	// 销售员2
		rs.append("pt", Types.VARCHAR);	// 私教
		rs.append("source", Types.VARCHAR);	// 获客渠道
		rs.append("money", Types.DOUBLE);	// 价格
		rs.append("ptamount", Types.DOUBLE);	// 折扣金额
		rs.append("financecode", Types.VARCHAR);	// 费用记录编号
		rs.append("remark", Types.VARCHAR);	// 备注
		rs.append("contractcode", Types.VARCHAR);	// 合同号
		
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
