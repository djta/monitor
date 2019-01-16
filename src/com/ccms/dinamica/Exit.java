package com.ccms.dinamica;

import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class Exit extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {

		// reuse superclass code
		super.service(inputParams);
		HttpServletRequest request = getRequest();
		HttpSession s = request.getSession(true);
		
		//记录当前是否微信登录
		String weixin_userid = (String)s.getAttribute("dinamica.security.weixin_userid");
		
		Enumeration em = request.getSession().getAttributeNames();
		while (em.hasMoreElements()) {
			request.getSession().removeAttribute(em.nextElement().toString());
		}
		
		//判断weixin_userid，如果非空，则weixin_type赋值,以便于filter中判断当前是否为微信登录
		if (weixin_userid != null && weixin_userid.length() > 0) {
			s.setAttribute("dinamica.security.weixin_userid", weixin_userid);
			s.setAttribute("dinamica.security.weixin_type", "0");
			String updateWeixinSql = getResource("update-weixin.sql");
			updateWeixinSql = getSQL(updateWeixinSql,inputParams);
			Db db = getDb();
			db.exec(updateWeixinSql);
		}
		
		return 0;
	}

}
