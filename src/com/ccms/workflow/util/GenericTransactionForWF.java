package com.ccms.workflow.util;

import java.io.InputStream;
import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.ccms.context.InitializerServlet;

import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.ValidatorUtil;

/**
 * @author zc (Mail:zhangchuanhz@gmail.com)
 * @version 2012-1-6
 * 改造自 GenericTransaction 方便获取Db来操作数据库
 * 
 */
public class GenericTransactionForWF
{
	final static String SEQUENCE_BAD_CONFIGURATION = "SQL Sequences are not properly in WEB.XML as context parameters: {0}.";
	Connection 	_conn = null;
	
	protected DataSource getDataSource() throws Throwable
	{

	  //get datasource config from web.xml
	  String jndiPrefix = null;
	  String name = null;
	  
	  if (InitializerServlet.getContext()!=null)
	  {
		name = InitializerServlet.getContext().getInitParameter("def-datasource");
		jndiPrefix = InitializerServlet.getContext().getInitParameter("jndi-prefix");
	  }
	  else
		throw new Throwable("This method can't return a datasource if servlet the context is null.");
	  
	  if (jndiPrefix==null)
		jndiPrefix="java:comp/env/";
	  
	  DataSource ds = Jndi.getDataSource(jndiPrefix + name);
	  if (ds==null)
		throw new Throwable("Can't get datasource: " + name);
	  	
	  return ds;
	  		
	}
	
	protected Db getDb() throws Throwable
	{
		
		if (_conn==null || _conn.isClosed()){
			_conn = getDataSource().getConnection();
			setConnection(_conn);
		}
		
		Db db = new Db(_conn);
		return db;
		
	}
	
	protected void close() throws Throwable
	{
		if (_conn!=null && !_conn.isClosed())
			_conn.close();
	}
	
	protected void rollback() throws Throwable
	{
		if (_conn!=null && !_conn.getAutoCommit())
			_conn.rollback();
	}
	
	/**
	 * The application uses a default DataSource.
	 * A connection from this pool is made available 
	 * for Transaction modules
	 * @return Default Database Connection
	 */
	protected Connection getConnection()
	{
		return _conn;
	}
	
	/**
	 * Set database connection
	 * @param connection
	 */
	public void setConnection(Connection connection)
	{
		_conn = connection;
	}
	
	/**
	 * Realiza la operacion de sumatoria de los valores de todos los registros de un
	 * recordset dado los campos.
	 * @param rsTotal Recordset que contendra los valores a sumar
	 * @param rsMaster Recordset que contiene los campos a sumar
	 * @param cols Array con los campos a sumar
	 * @throws Throwable
	 */
	protected void computeTotal(Recordset rsTotal, Recordset rsMaster, String cols[]) throws Throwable {
		
		rsTotal.first();
		for (int i = 0; i < cols.length; i++) {
			double total = rsMaster.getSUM(cols[i]);
			rsTotal.setValue(cols[i], total);	
		}
		
	}
	
	/**
	 * Create a recordset with all the fields
	 * required to produce a chart with ChartOutput. This recordset
	 * will contain no records.
	 * @return Recordset with the column structure required by 
	 * the class ChartOutput
	 * @throws Throwable
	 */
	public Recordset getChartInfoRecordset() throws Throwable
	{
		/* define chart params recordset */
		Recordset rs = new Recordset();
		rs.append("chart-plugin", java.sql.Types.VARCHAR);
		rs.append("title", java.sql.Types.VARCHAR);
		rs.append("title-x", java.sql.Types.VARCHAR);
		rs.append("title-y", java.sql.Types.VARCHAR);
		rs.append("column-x", java.sql.Types.VARCHAR);
		rs.append("column-y", java.sql.Types.VARCHAR);
		rs.append("title-series", java.sql.Types.VARCHAR);
		rs.append("width", java.sql.Types.INTEGER);
		rs.append("height", java.sql.Types.INTEGER);
		rs.append("data", java.sql.Types.VARCHAR);
		rs.append("dateformat", java.sql.Types.VARCHAR);
		
		//added on april-06-2004
		rs.append("session", java.sql.Types.VARCHAR); //true|false: save in session?
		rs.append("image-id", java.sql.Types.VARCHAR);//session attribute id

		//added on july-19-2005
		rs.append("color", java.sql.Types.VARCHAR); //true|false: save in session?

		//added on march-17-2010
		rs.append("labelx-format", java.sql.Types.VARCHAR); //formato para etiquetas en el eje X
		rs.append("tick-unit", java.sql.Types.INTEGER); //escala para eje X
		
		
		return rs;		
	}

	/**
	* Returns an "env-entry" value stored in web.xml.
	* @param name env-entry-name element
	**/
	protected String getEnvEntry(String name) throws Throwable
	{
     	
		Context env = (Context) new InitialContext().lookup("java:comp/env");
		String v = (String) env.lookup(name);
		return v;

	}
	
	/**
	 * Retorna un recordset dado un array unidimensional, tendra un solo campo y un registro por cada elemento del array.
	 * Es un metodo utilitario que sirve para recibir datos concatenados en un solo parametro, separados por ";" u otro caracter,
	 * que son convertidos en un array -con StringUtil.split()- y luego en registros individuales para un insert en batch, por ejemplo.<br>
	 * Si la data representa fechas, las mismas deben venir en el formato especificado por def-input-date en web.xml.
	 * @param data Array conteniendo la data
	 * @param colName Nombre que tendra el campo en el recordset
	 * @param dataType Tipo de dato: java.sql.Types.VARCHAR, DOUBLE, DATE, INTEGER.
	 * @return Recordset conteniendo un registro por cada elemento del array
	 * @throws Throwable
	 */
	protected Recordset getRsFromArray(String data[], String colName, int dataType) throws Throwable
	{
		
		/* load default date format used to convert date strings to Date objects */
		//String dateFormat = _ctx.getInitParameter("def-input-date");
		String dateFormat = "yyyy-MM-dd";
		
		Recordset rs = new Recordset();
		rs.append(colName, dataType);
		
		for (int i = 0; i < data.length; i++) {
			rs.addNew();
			switch (dataType) {
				case java.sql.Types.INTEGER:
					rs.setValue(colName, new Integer(data[i]));
					break;
				case java.sql.Types.DATE:
					java.util.Date d = ValidatorUtil.testDate(data[i], dateFormat);
					if (d==null)
						throw new Throwable("El valor no representa una fecha: " + data[i] + " en el formato: " + dateFormat);
					rs.setValue(colName, d);
					break;
				case java.sql.Types.DOUBLE:
					rs.setValue(colName, new Double(data[i]));
					break;
				default:
					rs.setValue(colName, data[i]);
					break;
			}
		}
		
		return rs;

	}

	/**
	 * Retorna la lista de valores que se utiliza con el filtro "IN (....)"
	 * de una clausula WHERE de una consulta SQL.<br>
	 * Dado un recordset, retorna un String que contiene una lista
	 * de valores tomados de una columna del recordset, separados por coma
	 * y encerrados entre parentesis. Dependiendo del tipo de dato de la columna,
	 * los valores estaran representados como literales VARCHAR o como numeros enteros.
	 * Solo soporta campos de tipo VARCHAR o INTEGER.<br><br>
	 * @param rs Recordset
	 * @param colName Nombre de la columna a usar
	 * @return Clausula IN o un string vacio si el recordset no tiene registros
	 * @throws Throwable
	 */
	public String getSqlIN(Recordset rs, String colName) throws Throwable 
	{
		String sql = "";
		
		int type = rs.getField(colName).getType();
		StringBuilder b = new StringBuilder();
		b.append( "(" );
		rs.top();
		while (rs.next()) {
			switch (type) {
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:				
				String v = StringUtil.replace(rs.getString(colName),"'","''");
				b.append("'" + v + "'" + ",");
				break;
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
				b.append(rs.getString(colName) + ",");
				break;
			}
		}
		b.deleteCharAt(b.length()-1);
		b.append( ")" );
		
		if (rs.getRecordCount()>0)
			sql = b.toString();
		return sql;
	}

	/**
	 * @param path
	 * @return 获取本地文件
	 * @throws Throwable
	 */
	protected String getLocalResource(String path) throws Throwable
	{

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;
		
		in = this.getClass().getResourceAsStream(path);
        
		try
		{
			if (in!=null)
			{
				while (true)
				{
					int len = in.read(data);
					if (len!=-1)
					{
						buf.append(new String(data,0,len));
					}
					else
					{
						break;
					}
				}
				return buf.toString();
			}
			else
			{
				throw new Throwable("Invalid path to resource: " + path);
			}
            
		}
		catch (Throwable e)
		{
			throw e;
		}
		finally
		{
			if (in!=null)
			{
				try{
					in.close();
				} catch (Exception e){}
			}
		}
	}
	
	/**
	 * Generate SQL command. Encapsulates the use of the TemplateEngine
	 * class, to make it easier for developers writing Transaction Modules
	 * @param sql SQL Template
	 * @param rs Recordset with at least one record - there must be
	 * a current record
	 * @return SQL command with replaced values
	 * @throws Throwable
	 */
	protected String getSQL(String sql, Recordset rs) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(InitializerServlet.getContext(),null, sql);
		return t.getSql(rs);
		
	}
}
