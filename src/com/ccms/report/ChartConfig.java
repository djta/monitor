package com.ccms.report;

import dinamica.*;

/**
 * Modifica la configuracion de un chart para adecuarlo al caso
 * de un recordset crosstab con un conjunto de columnas dinamicas
 * cuyos nombres y valores solo se conocen a traves de otro recordset,
 * en este caso cols.sql. Normalmente un chart basta que se configure
 * en config.xml si uno conoce de antemano los atributos, pero con una
 * consulta crosstab, no se conocen de antemano, por eso es necesario
 * extender a la clase GenericChart para proceder a modificar dos
 * parametros que se usan en graficos multi-series: column-y y title-series.
 * <br><br>
 * Actualizado: 2007-05-31<br>
 * Framework Dinamica - Distribuido bajo licencia LGPL<br>
 * @author mcordova (martin.cordova@gmail.com)
 */

public class ChartConfig extends GenericChart
{

	@Override
	public int service(Recordset inputParams) throws Throwable
	{
		
		//obtener configuracion basica del chart leida de config.xml
		super.service(inputParams);
		Recordset rs = getRecordset("chartinfo");
		
		//obtener lista de columnas dinamicas
		Recordset rsAxisX = getRecordset("query-axis-x.sql");
		Recordset rsAxisY = getRecordset("query-axis-y.sql");
		Recordset rsSeries = getRecordset("query-series.sql");
		Recordset rsChart = getRecordset("query-chart.sql");
		
		String plugin = "";
		String title = "";
		String titlex = "";
		String titley = "";
		Integer width = 500;
		Integer height = 400;
		//String dateFormat = "";
		String fieldx = "";
		String fieldy = "";
		String series = "";
		//String color = "";
		//String labelXFormat = "";
		//String tickUnit = "1";
		
		//ensamblar la lista de campos Y con los nombres de cada serie
		//y tambien las etiquetas de cada serie
		rsAxisX.first();
		fieldx = rsAxisX.getString("axis_x");

		rsAxisY.top();
		while (rsAxisY.next()) {
			fieldy = fieldy + rsAxisY.getString("axis_y") + ";";
		}
		if (fieldy.endsWith(";"))
			fieldy = fieldy.substring(0,fieldy.length()-1);

		rsSeries.top();
		while (rsSeries.next()) {
			series = series + rsSeries.getString("series") + ";";
		}
		if (series.endsWith(";"))
			series = series.substring(0,series.length()-1);
		
		rsChart.first();
		plugin = rsChart.getString("plugin");
		title = rsChart.getString("title");
		titlex = rsChart.getString("title_x");
		titley = rsChart.getString("title_y");
		width = rsChart.getInteger("width");
		height = rsChart.getInteger("height");
		//dateFormat = rsChart.getString("dateFormat");
		//color = rsChart.getString("color");
		//labelXFormat = rsChart.getString("labelXFormat");
		//tickUnit = rsChart.getString("tickUnit");
		
		//reemplazar los valores en la configuracion
		//esta listo para graficar
		//rs.setValue("dateformat", dateFormat); //in pixels

		rs.setValue("chart-plugin", plugin);
		rs.setValue("title", title);
		rs.setValue("title-x", titlex); //irrelevant for pie charts
		rs.setValue("title-y", titley); //irrelevant for pie charts
		rs.setValue("column-x", fieldx); 
		rs.setValue("column-y", fieldy); //if multiseries then type multiple column names separated by ";"
		rs.setValue("title-series", series); //no series - otherwise set label names separated by ";"
		rs.setValue("width", width); //in pixels
		rs.setValue("height", height); //in pixels
		//rs.setValue("color", color);
		//rs.setValue("labelx-format", labelXFormat);

		//if (tickUnit!=null)
		//	rs.setValue("tick-unit", Integer.valueOf(tickUnit));
		
		return 0;

	}
	
}
