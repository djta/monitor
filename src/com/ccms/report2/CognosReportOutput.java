package com.ccms.report2;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class CognosReportOutput extends GenericOutput{

	public void print(GenericTransaction data) throws Throwable
	{
		try{
			Recordset rsReport = data.getRecordset("query_report.sql");
			Recordset rsField = data.getRecordset("query_filter_field.sql");
	
			String oldDateFormat = "yyyy-MM-dd";
			String dateFormat = "yyyy/MM/dd";
			
			StringBuffer filter = new StringBuffer(256);
			String field_code = null,field_alias = null,type = null,showType = null;
			String []tempArray = new String[0];
			// 取参数变量值
			rsField.top();
			while (rsField.next()) {
				field_code = rsField.getString("field_code");
				field_alias = rsField.getString("field_code_alias");
				type = rsField.getString("field_type");
				showType = rsField.getString("show_type");
				
				//filter.append("&").append(field_code).append("=");
				String values[] = getRequest().getParameterValues(field_alias + "_filter");
	
				if (values != null && values.length > 0 && values[0] != null) {
					String value = values[0].trim();
					if (type.equalsIgnoreCase("date")){
						SimpleDateFormat sd = new SimpleDateFormat(oldDateFormat);
						Date d = sd.parse(value);
						SimpleDateFormat nsd = new SimpleDateFormat(dateFormat);
						value = nsd.format(d);
					}
					//如果显示的是checkbox，则需要替换里面的单引号，然后外面套一个单引号
					if("2".equals(showType) || ("8".equals(showType))){
						value = StringUtil.replace(value, "'", "");
						tempArray = StringUtil.split(value,",");
						for (String val : tempArray) {
							filter.append(String.format("<input type='hidden' name='%s' value='%s' />\n",field_code,val));
						}
						
					} else {
						filter.append(String.format("<input type='hidden' name='%s' value='%s' />\n",field_code,value));
					}
				}else{
					if (type.equalsIgnoreCase("date") || type.equalsIgnoreCase("varchar") || type.equalsIgnoreCase("timestamp")){
						filter.append(String.format("<input type='hidden' name='%s' value='%s' />\n",field_code,""));
					}
					
				}
			}
			rsReport.first();
			String congnos_ip = rsReport.getString("congnos_ip");
			String report_path = rsReport.getString("report_path");
			//String param = URLEncoder.encode(report_path+filter.toString(),"UTF-8");
			//getResponse().sendRedirect(congnos_ip+"?"+report_path+filter.toString());
			String text = StringUtil.getResource(getContext(), "/WEB-INF/action/subject/report/reportgen/cognossearch/cognosSubject.txt", "UTF-8");
			Recordset rs = new Recordset();
			rs.append("cognos_url", Types.VARCHAR);
			rs.append("report_name", Types.VARCHAR);
			rs.append("input_params", Types.VARCHAR);
			rs.addNew();
			rs.setValue("cognos_url", congnos_ip);
			rs.setValue("report_name", report_path);
			rs.setValue("input_params", filter.toString());
			TemplateEngine template = new TemplateEngine(getContext(), getRequest(), text);
			template.replace(rs, "");
			byte [] bt = template.toString().getBytes();
			
			this.getResponse().getOutputStream().write(bt);
			
    	}catch(Throwable a){
    		getResponse().getWriter().write("<html><head></head><body><script language=\"javascript\">parent.jorSearchError('"+encodeJS(a.getMessage())+"');</script></body></html>");
    		getResponse().getWriter().close();
    	}
	}
	public String encodeJS(String input)
	{
		input = StringUtil.replace(input, "\\", "\\\\");
		input = StringUtil.replace(input, "\"", "\\\"");
		input = StringUtil.replace(input, "'", "\\\'");
		input = StringUtil.replace(input, "\r\n", "\\r\\n");
		input = StringUtil.replace(input, "\n", "\\n");
		return input;
	}	
	
}
