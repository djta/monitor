package com.ccms.workflow;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class UpdateWebFlow extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		String query = getSQL(getResource("query.sql"),inputParams);
		String execSql = null;
		
		Db db = getDb();
		Recordset rs = db.get(query);
		rs.next();
		int flag = rs.getInt("flag");
		if(flag == 0){//add
			execSql = getSQL(getResource("insert.sql"),inputParams);
		}else{//update
			execSql = getSQL(getResource("update.sql"),inputParams);
		}
		
		String xmlStr = inputParams.getString("xml_value");
		String dbType = getContext().getInitParameter("db");
		PreparedStatement p = null;
		try
		{
			p = getConnection().prepareStatement(execSql);
			if("postgresql".equalsIgnoreCase(dbType)){//postgresql 特殊处理
				p.setString(1, xmlStr);
			}else{
				StringReader sr = new StringReader(xmlStr);
				p.setClob(1, sr, xmlStr.length());
			}
			p.execute();
		}
		catch (SQLException sqe)
		{
			Throwable t = null;
			String msg = null;
			String date = StringUtil.formatDate(new java.util.Date(), "dd-MM-yyyy HH:mm:ss");
			
			if (sqe.getNextException()!=null) {
				msg = sqe.getNextException().getMessage();
				t = sqe.getCause();
			}
			else {
				msg = sqe.getMessage();
				t = sqe;
			}
			System.err.println("[WARNING@" + date + "] Db.saveBlob failed: " + msg + " SQL: [" + execSql + "]");
			throw new Throwable("Error cargando el archivo en base de datos", t);
				
		}		
		catch (Throwable e)
		{
			String date = StringUtil.formatDate(new java.util.Date(), "dd-MM-yyyy HH:mm:ss");
			System.err.println("[WARNING@" + date + "] Db.saveBlob failed: " + e.getMessage() + " SQL: [" + execSql + "]");
			throw new Throwable("Error cargando el archivo en base de datos", e);
		}
		finally
		{
			if (p!=null) p.close();
		}

		return rc;

	}
}
