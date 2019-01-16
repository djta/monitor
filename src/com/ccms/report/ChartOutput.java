package com.ccms.report;

import dinamica.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.*;

/**
 * Specific Output Plugin for server-side charting using the JFreeChart
 * LGPL component - every available chart type is also base on a plugin
 * architecture (AbstracChartPlugin).
 * <br>
 * <br>
 * Creation date: 17/11/2003<br>
 * Last Update: 17/11/2003<br>
 * (c) 2003 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * @author Martin Cordova
 * */
public class ChartOutput extends GenericOutput
{

	/* (non-Javadoc)
	 * @see dinamica.GenericOutput#print(dinamica.GenericTransaction)
	 */
	public void print(GenericTransaction t) throws Throwable
	{

		//get chart parameters
		Recordset chartinfo = t.getRecordset("chartinfo");

		//get chart data
		String id = chartinfo.getString("data");
		Recordset data = (Recordset)getSession().getAttribute(id);
		if (data==null)
			throw new Throwable("Invalid Recordset ID:" + id + " - The session does not contain an attribute with this ID.");

		//general chart params
		int width = chartinfo.getInt("width");
		int height = chartinfo.getInt("height");
		
		//load chart plugin
		String plugin = (String)chartinfo.getValue("chart-plugin");
		AbstractChartPlugin obj = (AbstractChartPlugin) Thread.currentThread().getContextClassLoader().loadClass(plugin).newInstance();
		
		//遍历Y轴数据,将百分号替换掉
		String columnYs = chartinfo.getString("column-y");
		String[] colNameY = columnYs.split(";");
		for (int i=0;i<colNameY.length;i++)
		{
			data.top();
			while(data.next()){
				if(data.getString(colNameY[i])!=null)
					data.setValue(colNameY[i], StringUtil.replace(data.getString(colNameY[i]), "%", ""));
				else
					data.setValue(colNameY[i], "0");
					
			}
		}

		//遍历X轴数据,将空的X替换为"空"
		String columnX = chartinfo.getString("column-x");
		data.top();
		while(data.next()){
			if(data.getString(columnX)==null)
				data.setValue(columnX, "空");
		}

		
		JFreeChart chart = obj.getChart(chartinfo, data);
			 		
		//set gradient
		chart.setBackgroundPaint(getGradient());
		
		//set border and legend params
		chart.setBorderPaint(Color.LIGHT_GRAY);
		chart.setBorderVisible(true);
		if (chart.getLegend()!=null) {
			chart.getLegend().setBorder(0.2, 0.2, 0.2, 0.2);
			chart.getLegend().setPadding(5, 5, 5, 5);
			chart.getLegend().setMargin(4, 5, 4, 4);
		}

		//render chart in memory
		BufferedImage img =  chart.createBufferedImage(width, height);
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		
		//encode as PNG
		ImageIO.write(img, "png", b);

		//send bitmap via servlet output
		HttpServletResponse res = getResponse();
		byte image[] = b.toByteArray();
		res.setBufferSize(image.length);
		res.setContentType("image/png");
		res.setContentLength(image.length);
		OutputStream out = res.getOutputStream();
		out.write(image);
			
		//save image bytes in session attribute if requested
		if (chartinfo.containsField("session"))
		{
			String session = chartinfo.getString("session");
			if (session!=null && session.equals("true"))
				getSession().setAttribute(chartinfo.getString("image-id"), image);
		}
				
	}
	
	/**
	 * Creates a gradient (white->gray) for charts, may
	 * be overrided by subclasses to provide a custom gradient
	 * @return Default gradient for charts
	 */
	protected GradientPaint getGradient()
	{
		return new GradientPaint(0, 0, Color.WHITE, 1000, 0, Color.LIGHT_GRAY);		
	}
}
