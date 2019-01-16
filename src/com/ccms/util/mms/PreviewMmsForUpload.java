package com.ccms.util.mms;

import java.sql.Types;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class PreviewMmsForUpload extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		try {
			Db db = getDb();
			String queryTotalSql = getSQL(getResource("query-total.sql"), inputParams);
			String queryCurrentSql = getSQL(getResource("query-current.sql"), inputParams);
			Recordset totalRecordset = db.get(queryTotalSql);
			Recordset queryCurrentRecordset = db.get(queryCurrentSql);
			
			String zip_path = MmsUtil.getZipPath(totalRecordset);
			Long total = MmsUtil.getZipSize(zip_path);
			if (total > 1024*50) {
				throw new Throwable("彩信字节大小不得大于50K ");
			}

			Recordset _totalRecordset = new Recordset();
			_totalRecordset.append("zip_size", Types.VARCHAR);
			_totalRecordset.addNew();
			_totalRecordset.setValue("zip_size", total.toString());
			publish("_total", _totalRecordset);
			publish("_preview", queryCurrentRecordset);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return rc;
	}
}
