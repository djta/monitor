package com.ccms.dinamica.domain.blob;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import dinamica.Config;
import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Recordset;

public class BlobOutput extends GenericOutput
{

	/* (non-Javadoc)
	 * @see dinamica.GenericOutput#print(dinamica.GenericTransaction)
	 */
	public void print(GenericTransaction data) throws Throwable
	{
		
		//get datasource object
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");
				
		/* PATCH 2005-03-10 read datasource name from config.xml if available */
		if (getConfig().transDataSource!=null)
			dataSourceName = getConfig().transDataSource;
		
		if (jndiPrefix==null)
			jndiPrefix="";
		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);
		
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		
		BufferedInputStream buf = null;
		ServletOutputStream out = null;
		
		try
		{
			//connect to database
			conn = ds.getConnection();
			s = conn.createStatement();

	  		//get recordset with blob metadata
	  		Recordset info = data.getRecordset("blobinfo");
			
			//get sql to retrieve blob
			String sql = info.getString("sql");

			//set BLOB content-type
			getResponse().setContentType(info.getString("format"));

			//attach?
			String fileName = info.getString("filename");
			if (fileName!=null)
			{
				getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + formatRequestEncoding(fileName) + "\";"); 
			}

			//get servlet output stream
			out = getResponse().getOutputStream();

			//execute query and retrieve blob
			rs = s.executeQuery(sql);
			if (rs.next())
			{
				//get reference to BLOB column - must be the only one retrieved!
				String DatabaseType = getContext().getInitParameter("db");
				if("postgresql".equalsIgnoreCase(DatabaseType)){
					//read blob
					byte[] blob = rs.getBytes(1);
					
					double scaleFactor = 0;
					String scale = getRequest().getParameter("scale");
					if (scale!=null && !scale.equals("")) {
						scaleFactor = Double.parseDouble(scale);
					}
					
					//scale image if requested
					if (blob.length > 20000 && scaleFactor > 0 && info.getString("format").startsWith("image/")) {
						BufferedImage bufimg = ImageIO.read(new ByteArrayInputStream(blob));
						AffineTransform tx = new AffineTransform();
					    tx.scale(scaleFactor, scaleFactor);
					    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					    BufferedImage newImg =  op.filter(bufimg, null);
					    ByteArrayOutputStream bout = new ByteArrayOutputStream();
					    if (info.getString("format").endsWith("png"))
					    	ImageIO.write(newImg, "png", bout);
					    else
					    	ImageIO.write(newImg, "jpg", bout);
					    blob = bout.toByteArray();
					}
					
					//set content length
					int size = (int)blob.length;
					getResponse().setContentLength(size);
					out.write(blob);
				}else{
					Blob blob = rs.getBlob(1);
					//set content length
					int size = (int)blob.length();
					getResponse().setContentLength(size);

					int bytes = 0;
					byte buffer[] = new byte[size];
					buf = new BufferedInputStream( blob.getBinaryStream() );
					
					while( bytes != -1 )
					{
						bytes = buf.read(buffer);
						if (bytes>0)
							out.write(buffer,0,bytes);
					} 
				}
			}
		}
		catch (Throwable e)
		{
			throw e;
		}
		finally
		{

			try
			{
				if (buf!=null) buf.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}

			try
			{
				if (rs!=null) rs.close();
			}
			catch (SQLException e2)
			{
				e2.printStackTrace();
			}

			try
			{
				if (s!=null) s.close();
			}
			catch (SQLException e3)
			{
				e3.printStackTrace();
			}

			try
			{
				if (conn!=null) conn.close();
			}
			catch (SQLException e4)
			{
				e4.printStackTrace();
			}

			try
			{
				if (out!=null)
				{
					out.close();
				}
			}
			catch (IOException e5)
			{
				e5.printStackTrace();
			}

		}
		
	}

	protected String formatRequestEncoding(String filename) {
		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		try {
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				filename = java.net.URLEncoder.encode(filename,_config.requestEncoding != null?_config.requestEncoding:(encoding != null && encoding.trim().equals("")?encoding:"utf-8"));
				filename = filename.replaceAll("\\+", "%20"); //处理空格
			} else {   
				filename = new String(filename.getBytes(_config.requestEncoding != null?_config.requestEncoding:(encoding != null && encoding.trim().equals("")?encoding:"utf-8")),"iso-8859-1");   
			}
		} catch (UnsupportedEncodingException e) { }
		return filename;
	}
}
