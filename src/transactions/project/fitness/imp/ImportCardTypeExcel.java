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
 * 会员卡导入
 * @author C
 * 2016-08-06
 */
public class ImportCardTypeExcel extends ImportUtil {
	
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
			String path = super.getContext().getRealPath(super.getRequest().getRequestURI());
			String fileNameTemplate = super.getContext().getRealPath("/")+"/erpclubdoc/template/cardtype.xlsx";
			
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
				// 第一列为卡类型名称
				// 第二列为卡种分类
				// 第三列为卡类别
				// 第四列为额外赠送天数
				// 第五列为赠送免费课节数
				// 第六列为最大使用人数
				// 第七列为可用次数（次卡）
				// 第八列有效天数
				// 第九列卡价格
				// 第十列最低价
				// 第十一列备注
				
				// 序号
				rs.setValue("row_number", j);
				int iDataCurrentCol = 0;	//当前列号
				Matcher m = null;
				String type ="";
				String cardfee = "";
				
				
				String intpattern = "^[0-9]{0,9}$";
				String doublepattern = "^([0-9]{0,9})|([0-9]{0,9}.[0-9]{1,2})$";
				//对()的用法总结：将()中的表达式作为一个整体进行处理，必须满足他的整体结构才可以。
				//(.[0-9]+)? ：表示()中的整体出现一次或一次也不出现
				Pattern intp = Pattern.compile(intpattern);
				Pattern doublep = Pattern.compile(doublepattern);

				//定位数据文件中，表头项的位置
				for (int iTemplateCurrentCol = 0; iTemplateCurrentCol < (null == dataRowTemplateTitle ? 0 : dataRowTemplateTitle.size()); iTemplateCurrentCol++) {
					titleNameTemplate = super.formatStringValue(dataRowTemplateTitle.get(iTemplateCurrentCol));
					for (int n = 0; n < (null == dataRowTitle ? 0 : dataRowTitle.size()); n++) {
						if(super.formatStringValue(dataRowTitle.get(n)).equals(titleNameTemplate)){
							iDataCurrentCol = n;
							break;
						}
					}
					
					if(iTemplateCurrentCol == 0){	//第一列为
						try{
							// 卡类型名称
							String name = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (name.length() > 0) {
								String _queryname = StringUtil.replace(getResource("query-cardtypename.sql"), "${field_name}", "name");
								_queryname = StringUtil.replace(_queryname, "${field_value}", name);
								_queryname = getSQL(_queryname, null);
								Recordset _rsname = db.get(_queryname);
								_rsname.first();
								if(_rsname.getString("name").equals("1")){
									validateError.append("卡类型名称已存在；");
								}else{
									rs.setValue("name", name);
								}
							}else{
								validateError.append("卡类型名称不能为空；");
							}
						} catch (Exception e) {
							validateError.append("卡类型名称不能为空；");
						}
					}else if(iTemplateCurrentCol == 1){	//第二列为
						try{
							//卡种分类
							String querycardcategory = getResource("query-cardcategory.sql");
							querycardcategory = getSQL(querycardcategory, null);
							Recordset cardcategoryType = db.get(querycardcategory);
							String cardcategory = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardcategory.length() > 0) {
								int index = super.findRecordNumber(cardcategoryType, "cardcategory", cardcategory);
								if (index < 0) {
									validateError.append("系统中不存在该卡种分类；");
								}else{
									String _querycardcategory = StringUtil.replace(getResource("query-cardcategoryvalue.sql"), "${field_name}", "category_name");
									_querycardcategory = StringUtil.replace(_querycardcategory, "${field_value}", cardcategory);
									_querycardcategory = getSQL(_querycardcategory, null);
									Recordset _rscardcategory = db.get(_querycardcategory);
									_rscardcategory.first();
									rs.setValue("cardcategory", _rscardcategory.getString("cardcategory"));
								}
							}else{
								validateError.append("卡种分类不能为空；");
							}
						} catch (Exception e) {
							validateError.append("卡种分类不能为空；");
						}
					}else if(iTemplateCurrentCol == 2){	//第二列为
						try{
							//卡类别
							String querytype = getResource("query-type.sql");
							querytype = getSQL(querytype, null);
							Recordset typeType = db.get(querytype);
							type = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (type.length() > 0) {
								int index = super.findRecordNumber(typeType, "type", type);
								if (index < 0) {
									validateError.append("系统中不存在该卡类别；");
								}else{
									//卡类别：0时效卡、1记次卡、3体验卡
									if(type.equals("时效卡")){
										rs.setValue("type", 0);
									}else if(type.equals("计次卡")){
										rs.setValue("type", 1);
									}else if(type.equals("体验卡")){
										rs.setValue("type", 3);
									}
								}
							}else{
								validateError.append("卡类别不能为空；");
							}
						} catch (Exception e) {
							validateError.append("卡类别不能为空；");
						}
					}else if(iTemplateCurrentCol == 3){	//第二列为
						try{
							//额外赠送天数
							String giveday = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (giveday.length() > 0) {
								m = intp.matcher(giveday);
								boolean b = m.matches();
								if(b){
									rs.setValue("giveday", giveday);
								}else{
									validateError.append("额外赠送天数格式应为整数；");
								}
							}else{
								rs.setValue("giveday", giveday);
							}
						} catch (Exception e) {
						}
					}else if(iTemplateCurrentCol == 4){	//第二列为
						try{
							//赠送免费课节数
							String ptcount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (ptcount.length() > 0) {
								Matcher mm = intp.matcher(ptcount);
								boolean b = mm.matches();
								if(b){
									rs.setValue("ptcount", ptcount);
								}else{
									validateError.append("赠送免费课节数格式应为整数；");
								}
							}else{
								rs.setValue("ptcount", ptcount);
							}
						} catch (Exception e) {
						}
						
					}else if(iTemplateCurrentCol == 5){	//第二列为
						try{
							//最大使用人数
							String maxusernum = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (maxusernum.length() > 0) {
								Matcher mm = intp.matcher(maxusernum);
								boolean b = mm.matches();
								if(b){
									rs.setValue("maxusernum", maxusernum);
								}else{
									validateError.append("最大使用人数格式应为整数；");
								}
							}else{
								rs.setValue("maxusernum", "1");
							}
						} catch (Exception e) {
						}
						
					}else if(iTemplateCurrentCol == 6){	//第二列为
						try{
							//可用次数（次卡）
							String count = super.formatStringValue(dataRow.get(iDataCurrentCol));
							//判断是否是次卡
							if(type.equals("记次卡")){
								if (count.length() > 0) {
									Matcher mm = intp.matcher(count);
									boolean b = mm.matches();
									if(b){
										rs.setValue("count", count);
									}else{
										validateError.append("可用次数（次卡）格式应为整数；");
									}
								}else{
									validateError.append("可用次数（次卡）不能为空；");
								}
							}else{
								rs.setValue("count", count);
							}
						} catch (Exception e) {
							validateError.append("可用次数（次卡）不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 7){	//第二列为
						try{
							//有效天数
							String daycount = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (daycount.length() > 0) {
								Matcher mm = intp.matcher(daycount);
								boolean b = mm.matches();
								if(b){
									rs.setValue("daycount", daycount);
								}else{
									validateError.append("有效天数格式应为整数；");
								}
							}else{
								validateError.append("有效天数不能为空；");
							}
						} catch (Exception e) {
							validateError.append("有效天数不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 8){	//第二列为
						try{
							//卡价格
							cardfee = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (cardfee.length() > 0) {
								Matcher mm = doublep.matcher(cardfee);
								boolean b = mm.matches();
								if(b){
									rs.setValue("cardfee", cardfee);
								}else{
									validateError.append("卡价格格式应为数字；");
								}
							}else{
								validateError.append("卡价格不能为空；");
							}
						} catch (Exception e) {
							validateError.append("卡价格不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 9){	//第二列为
						try{
							//最低价
							String minfee = super.formatStringValue(dataRow.get(iDataCurrentCol));
							if (minfee.length() > 0) {
								Matcher mm = doublep.matcher(minfee);
								boolean b = mm.matches();
								if(b){
									rs.setValue("minfee", minfee);
								}else{
									validateError.append("最低价格式应为数字；");
								}
							}else{
								rs.setValue("minfee", cardfee);
							}
						} catch (Exception e) {
							validateError.append("最低价格式不能为空；");
						}
						
					}else if(iTemplateCurrentCol == 10){	//第二列为
						try {
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
					//添加卡类型
					String cardType = getResource("insert-cardtype.sql");
					String _cardType = getSQL(cardType, rs);
					db.exec(_cardType);
					//会员卡类型费用
					String cardFee = getResource("insert-cardfee.sql");
					String _cardFee = getSQL(cardFee, rs);
					db.exec(_cardFee);
					
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
		rs.append("name", Types.VARCHAR);	// 卡类型名称
		rs.append("cardcategory", Types.VARCHAR);	// 卡种分类
		rs.append("type", Types.INTEGER);	// 卡类别
		rs.append("giveday", Types.VARCHAR);	// 额外赠送天数
		rs.append("ptcount", Types.VARCHAR);	// 赠送免费课节数
		rs.append("maxusernum", Types.VARCHAR);	// 最大使用人数
		rs.append("count", Types.VARCHAR);	// 可用次数
		rs.append("daycount", Types.VARCHAR);	// 有效天数
		rs.append("cardfee", Types.VARCHAR);	// 卡价格
		rs.append("minfee", Types.VARCHAR);	// 最低价
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
