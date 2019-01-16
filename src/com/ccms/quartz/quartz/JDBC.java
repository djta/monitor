package com.ccms.quartz.quartz;

import java.sql.Connection;

import javax.sql.DataSource;

import com.ccms.context.InitializerServlet;

import dinamica.Jndi;

public class JDBC {

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws Throwable
	 */
	public static Connection getConnection() throws Throwable {
		return getConnection(null, null);
	}

	/**
	 * 获取数据库连接
	 * @param jndiPrefix
	 * @param dataSource
	 * @return
	 * @throws Throwable
	 */
	public static Connection getConnection(String jndiPrefix, String dataSource) throws Throwable {
		DataSource ds = getDataSource(jndiPrefix, dataSource);
		Connection conn = ds.getConnection();
		conn.setAutoCommit(true);
		return conn;
	}
	
	/**
	 * 获取数据源
	 * @param jndiPrefix
	 * @param dataSource
	 * @return
	 * @throws Throwable
	 */
	protected static DataSource getDataSource(String jndiPrefix, String dataSource) throws Throwable {
		// get datasource config from web.xml
		if (InitializerServlet.getContext() != null) {
			if(dataSource == null){
				dataSource = InitializerServlet.getContext().getInitParameter("def-datasource");
			}
			if(jndiPrefix == null){
				jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
			}
		} 
		if(dataSource == null){
			throw new Throwable("This method can't return a datasource if servlet the context is null.");
		}
		if (jndiPrefix == null)
			jndiPrefix = "java:comp/env/";

		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSource);
		if (ds == null)
			throw new Throwable("Can't get datasource: " + dataSource);

		return ds;
	}
}
