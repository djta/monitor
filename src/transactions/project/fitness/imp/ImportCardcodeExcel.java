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

import com.ccms.imp.ExcelImportFor2k3;
import com.ccms.imp.ExcelImportFor2k7;
import com.ccms.imp.ExcelImportUtil;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;

/***
 * 卡内码导入
 * @author C
 * 2016-09-23
 */
public class ImportCardcodeExcel extends ImportUtil {
	
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
			List<List<String>> dataList = excel.getExcelData();
			if (dataList.size() == 0) {
				throw new Throwable(ExcelImportUtil.ERROR_FILE_EMPTY);
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
			String insert = getResource("insert.sql");
			String nodup = getResource("nodup.sql");
			
			int rowsNum = dataList.size(); // 获取行数
			int title_line_num = 0;
			Recordset rs = this.initRecordset();
			// 以下均按照单元格位置固定处理
			for (int j = (title_line_num + 1); j < rowsNum; j++) {
				StringBuffer validateError = new StringBuffer();
				rs.addNew();
				List<String> dataRow = dataList.get(j);
				int colsNum = (null == dataRow ? 0 : dataRow.size());
				if (colsNum < 2) {
					throw new Throwable("导入的Excel列数不足，请参考模板确认后再导入");
				}
				// 序号
				rs.setValue("row_number", j);
				
				// 卡内码
				String vc_guid = super.formatStringValue(dataRow.get(0));
				if (vc_guid.length() <= 0) {
					validateError.append("卡内码不能为空;");
				}else{
					// 判断卡内码是否重复
					String _query = StringUtil.replace(nodup, "${field_name}", "vc_guid");
					_query = StringUtil.replace(_query, "${field_value}", vc_guid);
					Recordset _rs = db.get(_query);
					if( null != _rs && _rs.getRecordCount() > 0 ){
						validateError.append("该卡内码已使用;");
					}
				}
				rs.setValue("vc_guid", vc_guid);
				
				// 会员卡号
				String vc_cardcode = super.formatStringValue(dataRow.get(1));
				if (vc_cardcode.length() <= 0) {
					validateError.append("会员卡号不能为空;");
				}else{
					// 判断会员卡号是否重复
					String _query = StringUtil.replace(nodup, "${field_name}", "vc_cardcode");
					_query = StringUtil.replace(_query, "${field_value}", vc_cardcode);
					Recordset _rs = db.get(_query);
					if( null != _rs && _rs.getRecordCount() > 0 ){
						validateError.append("该会员卡号已使用;");
					}
				}
				rs.setValue("vc_cardcode", vc_cardcode);
				
				// 备注
				String vc_remark = "";
				if (colsNum >= 3 ) {
					vc_remark = super.formatStringValue(dataRow.get(2));
				}
				rs.setValue("vc_remark", vc_remark);
				
				// 数据保存
				if( null != validateError && validateError.length() > 0 ){
					rs.setValue("validate_error", validateError.toString());
					rsFailed.addNew();
					rs.copyValues(rsFailed);
				}else{
					db.beginTrans();
					String _insertGoods = getSQL(insert, rs);
					db.exec(_insertGoods);
					
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
		rs.append("vc_guid", Types.VARCHAR);	// 卡内码
		rs.append("vc_cardcode", Types.VARCHAR);	// 会员卡号
		rs.append("vc_remark", Types.VARCHAR);	// 备注
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
	
}
