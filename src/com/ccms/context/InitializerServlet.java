package com.ccms.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author zc (Mail:zhangchuanhz@gmail.com)
 * @version 2011-3-3
 * 应用初始化时获取 ServletContext 并储存
 * 
 */
public class InitializerServlet extends HttpServlet {

	private static final long serialVersionUID = -8155814013775084511L;
	private static ServletContext context;

	public InitializerServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		context = config.getServletContext();
	}
	
	public void destroy() {
		super.destroy();
	}
	
	public static ServletContext getContext (){
		return context;
	}

}
