package com.ccms.report;

import dinamica.*;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

/**
 * Reporte PDF generico y basico con logo, titulo, tabla de data 
 * y pie de pagina con estilo "pagina X de Y". 
 * <br>
 * Esta clase lee metadata del config.xml y construye
 * la tabla con sus columnas, valores y formatos. El recordset
 * que suple la data debe ser provisto a esta clase por la clase
 * Transaction correspondiente al Action donde corren.<br><br>
 * 
 * Para parametrizar esta clase se definen elementos 
 * en el config.xml como en este ejemplo:<br><br>
 * <xmp>
 * <pdf-table recordset="query.sql" width="100" pageSize="letter" rotate="false" spacingBefore="70">
 *		<logo url="/images/logo.gif" scale="80" />
 *		<col name="tramite" title="Tr醡ite" width="80" align="left" />
 *		<col name="fecha" title="Fecha" format="dd-MM-yyyy" width="20" align="center" />
 *		<!--opcional-->
 *		<after-table-image scale="100" url="${def:actionroot}/chart"/>
 *	</pdf-table>
 *</xmp>
 * <br><br>
 * Si los campos son de tipo fecha o num閞icos, debe indicar el formato. Los anchos
 * son especificados en porcentajes.
 * <br><br>
 * Actualizado: 2010-03-18<br>
 * Framework Dinamica - Distribuido bajo licencia LGPL<br>
 * @author Francisco Galizia (galiziafrancisco@gmail.com)
 */
public class PDFReportTable extends AbstractPDFOutput
{

	//documento config.xml
	dinamica.xml.Document docXML = null;
	
	//elemento raiz de la configuracion del reporte "<pdf-table>"
	dinamica.xml.Element _table = null;
	
	//recordset que contendra la metadata
	Recordset rows = null;
	
	//parametros requeridos para el footer
    PdfTemplate tpl = null;
    BaseFont bf = null;
    PdfContentByte cb = null;
    Image img = null;
    Font tblHeaderFont = null;
    Font tblBodyFont = null;
    
    //parametros generales del reporte	
    String reportTitle = ""; //lo lee de config.xml por defecto
    String footerText = ""; //lo lee de web.xml o config.xml por defecto
    String logoPath = ""; //ubicacion del logotipo
    float scale = 100; //escala del grafico
    String pageXofY = " of ";  //texto por defecto para Pagina X de Y

    String fontName = BaseFont.HELVETICA; //font name
    String fontEncoding = BaseFont.CP1252; //font encoding
    boolean fontEmbedded = BaseFont.NOT_EMBEDDED; //font embedded
    
    protected void createPDF(GenericTransaction data, ByteArrayOutputStream buf)
            throws Throwable
    {
    	//permite acceder a los elementos definidos en el config.xml
    	docXML = getConfig().getDocument();
        _table = docXML.getElement("pdf-table");
       
        //recordset para almacenar definicion de columnas
	    rows = new Recordset ();
	    rows.append("name", java.sql.Types.VARCHAR);
	    rows.append("title", java.sql.Types.VARCHAR);
	    rows.append("format", java.sql.Types.VARCHAR);
	    rows.append("width", java.sql.Types.INTEGER);
	    rows.append("align", java.sql.Types.VARCHAR);

			Recordset rs = data.getRecordset("query_pdf_field.sql");
			if(rs.getRecordCount() == 0) return;
			rs.top();
	    
	    while (rs.next()) 
	    {
	    	rows.addNew();
	    	rows.setValue("name", rs.getString("colname"));
	    	rows.setValue("title", rs.getString("field_name"));
	    	rows.setValue("format", rs.getString("format"));
	    	rows.setValue("width", rs.getInteger("width"));
	    	rows.setValue("align", rs.getString("show_type"));
	    }

        //取得字段配置信息
	    dinamica.xml.Element pdf_font = docXML.getElement("//font");
	    if (pdf_font != null)
	    {
            fontName = pdf_font.getAttribute("name"); //font name
            fontEncoding = pdf_font.getAttribute("encoding"); //font encoding
            fontEmbedded = Boolean.parseBoolean(pdf_font.getAttribute("embedded")); //font embedded
	    }
	   
	    //obtener elemento del logo
	    dinamica.xml.Element logo = docXML.getElement("//logo");
	    if (logo == null)
	    	throw new Throwable("No se encontro el elemento <logo> en el archivo config.xml - forma parte obligatoria de la definici髇 de un reporte gen閞ico.");
	    	
	    logoPath = logo.getAttribute("url");
    	scale = Float.parseFloat(logo.getAttribute("scale"));
	    
    	//patch 20091119 - soportar configuracion del tamano de papel (letter|legal)
    	Rectangle ps = null;
    	String pageSize = _table.getAttribute("pageSize");
    	if (pageSize==null || pageSize.equals("letter")) {
    		ps = PageSize.LETTER;
    	} else if (pageSize.equals("legal")) {
    		ps = PageSize.LEGAL;
    	} else {
    		throw new Throwable("Invalid page size, letter|legal are the only valid options.");
    	}
    	
    	/*patch para poder rotar la pagina colocando un atributo rotate="true" en el config.xml*/
    	String r = _table.getAttribute("rotate");
    	if (r!=null && r.equals("true"))
    		ps = ps.rotate();
    	
		//inicializar documento: tamano de pagina, orientacion, margenes
		Document doc = new Document();
		PdfWriter docWriter = PdfWriter.getInstance(doc, buf);
		doc.setPageSize(ps);
		doc.setMargins(30,30,30,40);
		
		doc.open();
		bf = BaseFont.createFont(fontName, fontEncoding, fontEmbedded);
		
		//crear fonts por defecto
		tblHeaderFont = getTableHeaderFont(bf);
		tblBodyFont = getTableBodyFont(bf);
		
		//definir pie de pagina del lado izquierdo
		String footerText = this.getFooter(); //read it from config.xml or web.xml
		String dateFormat = getContext().getInitParameter("pdf-dateformat");
		if (dateFormat==null || dateFormat.equals(""))
				dateFormat = "yyyy-MM-dd HH:mm";
		String reportDate = StringUtil.formatDate(new java.util.Date(), dateFormat);
		
		//crear template (objeto interno de IText) y manejador de evento 
		//para imprimir el pie de pagina
		//bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		cb = docWriter.getDirectContent();
		tpl = cb.createTemplate(20, 14);
		docWriter.setPageEvent(new PDFPageEvents(footerText, pageXofY, tpl, bf, cb, reportDate));

		//titulo - lo lee de config.xml por defecto
		reportTitle = getReportTitle();
		Paragraph t = new Paragraph(reportTitle, getTitleFont(bf));
		t.setAlignment(Rectangle.ALIGN_RIGHT);
		doc.add(t);

		//logo
		img = Image.getInstance(this.callLocalAction(logoPath));
		img.scalePercent(scale);
		float imgY = doc.top() - img.getHeight();
		float imgX = doc.left();
		img.setAbsolutePosition(imgX, imgY);
		doc.add(img);	
		
		//imprimir antes de la tabla si es necesario
		beforePrintTable(data, doc, docWriter);
		
		//imprimir tabla principal
		PdfPTable dataTable = getDataTable(data);
		dataTable.setSpacingBefore(getDefaultSpacing());
		//agregar un row de total si es necesario
		addTotalRow(dataTable, data, doc, docWriter);
		doc.add(dataTable);

		//imprimir despues de la tabla si es necesario
		afterPrintTable(data, doc, docWriter);
		
		doc.close();
		docWriter.close();
		
    }

	/**
	 * Retorna una tabla conteniendo la data del recordset
	 * @param data Objeto de negocios que suple los recordsets
	 * @return
	 */
	protected PdfPTable getDataTable(GenericTransaction data) throws Throwable
	{

		//obtener recordset de data
		//obtiene el nombre del recordset mediante el atributo definido en el config.xml
		Recordset rs = data.getRecordset(_table.getAttribute("recordset"));
		rs.top();
		
		//definir array que contendra los tama駉s de la columnas
		int colWidth[] = new int[rows.getRecordCount()]; 
		
		rows.top();
		//recorrer el recordset y a馻dir los valores al array
		while(rows.next()){
			colWidth[rows.getRecordNumber()] = rows.getInt("width")==0?10:rows.getInt("width");
		}
		
		//definir estructura de la tabla
		//cuenta el numero de registros que contiene el recordset con la metadadata
		//del config.xml, para asi saber cuantas columnas tendra la tabla
		PdfPTable datatable = new PdfPTable(rows.getRecordCount());
		datatable.getDefaultCell().setPadding(3);
		int headerwidths[] = colWidth;
		datatable.setWidths(headerwidths);
		
		//mediante un atributo definido en el config.xml se sabe de que tama駉 sera la tabla
		datatable.setWidthPercentage(new Float(_table.getAttribute("width")));
		datatable.setHeaderRows(1);

		PdfPCell c = null;
		String v = "";

		rows.top();
		while(rows.next()){
			//encabezados de columnas
			c = new PdfPCell( new Phrase(rows.getString("title"), tblHeaderFont) );
			c.setGrayFill(0.95f);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			datatable.addCell(c);
		}
	
		//imprimir cuerpo de la tabla
		while (rs.next())
		{
			rows.top();
			while(rows.next()){

				int align = 0;
				//asignar a cada celda la alineacion
				if(rows.getString("align").equals("center"))
					align = Element.ALIGN_CENTER;
				
				if(rows.getString("align").equals("left"))
					align = Element.ALIGN_LEFT;
				
				if(rows.getString("align").equals("right"))
					align = Element.ALIGN_RIGHT;
			
				//asignar a la celda los valores con su respectivo formatos, segun el
				//tipo de dato que contenga
				RecordsetField rf = rs.getField(rows.getString("name"));
				switch (rf.getType()) {

					case java.sql.Types.DATE:
					case java.sql.Types.TIMESTAMP:
						if(!rows.isNull("format")){
							if (rs.isNull(rows.getString("name")))
								v = "";
							else							
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), rows.getString("format"));
						}else{
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), "yyyy-MM-dd");
						}
						break;

					case java.sql.Types.INTEGER:						
					case java.sql.Types.DOUBLE:
						if(!rows.isNull("format")){
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatNumber(rs.getValue(rows.getString("name")), rows.getString("format"));
						}else{
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

				c = new PdfPCell( new Phrase( v, tblBodyFont ) );
				c.setHorizontalAlignment(align);
				datatable.addCell(c);
				
			}
			
		}
	
		return datatable;
		
	}    
    
	/**
	 * Define el font a ser utilizado para los encabezados de la tabla,
	 * puede sobreescribir este metodo si desea cambiar el font en una
	 * subclase de este reporte
	 * @return Font
	 */
	protected Font getTableHeaderFont(BaseFont bf) {
		return new Font(bf, 10f, Font.BOLD);
	}
	
	/**
	 * Define el font a ser utilizado para el cuerpo de la tabla,
	 * puede sobreescribir este metodo si desea cambiar el font en una
	 * subclase de este reporte
	 * @return Font
	 */	
	protected Font getTableBodyFont(BaseFont bf) {
		return new Font(bf, 10f, Font.NORMAL);
	}
	
	/**
	 * Definir el font para el t韙ulo del reporte
	 * @return
	 */	
	protected Font getTitleFont(BaseFont bf) {
		return new Font(bf, 14f, Font.BOLD);
	}
	
	/**
	 * Por si desea imprimir algo antes de la tabla de datos
	 * @param data Transaction object pasado a esta clase Output, de aqui puede extraer los recordsets
	 * @param doc
	 * @param docWriter
	 * @throws Throwable
	 */
	protected void beforePrintTable(GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable
	{
		
		//imprimir filtro del reporte si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//pdf-table/record");
		if (e!=null) {

			//obtener recordset de filtro
			Recordset rs = data.getRecordset(e.getAttribute("recordset"));
			rs.first();
			
			//definir estructura de la tabla
			PdfPTable datatable = new PdfPTable(2);
			datatable.getDefaultCell().setPadding(3);
			int headerwidths[] = {50,50};
			datatable.setWidths(headerwidths);
			datatable.setWidthPercentage(Integer.parseInt(e.getAttribute("width")));
			datatable.setSpacingBefore(Integer.parseInt(e.getAttribute("spacingBefore")));
			
			PdfPCell c = null;
			
			//encabezado para toda la tabla
			c = new PdfPCell(new Phrase(e.getAttribute("title"), tblHeaderFont ));
			c.setGrayFill(0.95f);
			c.setColspan(2);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			datatable.addCell(c);
			
			dinamica.xml.Element cols[] = docXML.getElements("//pdf-table/record/col");
			for (int i = 0; i < cols.length; i++) 
			{
		    	dinamica.xml.Element elem = cols[i];
		    	
		    	String name  = elem.getAttribute("name");
		    	String title = elem.getAttribute("title");
		    	String format = elem.getAttribute("format");
		    	String align = elem.getAttribute("align");
		    	
		    	if(name == null)
		    		throw new Throwable("No se encontro el valor del atributo [name] del elemento <col> #" + (i+1));
		    	if(title == null)
		    		throw new Throwable("No se encontro el valor del atributo [title] del elemento <col> #" + (i+1));
		    	if(align == null)
		    		throw new Throwable("No se encontro el valor del atributo [align] del elemento <col> #" + (i+1));
		    	
		    	c = new PdfPCell(new Phrase(title, tblHeaderFont));
				c.setGrayFill(0.95f);
				c.setHorizontalAlignment(Element.ALIGN_LEFT);
				datatable.addCell(c);
		    	
				String v = "";
				RecordsetField f = rs.getField(name);
				switch (f.getType()) {
	
					case java.sql.Types.DATE:
					case java.sql.Types.TIMESTAMP:
						if(!rs.isNull(name)){
							if (format!=null)
								v = StringUtil.formatDate(rs.getDate(name), format);
							else
								v = StringUtil.formatDate(rs.getDate(name), "yyyy-MM-dd");
						} else
							v = "";
						break;
	
					case java.sql.Types.INTEGER:						
					case java.sql.Types.DOUBLE:
						if(!rs.isNull(name)){
							if (format!=null)
								v = StringUtil.formatNumber(rs.getValue(name), format);
							else
								v = StringUtil.formatNumber(rs.getValue(name), "#,###,###.##");
						} else
							v = "";
						break;
						
					default:
						v = rs.getString(name);
						break;
				}
				
				c = new PdfPCell( new Phrase( v , tblBodyFont ) );
				
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
	 * @param data Transaction object pasado a esta clase Output, de aqui puede extraer los recordsets
	 * @param doc
	 * @param docWriter
	 * @throws Throwable
	 */
	protected void afterPrintTable(GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable
	{
		
		//imprimir grafico si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//after-table-image");
		if (e!=null) {
			Recordset rs = data.getRecordset(e.getAttribute("recordset"));
			if(rs.getRecordCount() == 0) return;
			rs.top();
			Image img;
    	    while (rs.next()) 
    	    {
    	        String strUrl = StringUtil.replace(e.getAttribute("url"), "${chart_id}", rs.getString("chart_id"));
    	        //System.out.println(strUrl);
    			img = Image.getInstance(getImage(this.getServerBaseURL() + strUrl, false));
    			img.scalePercent(Integer.parseInt(e.getAttribute("scale")));
    			img.setAlignment(Element.ALIGN_CENTER);
    			doc.add(img);			
    		}
		}
		
	}

	protected void addTotalRow(PdfPTable tbl, GenericTransaction data, Document doc, PdfWriter docWriter) throws Throwable
	{

		//imprimir filtro del reporte si existe la configuracion
		dinamica.xml.Element e = docXML.getElement("//pdf-table/after-table-row");
		if (e!=null) {

			//obtener recordset de filtro
			Recordset rs = data.getRecordset(e.getAttribute("recordset"));
			rs.first();
			
			PdfPCell c = null;
    		String v = "";
			
			rows.top();
			while(rows.next()){

				int align = 0;
				//asignar a cada celda la alineacion
				if(rows.getString("align").equals("center"))
					align = Element.ALIGN_CENTER;
				
				if(rows.getString("align").equals("left"))
					align = Element.ALIGN_LEFT;
				
				if(rows.getString("align").equals("right"))
					align = Element.ALIGN_RIGHT;
			
				//asignar a la celda los valores con su respectivo formatos, segun el
				//tipo de dato que contenga
				RecordsetField rf = rs.getField(rows.getString("name"));
				switch (rf.getType()) {

					case java.sql.Types.DATE:
					case java.sql.Types.TIMESTAMP:
						if(!rows.isNull("format")){
							if (rs.isNull(rows.getString("name")))
								v = "";
							else							
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), rows.getString("format"));
						}else{
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatDate(rs.getDate(rows.getString("name")), "yyyy-MM-dd");
						}
						break;

					case java.sql.Types.INTEGER:						
					case java.sql.Types.DOUBLE:
						if(!rows.isNull("format")){
							if (rs.isNull(rows.getString("name")))
								v = "";
							else
								v = StringUtil.formatNumber(rs.getValue(rows.getString("name")), rows.getString("format"));
						}else{
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

				c = new PdfPCell( new Phrase( v, tblBodyFont ) );
				c.setHorizontalAlignment(align);

				tbl.addCell(c);
			}			
    	}
	}
	
	/**
	 * Retorna el espacio a imprimir antes de la tabla de datos,
	 * una subclase puede redefinir este metodo para variar el espacio.
	 * @return El espacio en puntos antes de la tabla de datos
	 */
	protected float getDefaultSpacing() {
		String sBefore = _table.getAttribute("spacingBefore");
		if (sBefore!=null)
			return Integer.parseInt(sBefore);
		else
			return 70;
	}

}
