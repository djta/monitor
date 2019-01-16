package transactions.project.weixin.service.unbind;


import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ccms.context.InitializerServlet;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class WeixinUnbind extends GenericTableManager{


	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		//ServletContext _ctx = InitializerServlet.getContext();
		
		//记录当前是否微信登录
		//String weixin_userid = (String)_ctx.getAttribute("dinamica.security.weixin_userid");

		/*Enumeration em = _ctx.getAttributeNames();
		while (em.hasMoreElements()) {
			System.out.println(em.nextElement().toString());
			_ctx.removeAttribute(em.nextElement().toString());
		}*/
		
		//_ctx.removeAttribute("dinamica.security.weixin_userid");		
		
		//判断weixin_userid，如果非空，则weixin_type赋值,以便于filter中判断当前是否为微信登录
		/*if (weixin_userid != null && weixin_userid.length() > 0) {
			_ctx.setAttribute("dinamica.security.weixin_userid", weixin_userid);
			_ctx.setAttribute("dinamica.security.weixin_type", "0");
		}*/
		
		Db db=getDb();
		//清除当前绑定的微信帐号
		String delete_weixin = getSQL(getResource("delete_weixin.sql"), inputParams);
		db.exec(delete_weixin);
		rc=5;

		return rc;
	}

}
