package com.ccms.dinamica.domain.blob;

import java.io.File;

import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class SaveBlob extends GenericTableManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputParams) throws Throwable {
		// reuse superclass code
		int rc = super.service(inputParams);

		// patch 2007-10-09 - archivo nulo sera considerado un error
		if (inputParams.isNull("file.filename"))
			throw new Throwable("file.filename is null!");

		String fileName = (String) inputParams.getValue("file.filename");
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		fileName = formatRequestEncoding(fileName);
		inputParams.setValue("file.filename", fileName);

		// get temp file
		String path = (String) inputParams.getValue("file");

		File f = new File(path);

		// get file size
		Integer size = new Integer((int) f.length());
		inputParams.setValue("image_size", size);

		if (size.intValue() == 0)
			throw new Throwable("文件大小为0！");

		if (size.intValue() > (1024 * 1024 * 20))
			throw new Throwable("文件大小限制为最大20M,您的文件较大，不能提交。");

		// prepare sql template (replace static values)
		String sql = getResource("query.sql");

		sql = getSQL(sql, inputParams);

		// get db object and save blob using prepared statement
		Db db = getDb();
		db.saveBlob(sql, path);

		// delete temp file
		f.delete();

		return rc;

	}

	protected String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		// load resource with appropiate encoding if defined
		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"),
					_config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}

}
