package com.ccms.quartz.quartz;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.context.InitializerServlet;

import dinamica.Db;
import dinamica.Marker;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;

public class BaseJob implements Job{

	private static Logger logger = Logger.getLogger(BaseJob.class.getName());
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException{
		
	}
	
	/**
	 * 执行之前记录日志
	 * @param arg0
	 * @param remark
	 * @return 返回日志主键
	 */
	protected String beforeExecute(JobExecutionContext arg0, String remark){
		String tuid = "";
		Connection conn = null;
		try{
			conn = JDBC.getConnection();
			Db db = new Db(conn);
			String querySeq = getSQL(getLocalResource("/com/ccms/quartz/quartz/sql/query_quartz_log_seq.sql"), null);
			String insert = getSQL(getLocalResource("/com/ccms/quartz/quartz/sql/insert_quartz_log.sql"), null);
			
			Recordset rs = db.get(querySeq);
			if(rs.next()){
				tuid = rs.getString("tuid");
				insert = StringUtil.replace(insert, "${tuid}", tuid);
				insert = StringUtil.replace(insert, "${job_name}", arg0.getJobDetail().getJobClass().getSimpleName());
				insert = StringUtil.replace(insert, "${job_cron}", arg0.getTrigger().getKey().getName());
				insert = StringUtil.replace(insert, "${fire_time}", StringUtil.formatDate((null != arg0.getNextFireTime() ? arg0.getNextFireTime() : new Date()), "yyyy-MM-dd HH:mm:ss"));
				insert = StringUtil.replace(insert, "${exec_remark}", (remark != null && remark.length()>2000)?remark.substring(0,2000):remark);
				db.exec(insert);
			}
			
		}catch (Throwable e)
		{
			logger.error(e);
			e.printStackTrace();
		}finally{
			if(conn != null){
				// 关闭连接
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return tuid;
	}
	
	/**
	 * 执行之后记录日志
	 * @param arg0
	 * @param tuid
	 * @param remark
	 */
	protected void afterExecute(JobExecutionContext arg0, String tuid, String remark){
		if(tuid == null || tuid.length()==0){
			logger.warn(arg0.getJobDetail().getJobClass().getSimpleName()+"===afterExecute >> tuid is null");
			return;
		}
		Connection conn = null;
		try{
			conn = JDBC.getConnection();
			Db db = new Db(conn);
			String update = getSQL(getLocalResource("/com/ccms/quartz/quartz/sql/update_quartz_log.sql"), null);
			
			update = StringUtil.replace(update, "${tuid}", tuid);
			update = StringUtil.replace(update, "${exec_remark}", StringUtil.replace(remark,"'","''"));
			db.exec(update);
			
		}catch (Throwable e)
		{
			logger.error(e);
			e.printStackTrace();
		}finally{
			if(conn != null){
				// 关闭连接
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
	}
	/**
	 * @param path
	 * @return 获取本地文件
	 * @throws Throwable
	 */
	protected String getLocalResource(String path) 
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
			e.printStackTrace();
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
		return "";
	}
	
	protected String getSQL(String _template, Recordset rs) throws Throwable
	{
		try
		{
			ServletContext _ctx = InitializerServlet.getContext();
			if (_ctx!=null)
			{
				if (_template.indexOf("${schema}") > 0 ) {
					String schema = _ctx.getInitParameter("security-schema");
					if (schema==null)
						schema = "";
					else
						if (!schema.endsWith(".") && !schema.equals(""))
							schema = schema + ".";
					_template = StringUtil.replace(_template, "${schema}", schema);
				}
			}
			
			if (rs!=null)
			{
				/* get recordset metadata */
				HashMap<String, RecordsetField> flds = rs.getFields();
			
				/* for each field try to replace value */
				Iterator<RecordsetField> i = flds.values().iterator();
				while (i.hasNext())
				{
				
					RecordsetField f = i.next();
					String fname = f.getName();
					Object value = rs.getValue(fname);
					String marker = "${fld:" + fname + "}";
				
					if (value==null)
					{
						_template = StringUtil.replace(_template, marker, "NULL");
					}
					else
					{
						switch (f.getType())
						{
							case Types.VARCHAR:
							case Types.CHAR:
							case Types.LONGVARCHAR:
								String v = (String)value;
								v = StringUtil.replace(v,"'","''");
								_template = StringUtil.replace(_template, marker, "'" + v + "'");
								break;
							
							case Types.DATE:
								java.util.Date d = (java.util.Date)value;
								_template = StringUtil.replace(_template, marker, "{d '" + StringUtil.formatDate(d, "yyyy-MM-dd") + "'}");
								break;
							
							case Types.TIMESTAMP:
								java.util.Date d1 = (java.util.Date)value;
								_template = StringUtil.replace(_template, marker, "{ts '" + StringUtil.formatDate(d1, "yyyy-MM-dd HH:mm:ss.SSS") + "'}");
								break;
							
							default:
								String n = "";
								try{
									n = dinamica.StringUtil.formatNumber(value, "#.######");
									n = dinamica.StringUtil.replace(n, ",", ".");				
									_template = StringUtil.replace(_template, marker, n);
								}catch(Throwable e){//如果转成数字失败则转化为字符串
									String vn = (String)value;
									vn = StringUtil.replace(vn,"'","''");
									_template = StringUtil.replace(_template, marker, "'" + vn + "'");
								}
								break;
								
						}
					}
				
				}
			}
			
			_template = replaceDefaultValues(_template);
		
			/* replace SEQUENCE and request/session markers */
			if (_ctx!=null)
			{
				
				ArrayList<Marker> seqs = getMarkers(_template, "seq");
		
				/* for each field marker set value */
				Iterator<Marker> is = seqs.iterator();
				while (is.hasNext())
				{
					/* get next marker */
					Marker m = (Marker)is.next();
					String seqType = m.getName(); //sequence mode (nextval|currval)
					String seqName = m.getExtraInfo(); //sequence object name
					String marker = "${seq:" + seqType + "@" + seqName + "}";
			
					/* get sequence configuration from context */
					String seqConfigParam = "sequence-" + seqType; 
					String seqExpr = _ctx.getInitParameter(seqConfigParam);
				
					/* throw error if config not found */
					if (seqExpr==null || seqExpr.equals(""))
					{
						String args[] = {marker};
						String msg = "SQL Sequences are not properly in WEB.XML as context parameters: {0}.";
						msg = MessageFormat.format(msg, (Object[])args);
						throw new Throwable(msg);
					}
							
					/* replace sequence expression */
					String value = "";
					
					//patch for Resin 3.0.6 - Feb26-2004
					if (seqExpr.indexOf("${seq}")<0)
						value = StringUtil.replace(seqExpr, "$[seq]", seqName);
					else
						value = StringUtil.replace(seqExpr, "${seq}", seqName);
					//end patch
					
					_template = StringUtil.replace(_template, marker, value);
			
				}
			}
				
			return _template;
		}
		catch (Throwable e)
		{
			String msg = "[TemplateEngine].\n Template:" + _template + "\n";
			String data = "";
			if (rs!=null)
			{
				data = rs.toString();
				System.err.println(msg + data);
			}
			throw e;
		}
		 
	}
	
	protected String replaceDefaultValues(String _template) throws Throwable
	{
		
		if (_template.indexOf("${def:")< 0 )
			return _template;
		
		String markers[] = {
							"${def:date}",
							"${def:time}",
							"${def:timestamp}"
							};
							
		String values[] = new String[markers.length];

		java.util.Date d = new java.util.Date();
		values[0] = StringUtil.formatDate(d, "yyyy-MM-dd");
		values[1] = StringUtil.formatDate(d, "HH:mm:ss");
		values[2] = StringUtil.formatDate(d, "yyyy-MM-dd HH:mm:ss.SSS");
		
		
		for (int i=0;i<markers.length;i++)
		{
			_template = StringUtil.replace(_template,markers[i],values[i]);
		}
		return _template;
	}
	
	protected ArrayList<Marker> getMarkers(String _template, String prefix) throws Throwable
	{	
		
		int pos = 0;
		ArrayList<Marker> l = new ArrayList<Marker>();
		
		/* search markers */
		while ( pos >= 0 )
		{
			int pos1 = 0;
			int pos2 = 0;
			int newPos = 0;
			
			/* find start of marker */
			pos1 = _template.indexOf("${" + prefix + ":", pos);
			if (pos1>=0)
			{
				
				/* find end of marker */
				newPos = pos1 + 6;
				pos2 = _template.indexOf("}", newPos);
				
				if (pos2>0)
				{
					
					/* get marker string */
					String fld = _template.substring(newPos, pos2);
					Marker m = new Marker(fld,null,pos1,pos2);
					
					/* search for etra attribute (format or sequence name) */
					int pos3 = fld.indexOf("@");
					if (pos3>0)
					{
						
						String name = fld.substring(0, pos3);
						String extraInfo = fld.substring(pos3 + 1, fld.length());
						
						if ( (name.indexOf(" ")>=0) || (name.indexOf("\r")>=0) || (name.indexOf("\n")>=0) || (name.indexOf('\t')>=0) )
						{
							String args[] = {name};
							String msg = "Invalid Marker ID - must be a contiguous string of letters and numbers, no spaces or special characters: {0}";
							msg = MessageFormat.format(msg, (Object[])args);
							throw new Throwable(msg);
						}


						m.setName(name);
						m.setExtraInfo(extraInfo);
					}
					
					l.add(m);
				}
				else
				{
					throw new Throwable( "Marker is not properly closed with with a brace '}'." );
				}
				pos = pos2 + 1;
			}
			else
			{
				pos = -1;
			}
		}
		
		return l;
		
	}
}
