package com.ccms.report2;

import jatools.designer.App;
import jatools.engine.ReportJob;
import jatools.server.FileFinder;
import jatools.server.ReportExporter;
import jatools.server.ReportWriter;

import javax.servlet.ServletException;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.StringUtil;

public class JorReportOutput extends GenericOutput{

	public void print(GenericTransaction data) throws Throwable
	{
		String action = getRequest().getParameter(ReportJob.ACTION_TYPE);

        if ("export".equals(action)) {
            ReportExporter.service(getRequest(), getResponse());
        } else if (action == null || action.length() == 0) {
        	try{
        		ReportWriter.service(getRequest(), getResponse());
                getResponse().getWriter().write("<script language=\"javascript\">parent.jorSearchOK();</script>");
        	}catch(Throwable a){
        		getResponse().getWriter().write("<html><head></head><body><script language=\"javascript\">parent.jorSearchError('"+encodeJS(a.getMessage())+"');</script></body></html>");
        		getResponse().getWriter().close();
        	}
        } else if ("tempfile".equals(action)) {
            FileFinder.service(getRequest(), getResponse());
        } else {
            throw new ServletException(App.messages.getString("res.42") + action + ".");
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
