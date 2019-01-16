package com.ccms.jdbc;

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
	public static Connection getConnection(String jdbcName) throws Throwable {
		DataSource ds = getDataSource(jdbcName);
		Connection conn = ds.getConnection();
		conn.setAutoCommit(true);
		return conn;
	}

	protected static DataSource getDataSource(String jdbcName) throws Throwable {
		// get datasource config from web.xml
		String jndiPrefix = null;
		String name = null;

		if (InitializerServlet.getContext() != null) {
			name = jdbcName;
			jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
		} else
			throw new Throwable("This method can't return a datasource if servlet the context is null.");

		if (jndiPrefix == null)
			jndiPrefix = "java:comp/env/";

		DataSource ds = Jndi.getDataSource(jndiPrefix + name);
		if (ds == null)
			throw new Throwable("Can't get datasource: " + name);

		return ds;
	}
	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws Throwable
	 */
	public static Connection getConnection() throws Throwable {
		DataSource ds = getDataSource();
		Connection conn = ds.getConnection();
		conn.setAutoCommit(true);
		return conn;
	}

	protected static DataSource getDataSource() throws Throwable {
		// get datasource config from web.xml
		String jndiPrefix = null;
		String name = null;

		if (InitializerServlet.getContext() != null) {
			name = InitializerServlet.getContext().getInitParameter("def-datasource");
			jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
		} else
			throw new Throwable("This method can't return a datasource if servlet the context is null.");

		if (jndiPrefix == null)
			jndiPrefix = "java:comp/env/";

		DataSource ds = Jndi.getDataSource(jndiPrefix + name);
		if (ds == null)
			throw new Throwable("Can't get datasource: " + name);

		return ds;
	}
}
