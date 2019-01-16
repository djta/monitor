package com.ccms.core.formengine;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;
import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import dinamica.AbstractPDFOutput;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.PDFPageEvents;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;

public class FormPDFOutputEngine extends AbstractPDFOutput {

	dinamica.xml.Document docXML = null;
	dinamica.xml.Element _table = null;
	Recordset rows = null;

	PdfTemplate tpl = null;
	BaseFont bf = null;
	PdfContentByte cb = null;
	Font tblHeaderFont = null;
	Font tblBodyFont = null;

	String reportTitle = "";
	String footerText = "";
	String pageXofY = " of ";

	String fontName = BaseFont.HELVETICA;
	String fontEncoding = BaseFont.CP1252;
	boolean fontEmbedded = BaseFont.NOT_EMBEDDED;
	
	private Integer form_id = null;

	protected void createPDF(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable {

		docXML = getConfig().getDocument();
		_table = docXML.getElement("pdf-table");

		rows = new Recordset();
		rows.append("name", java.sql.Types.VARCHAR);
		rows.append("title", java.sql.Types.VARCHAR);
		rows.append("format", java.sql.Types.VARCHAR);
		rows.append("width", java.sql.Types.INTEGER);
		rows.append("align", java.sql.Types.VARCHAR);

		//每次都从缓存中重新复制一份
		CacheProvider cp = new FormProviderImpl();
		form_id = Integer.parseInt(getRequest().getParameter("form_id"));
		FormBean form = cp.getFormBeanById(form_id);
		Recordset rs = form.getQueryPdfField().copy();
		String locale = getRequest().getSession().getAttribute("dinamica.user.locale").toString();
		
		if (rs.getRecordCount() == 0)
			return;
		rs.top();

		while (rs.next()) {
			rows.addNew();
			
			rows.setValue("name", rs.getString("colname"));
			if("en".equalsIgnoreCase(locale)){
				rows.setValue("title", rs.getString("field_name_en"));
			}else{
				rows.setValue("title", rs.getString("field_name_cn"));
			}
			
			rows.setValue("format", rs.getString("format"));
			rows.setValue("width", rs.getString("width")==null?50:rs.getInteger("width"));
			rows.setValue("align", rs.getString("show_type"));
		}

		// 取得字段配置信息
		dinamica.xml.Element pdf_font = docXML.getElement("//font");
		if (pdf_font != null) {
			fontName = pdf_font.getAttribute("name"); // font name
			fontEncoding = pdf_font.getAttribute("encoding"); // font encoding
			fontEmbedded = Boolean.parseBoolean(pdf_font.getAttribute("embedded"));
		}

		Rectangle ps = null;
		String pageSize = _table.getAttribute("pageSize");
		if (pageSize == null || pageSize.equals("letter")) {
			ps = PageSize.LETTER;
		} else if (pageSize.equals("legal")) {
			ps = PageSize.LEGAL;
		} else {
			throw new Throwable("Invalid page size, letter|legal are the only valid options.");
		}

		String r = _table.getAttribute("rotate");
		if (r != null && r.equals("true"))
			ps = ps.rotate();

		Document doc = new Document();
		PdfWriter docWriter = PdfWriter.getInstance(doc, buf);
		doc.setPageSize(ps);
		doc.setMargins(30, 30, 30, 40);

		doc.open();
		bf = BaseFont.createFont(fontName, fontEncoding, fontEmbedded);

		tblHeaderFont = getTableHeaderFont(bf);
		tblBodyFont = getTableBodyFont(bf);

		String footerText = this.getFooter();
		String dateFormat = getContext().getInitParameter("pdf-dateformat");
		if (dateFormat == null || dateFormat.equals(""))
			dateFormat = "yyyy-MM-dd HH:mm";
		String reportDate = StringUtil.formatDate(new java.util.Date(), dateFormat);

		cb = docWriter.getDirectContent();
		tpl = cb.createTemplate(20, 14);
		docWriter.setPageEvent(new PDFPageEvents(footerText, pageXofY, tpl, bf, cb, reportDate));

		reportTitle = getReportTitle();
		Paragraph t = new Paragraph(reportTitle, getTitleFont(bf));
		t.setAlignment(Rectangle.ALIGN_RIGHT);
		doc.add(t);

		PdfPTable dataTable = getDataTable(data);
		dataTable.setSpacingBefore(getDefaultSpacing());
		doc.add(dataTable);

		doc.close();
		docWriter.close();
	}

	protected PdfPTable getDataTable(GenericTransaction data) throws Throwable {
		String querySql = (String)getRequest().getSession().getAttribute("QuerySQL_" + form_id);
		Recordset rs = null;

		//获取数据库连接
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");
				
		if (getConfig().transDataSource!=null)
			dataSourceName = getConfig().transDataSource;
		
		if (jndiPrefix==null)
			jndiPrefix="";
		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);
		Connection conn = null;
		try
		{
			conn = ds.getConnection();
			Db db = new Db(conn);
			Dialect dialect = DialectFactory.buildDialect(conn.getMetaData().getDatabaseProductName().toLowerCase());
			//限制最大打印条数
			int maxRecord = Integer.parseInt(_table.getAttribute("maxRecord"));
			String sql = dialect.getLimitString(querySql, 0, maxRecord);
			rs = db.get(sql);
		}
		catch (Throwable e)
		{
			throw e;
		}
		finally{
			if(conn != null){
				conn.close();
			}
		}
		
		int colWidth[] = new int[rows.getRecordCount()];

		rows.top();
		while (rows.next()) {
			colWidth[rows.getRecordNumber()] = rows.getInt("width") == 0 ? 10 : rows.getInt("width");
		}

		PdfPTable datatable = new PdfPTable(rows.getRecordCount());
		datatable.getDefaultCell().setPadding(3);
		int headerwidths[] = colWidth;
		datatable.setWidths(headerwidths);

		datatable.setWidthPercentage(new Float(_table.getAttribute("width")));
		datatable.setHeaderRows(1);

		PdfPCell c = null;
		String v = "";

		rows.top();
		while (rows.next()) {
			c = new PdfPCell(new Phrase(rows.getString("title"), tblHeaderFont));
			c.setGrayFill(0.95f);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			datatable.addCell(c);
		}

		while (rs.next()) {
			rows.top();
			while (rows.next()) {

				int align = 0;
				if ("center".equals(rows.getString("align")))
					align = Element.ALIGN_CENTER;

				if ("left".equals(rows.getString("align")))
					align = Element.ALIGN_LEFT;

				if ("right".equals(rows.getString("align")))
					align = Element.ALIGN_RIGHT;

				//判断列在加载界面中是否存在
				if(rs.containsField(rows.getString("name"))){
					RecordsetField rf = rs.getField(rows.getString("name"));
					switch (rf.getType()) {

					case java.sql.Types.DATE:
					case java.sql.Types.TIMESTAMP:
						if (!rows.isNull("format")) {
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), rows.getString("format"));
						} else {
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), "dd-MM-yyyy");
						}
						break;

					case java.sql.Types.INTEGER:
					case java.sql.Types.DOUBLE:
						if (!rows.isNull("format")) {
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatNumber(rs.getValue(rows.getString("name")), rows.getString("format"));
						} else {
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatNumber(rs.getValue(rows.getString("name")), "#,###,###.##");
						}
						break;

					default:
						v = rs.getString(rows.getString("name"));
						break;
					}
				}else{
					v = "请检查列配置";
				}

				c = new PdfPCell(new Phrase(v, tblBodyFont));
				c.setHorizontalAlignment(align);
				datatable.addCell(c);

			}

		}
		return datatable;
	}

	protected Font getTableHeaderFont(BaseFont bf) {
		return new Font(bf, 10f, Font.BOLD);
	}

	protected Font getTableBodyFont(BaseFont bf) {
		return new Font(bf, 10f, Font.NORMAL);
	}

	protected Font getTitleFont(BaseFont bf) {
		return new Font(bf, 14f, Font.BOLD);
	}

	protected float getDefaultSpacing() {
		String sBefore = _table.getAttribute("spacingBefore");
		if (sBefore != null)
			return Integer.parseInt(sBefore);
		else
			return 70;
	}

}
