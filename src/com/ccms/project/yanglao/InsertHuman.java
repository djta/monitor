package com.ccms.project.yanglao;

import dinamica.Base64;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class InsertHuman extends GenericTableManager
{

	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		super.service(inputParams);
		Db db=getDb();
		String AlldayTel=inputParams.getString("AlldayTel");
		String userlogin=AlldayTel;
		String passwd=userlogin;
		
		java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		byte[] b = (userlogin + ":" + passwd).getBytes();
		byte[] hash = md.digest(b);
		String pwd = Base64.encodeToString( hash, true );
		
		String querySqlString=StringUtil.replace(getResource("nodup.sql"), "${userlogin}", userlogin);
		Recordset queryRecordset=db.get(querySqlString);
		int count=queryRecordset.getRecordCount();
		if(count>0){
			throw new Throwable("用户已存在");
		}else{
			String insertHumanString=StringUtil.replace(getSQL(getResource("insert-human.sql"), inputParams), "${userlogin}", userlogin);
			String insertHumanApend=StringUtil.replace(getSQL(getResource("insert-human_apend.sql"), inputParams), "${userlogin}", userlogin);

			String insertUserString=StringUtil.replace(StringUtil.replace(getSQL(getResource("insert-user.sql"), inputParams), "${userlogin}", userlogin), "${passwd}", pwd);
			String insertStaffString=StringUtil.replace(getSQL(getResource("insert-staff.sql"), inputParams), "${userlogin}", userlogin);
			String insertRoleString=StringUtil.replace(getSQL(getResource("insert-roles.sql"), inputParams), "${userlogin}", userlogin);
			String insertSkillString=StringUtil.replace(getSQL(getResource("insert-skills.sql"), inputParams), "${userlogin}", userlogin);
			String insertLogString=StringUtil.replace(StringUtil.replace(getSQL(getResource("insert-passlog.sql"), inputParams), "${userlogin}", userlogin), "${passwd}", pwd);
			String insertAccountString=getSQL(getResource("insert-account.sql"), inputParams);
			db.exec(insertUserString);
			db.exec(insertRoleString);
			db.exec(insertSkillString);
			db.exec(insertHumanString);
			db.exec(insertHumanApend);
			db.exec(insertStaffString);
			db.exec(insertLogString);
			db.exec(insertAccountString);
			
		}
		return rc;
	} 
}
