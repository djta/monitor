package transactions.project.fitness.imp;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.tools.ant.util.StringUtils;

import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class ImportUtil extends GenericTableManager {
	protected String formatRequestEncoding(String str) throws Throwable {
		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equalsIgnoreCase(""))
			encoding = null;

		// load resource with appropiate encoding if defined
		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"), _config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}
	
	protected String getRealSavePath(String parentPath, String subPath, boolean bPersist) {
		// 获得物理路径
		String strCurPath = getRequest().getSession().getServletContext().getRealPath("/");
		String fileSeperator = System.getProperty("file.separator");

		int nPosSeperator = strCurPath.lastIndexOf(fileSeperator);
		String rootPath = strCurPath.substring(0, nPosSeperator);

		if (bPersist) {
			nPosSeperator = rootPath.lastIndexOf(fileSeperator);
			rootPath = strCurPath.substring(0, nPosSeperator);
		}

		// 组织路径
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
		// 创建路径
		String savePath = totalSubPath + fileSeperator;
		return savePath;
	}
	
	public String getCellValue(Cell colcell, SimpleDateFormat sdf, DecimalFormat df) {
		String content = null;
		if (colcell != null) {
			switch (colcell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				content = colcell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(colcell)) {
					Date dateCell = colcell.getDateCellValue();
					content = sdf.format(dateCell);
				} else {
					Double value = colcell.getNumericCellValue();
					if (value == value.longValue()) {
						content = "" + value.longValue();
					} else {
						content = df.format(value);
					}
				}
				break;
			case Cell.CELL_TYPE_FORMULA:
				content = colcell.getCellFormula();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				content = String.valueOf(colcell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				content = colcell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:
				content = String.valueOf(colcell.getErrorCellValue());
				break;
			default:
				content = colcell.toString();
			}
		}
		return formatStringValue(content);
	}

	/**
	 * 将String值NULL，空格串转化为空串
	 * @param input
	 * @return
	 */
	protected String formatStringValue(String input){
		if(null == input || input.length()<=0) {
			return "";
		} else {
			input = input.trim();
			if(input.length()>=0) {
				return input;
			}
			return "";
		}
	}
	
	protected Recordset getRecordset(Db db, String query, String field, String fieldvalue) throws Throwable {
		String _query = query;
		_query = StringUtil.replace(_query, field, fieldvalue);
		Recordset rs = db.get(getSQL(_query, null));
		return rs;
	}
	
	protected int findRecordNumber(Recordset rs, String name, String value) throws Throwable{
		int index = -1;
		if(null != rs && rs.getRecordCount()>0){
			index = rs.findRecord(name, value);
		}
		return index;
	}
	
	protected Integer querySeq(Db db, String sql, String param) throws Throwable{
		Recordset rs = this.getRecordset(db, sql, "${seq}", param);
		rs.first();
		return rs.getInteger("seq");
	}
	
	protected String getExcelCellValue(List<String> dataRow, int index, int len){
		String value = null;
		if(len >= index+1){
			value = dataRow.get(index);
		}
		return value;
	}

	/**
	 * 记录日志并返回日志主键
	 * @param db
	 * @param importBatch
	 * @param importType
	 * @param fileName
	 * @param filePath
	 * @return
	 * @throws Throwable
	 */
	protected String insertImportLog(Db db, String importBatch, String fileName, String filePath) throws Throwable {
		String queryLogSeq = getLocalResource("/transactions/project/fitness/imp/log/query-import-log-seq.sql");
		Recordset rsSeq = db.get(queryLogSeq);
		rsSeq.first();
		String logTuid = rsSeq.getString("seq");
		String insertLog = getLocalResource("/transactions/project/fitness/imp/log/insert-import-log.sql");
		insertLog = StringUtil.replace(insertLog, "${import_log_tuid}", logTuid);
		insertLog = StringUtil.replace(insertLog, "${import_batch}", importBatch);
		insertLog = StringUtil.replace(insertLog, "${file_name}", fileName);
		insertLog = StringUtil.replace(insertLog, "${file_path}", filePath);
		insertLog = getSQL(insertLog,null);
		db.exec(insertLog);
		return logTuid;
	}

	/**
	 * 更新日志结果
	 * @param db
	 * @param importLogTuid
	 * @param result
	 * @param desc
	 * @throws Throwable
	 */
	protected void updateImportLog(Db db, String importLogTuid, int result, String desc) throws Throwable {
		if( importLogTuid!=null && !"".equals(importLogTuid)){
			String updateLog = getLocalResource("/transactions/project/fitness/imp/log/update-import-log.sql");
			updateLog = StringUtil.replace(updateLog, "${import_log_tuid}", importLogTuid);
			updateLog = StringUtil.replace(updateLog, "${import_result}", String.valueOf(result));
			updateLog = StringUtil.replace(updateLog, "${result_desc}", desc);
			updateLog = getSQL(updateLog, null);
			db.exec(updateLog);
		}
	}
	
	/**
	 * 保存的文件重命名
	 * @param filename
	 * @param importBatch
	 * @param ext
	 * @return
	 */
	protected String renameSaveFile(String filename, String importBatch, String ext){
		String _filename = filename.substring(0, filename.lastIndexOf("."));
		_filename += "_" + importBatch + "." + ext;
		return _filename;
	}
}
