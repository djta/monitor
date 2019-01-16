package com.ccms.core.formgen;

import java.io.ByteArrayOutputStream;

import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
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
import dinamica.GenericTransaction;
import dinamica.PDFPageEvents;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;

public class FormPDFOutput extends AbstractPDFOutput {

	dinamica.xml.Document docXML = null;
	dinamica.xml.Element _table = null;
	Recordset rows = null;

	PdfTemplate tpl = null;
	BaseFont bf = null;
	PdfContentByte cb = null;
	Image img = null;
	Font tblHeaderFont = null;
	Font tblBodyFont = null;

	// parametros generales del reporte
	String reportTitle = ""; // lo lee de config.xml por defecto
	String footerText = ""; // lo lee de web.xml o config.xml por defecto
	String logoPath = ""; // ubicacion del logotipo
	float scale = 100; // escala del grafico
	String pageXofY = " of "; // texto por defecto para Pagina X de Y

	String fontName = BaseFont.HELVETICA; // font name
	String fontEncoding = BaseFont.CP1252; // font encoding
	boolean fontEmbedded = BaseFont.NOT_EMBEDDED; // font embedded

	protected void createPDF(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable {

		String suffix = getConfig().getConfigValue("suffix-col", "form_id");
		String suffixValue = getRequest().getParameter(suffix);
		
		docXML = getConfig().getDocument();
		_table = docXML.getElement("pdf-table");

		// recordset para almacenar definicion de columnas
		rows = new Recordset();
		rows.append("name", java.sql.Types.VARCHAR);
		rows.append("title", java.sql.Types.VARCHAR);
		rows.append("format", java.sql.Types.VARCHAR);
		rows.append("width", java.sql.Types.INTEGER);
		rows.append("align", java.sql.Types.VARCHAR);

		//每次都从缓存中重新复制一份
		CacheProvider cp = new CacheProviderImpl();
		DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
		Integer form_id = document.getForm_id();
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
			fontEmbedded = Boolean.parseBoolean(pdf_font.getAttribute("embedded")); // font
																					// embedded
		}

		// obtener elemento del logo
		dinamica.xml.Element logo = docXML.getElement("//logo");
		if (logo == null)
			throw new Throwable(
					"No se encontro el elemento <logo> en el archivo config.xml - forma parte obligatoria de la definici髇 de un reporte gen閞ico.");

		logoPath = logo.getAttribute("url");
		scale = Float.parseFloat(logo.getAttribute("scale"));

		// patch 20091119 - soportar configuracion del tamano de papel
		// (letter|legal)
		Rectangle ps = null;
		String pageSize = _table.getAttribute("pageSize");
		if (pageSize == null || pageSize.equals("letter")) {
			ps = PageSize.LETTER;
		} else if (pageSize.equals("legal")) {
			ps = PageSize.LEGAL;
		} else {
			throw new Throwable("Invalid page size, letter|legal are the only valid options.");
		}

		/*
		 * patch para poder rotar la pagina colocando un atributo rotate="true"
		 * en el config.xml
		 */
		String r = _table.getAttribute("rotate");
		if (r != null && r.equals("true"))
			ps = ps.rotate();

		// inicializar documento: tamano de pagina, orientacion, margenes
		Document doc = new Document();
		PdfWriter docWriter = PdfWriter.getInstance(doc, buf);
		doc.setPageSize(ps);
		doc.setMargins(30, 30, 30, 40);

		doc.open();
		bf = BaseFont.createFont(fontName, fontEncoding, fontEmbedded);

		// crear fonts por defecto
		tblHeaderFont = getTableHeaderFont(bf);
		tblBodyFont = getTableBodyFont(bf);

		// definir pie de pagina del lado izquierdo
		String footerText = this.getFooter(); // read it from config.xml or
												// web.xml
		String dateFormat = getContext().getInitParameter("pdf-dateformat");
		if (dateFormat == null || dateFormat.equals(""))
			dateFormat = "yyyy-MM-dd HH:mm";
		String reportDate = StringUtil.formatDate(new java.util.Date(), dateFormat);

		// crear template (objeto interno de IText) y manejador de evento
		// para imprimir el pie de pagina
		// bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
		// BaseFont.NOT_EMBEDDED);
		cb = docWriter.getDirectContent();
		tpl = cb.createTemplate(20, 14);
		docWriter.setPageEvent(new PDFPageEvents(footerText, pageXofY, tpl, bf, cb, reportDate));

		// titulo - lo lee de config.xml por defecto
		reportTitle = getReportTitle();
		Paragraph t = new Paragraph(reportTitle, getTitleFont(bf));
		t.setAlignment(Rectangle.ALIGN_RIGHT);
		doc.add(t);

		// logo
		img = Image.getInstance(this.callLocalAction(logoPath));
		img.scalePercent(scale);
		float imgY = doc.top() - img.getHeight();
		float imgX = doc.left();
		img.setAbsolutePosition(imgX, imgY);
		doc.add(img);

		// imprimir antes de la tabla si es necesario
		beforePrintTable(data, doc, docWriter);

		// imprimir tabla principal
		PdfPTable dataTable = getDataTable(data);
		dataTable.setSpacingBefore(getDefaultSpacing());
		// agregar un row de total si es necesario
		addTotalRow(dataTable, data, doc, docWriter);
		doc.add(dataTable);

		// imprimir despues de la tabla si es necesario
		afterPrintTable(data, doc, docWriter);

		doc.close();
		docWriter.close();

	}

	/**
	 * Retorna una tabla conteniendo la data del recordset
	 * 
	 * @param data
	 *            Objeto de negocios que suple los recordsets
	 * @return
	 */
	protected PdfPTable getDataTable(GenericTransaction data) throws Throwable {
		String suffix = getConfig().getConfigValue("suffix-col", "form_id");
		String suffixValue = getRequest().getParameter(suffix);

		// obtener recordset de data
		// obtiene el nombre del recordset mediante el atributo definido en el
		// config.xml
		Recordset rs = (Recordset) getRequest().getSession().getAttribute(
				_table.getAttribute("recordset") + "_" + (suffixValue == null ? "" : suffixValue));
		rs.top();

		// definir array que contendra los tama駉s de la columnas
		int colWidth[] = new int[rows.getRecordCount()];

		rows.top();
		// recorrer el recordset y a馻dir los valores al array
		while (rows.next()) {
			colWidth[rows.getRecordNumber()] = rows.getInt("width") == 0 ? 10 : rows.getInt("width");
		}

		// definir estructura de la tabla
		// cuenta el numero de registros que contiene el recordset con la
		// metadadata
		// del config.xml, para asi saber cuantas columnas tendra la tabla
		PdfPTable datatable = new PdfPTable(rows.getRecordCount());
		datatable.getDefaultCell().setPadding(3);
		int headerwidths[] = colWidth;
		datatable.setWidths(headerwidths);

		// mediante un atributo definido en el config.xml se sabe de que tama駉
		// sera la tabla
		datatable.setWidthPercentage(new Float(_table.getAttribute("width")));
		datatable.setHeaderRows(1);

		PdfPCell c = null;
		String v = "";

		rows.top();
		while (rows.next()) {
			// encabezados de columnas
			c = new PdfPCell(new Phrase(rows.getString("title"), tblHeaderFont));
			c.setGrayFill(0.95f);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			datatable.addCell(c);
		}

		// imprimir cuerpo de la tabla
		while (rs.next()) {
			rows.top();
			while (rows.next()) {

				int align = 0;
				// asignar a cada celda la alineacion
				if ("center".equals(rows.getString("align")))
					align = Element.ALIGN_CENTER;

				if ("left".equals(rows.getString("align")))
					align = Element.ALIGN_LEFT;

				if ("right".equals(rows.getString("align")))
					align = Element.ALIGN_RIGHT;

				// asignar a la celda los valores con su respectivo formatos,
				// segun el
				// tipo de dato que contenga
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

	/**
	 * Define el font a ser utilizado para los encabezados de la tabla, puede
	 * sobreescribir este metodo si desea cambiar el font en una subclase de
	 * este reporte
	 * 
	 * @return Font
	 */
	protected Font getTableHeaderFont(BaseFont bf) {
		return new Font(bf, 10f, Font.BOLD);
	}

	/**
	 * Define el font a ser utilizado para el cuerpo de la tabla, puede
	 * sobreescribir este metodo si desea cambiar el font en una subclase de
	 * este reporte
	 * 
	 * @return Font
	 */
	protected Font getTableBodyFont(BaseFont bf) {
		return new Font(bf, 10f, Font.NORMAL);
	}

	/**
	 * Definir el font para el t韙ulo del reporte
	 * 
	 * @return
	 */
	protected Font getTitleFont(BaseFont bf) {
		return new Font(bf, 14f, Font.BOLD);
	}

	/**
	 * Por si desea imprimir algo antes de la tabla de datos
	 * 
	 * @param data
	 *            Transaction object pasado a esta clase Output, de aqui puede
	 *            extraer los recordsets
	 * @param doc
	 * @param docWriter
	 * @throws Throwable
	 */
	protected void beforePrintTable(GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable {

		// imprimir filtro del reporte si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//pdf-table/record");
		if (e != null) {

			// obtener recordset de filtro
			Recordset rs = data.getRecordset(e.getAttribute("recordset"));
			rs.first();

			// definir estructura de la tabla
			PdfPTable datatable = new PdfPTable(2);
			datatable.getDefaultCell().setPadding(3);
			int headerwidths[] = { 50, 50 };
			datatable.setWidths(headerwidths);
			datatable.setWidthPercentage(Integer.parseInt(e.getAttribute("width")));
			datatable.setSpacingBefore(Integer.parseInt(e.getAttribute("spacingBefore")));

			PdfPCell c = null;

			// encabezado para toda la tabla
			c = new PdfPCell(new Phrase(e.getAttribute("title"), tblHeaderFont));
			c.setGrayFill(0.95f);
			c.setColspan(2);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			datatable.addCell(c);

			dinamica.xml.Element cols[] = docXML.getElements("//pdf-table/record/col");
			for (int i = 0; i < cols.length; i++) {
				dinamica.xml.Element elem = cols[i];

				String name = elem.getAttribute("name");
				String title = elem.getAttribute("title");
				String format = elem.getAttribute("format");
				String align = elem.getAttribute("align");

				if (name == null)
					throw new Throwable("No se encontro el valor del atributo [name] del elemento <col> #" + (i + 1));
				if (title == null)
					throw new Throwable("No se encontro el valor del atributo [title] del elemento <col> #" + (i + 1));
				if (align == null)
					throw new Throwable("No se encontro el valor del atributo [align] del elemento <col> #" + (i + 1));

				c = new PdfPCell(new Phrase(title, tblHeaderFont));
				c.setGrayFill(0.95f);
				c.setHorizontalAlignment(Element.ALIGN_LEFT);
				datatable.addCell(c);

				String v = "";
				RecordsetField f = rs.getField(name);
				switch (f.getType()) {

				case java.sql.Types.DATE:
				case java.sql.Types.TIMESTAMP:
					if (!rs.isNull(name)) {
						if (format != null)
							v = StringUtil.formatDate(rs.getDate(name), format);
						else
							v = StringUtil.formatDate(rs.getDate(name), "dd-MM-yyyy");
					} else
						v = "";
					break;

				case java.sql.Types.INTEGER:
				case java.sql.Types.DOUBLE:
					if (!rs.isNull(name)) {
						if (format != null)
							v = StringUtil.formatNumber(rs.getValue(name), format);
						else
							v = StringUtil.formatNumber(rs.getValue(name), "#,###,##0.00");
					} else
						v = "";
					break;

				default:
					v = rs.getString(name);
					break;
				}

				c = new PdfPCell(new Phrase(v, tblBodyFont));

				if (align.equals("left"))
					c.setHorizontalAlignment(Element.ALIGN_LEFT);
				else if (align.equals("center"))
					c.setHorizontalAlignment(Element.ALIGN_CENTER);
				else
					c.setHorizontalAlignment(Element.ALIGN_RIGHT);
				datatable.addCell(c);
			}

			doc.add(datatable);

		}

	}

	/**
	 * Por si desea imprimir algo luego de la tabla de datos
	 * 
	 * @param data
	 *            Transaction object pasado a esta clase Output, de aqui puede
	 *            extraer los recordsets
	 * @param doc
	 * @param docWriter
	 * @throws Throwable
	 */
	protected void afterPrintTable(GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable {

		// imprimir grafico si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//after-table-image");
		if (e != null) {
			Image img = Image.getInstance(getImage(this.getServerBaseURL() + e.getAttribute("url"), false));
			img.scalePercent(Integer.parseInt(e.getAttribute("scale")));
			img.setAlignment(Element.ALIGN_CENTER);
			doc.add(img);
		}

	}

	protected void addTotalRow(PdfPTable tbl, GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable {

		// imprimir filtro del reporte si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//pdf-table/after-table-row");
		if (e != null) {

			// obtener recordset de filtro
			Recordset rs = data.getRecordset(e.getAttribute("recordset"));
			rs.first();

			PdfPCell c = null;

			dinamica.xml.Element cols[] = docXML.getElements("//pdf-table/after-table-row/col");
			for (int i = 0; i < cols.length; i++) {
				dinamica.xml.Element elem = cols[i];

				String name = elem.getAttribute("name");
				String value = elem.getAttribute("value");
				String colspan = elem.getAttribute("colspan");
				String format = elem.getAttribute("format");
				String align = elem.getAttribute("align");

				String v = "";

				if (name == null) {
					v = value;
					if (v == null)
						v = "";
				} else {
					RecordsetField f = rs.getField(name);
					switch (f.getType()) {

					case java.sql.Types.DATE:
					case java.sql.Types.TIMESTAMP:
						if (!rs.isNull(name)) {
							if (format != null)
								v = StringUtil.formatDate(rs.getDate(name), format);
							else
								v = StringUtil.formatDate(rs.getDate(name), "dd-MM-yyyy");
						} else
							v = "";
						break;

					case java.sql.Types.INTEGER:
					case java.sql.Types.DOUBLE:
						if (!rs.isNull(name)) {
							if (format != null)
								v = StringUtil.formatNumber(rs.getValue(name), format);
							else
								v = StringUtil.formatNumber(rs.getValue(name), "#,###,##0.00");
						} else
							v = "";
						break;

					default:
						v = rs.getString(name);
						break;
					}
				}

				c = new PdfPCell(new Phrase(v, tblBodyFont));

				if (align.equals("left"))
					c.setHorizontalAlignment(Element.ALIGN_LEFT);
				else if (align.equals("center"))
					c.setHorizontalAlignment(Element.ALIGN_CENTER);
				else
					c.setHorizontalAlignment(Element.ALIGN_RIGHT);

				if (colspan != null)
					c.setColspan(Integer.parseInt(colspan));

				tbl.addCell(c);

			}

		}

	}

	/**
	 * Retorna el espacio a imprimir antes de la tabla de datos, una subclase
	 * puede redefinir este metodo para variar el espacio.
	 * 
	 * @return El espacio en puntos antes de la tabla de datos
	 */
	protected float getDefaultSpacing() {
		String sBefore = _table.getAttribute("spacingBefore");
		if (sBefore != null)
			return Integer.parseInt(sBefore);
		else
			return 70;
	}

}
