package com.ccms.workflow.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.config.DefaultConfiguration;

/**
 * @author zc (Mail:zhangchuanhz@gmail.com)
 * @version 2014-10-18
 * 应用初始化时初始化流程加载
 * 
 */
public class WorkflowInitServlet extends HttpServlet {

	private static final long serialVersionUID = 422805878988074048L;

	public WorkflowInitServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		//初始化加载工作流的配置文件(通过URL方式访问)
		try {
			DefaultConfiguration.INSTANCE.load(null);
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
	}
	
}
