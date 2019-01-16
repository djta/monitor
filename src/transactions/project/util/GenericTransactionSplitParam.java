package transactions.project.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.IRecordsetProvider;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.xml.Document;
import dinamica.xml.Element;

/***
 * 参数分割查询
 * @author C
 * 2015-12-26
 */
public class GenericTransactionSplitParam extends GenericTransaction {
	
	/**
	 * Transaction service - this method must be redefined
	 * by descendants of this class, include a super.service(inputParams)
	 * as the first line of your service() method code to reuse base
	 * functionality (auto-creation of recordsets based on recordset elements defined in config.xml).
	 * In this method the business logic will be contained, and results will be represented
	 * as recordsets that will be consumed by Output objects. Recordsets are published using
	 * the method publish(id, rsObject). This class provides a method to retrieve
	 * a recordset using its ID and throws an error if the recordset is not present in the HashMap.<br>
	 * If inputParams is not null then it is published with the id "_request".
	 * @param inputParams Request parameters pre-validated and represented as a Recordset with one record.
	 * Recordset fields are set according to the data types defined in the validator.xml file.
	 * @return 0 if success - any other return values are user defined
	 * @throws Throwable
	 */
	public int service(Recordset inputParams) throws Throwable
	{
		int rc = createRecordsets(inputParams);
		
		if (inputParams!=null)
			publish("_request", inputParams);
		
		return rc;
		
	}
	
	/**
	 * Create recordsets using config.xml parameters.
	 * For recordsets created using SQL templates, all values
	 * from the inputParams recordset will be auto-replaced into
	 * the template. This recordset is only created when using
	 * a validator (validator.xml definition to auto-validate request parameters). 
	 * @throws Throwable in case of invalid config.xml parameters or JDBC exceptions.
	 */
	protected int createRecordsets(Recordset inputParams) throws Throwable
	{
	
		int rc = 0;
	
		/* get database object */
		Db db = getDb();

		Document doc = getConfig().getDocument();
		Element tele = doc.getElement(doc.getRoot(), "transaction");
		Element[] xx = doc.getElements(tele);
		int xxlen = ( null != xx ? xx.length : 0);
		Map<String, Element> eleMap = new HashMap<String, Element>();
		for( int i = 0; i < xxlen; i++ ){
			Element x = xx[i];
			if ( !x.getNodeName().equals("recordset") ) {
				continue;
			}
			String _id = x.getAttribute("id");
			eleMap.put(_id, x);
		}
		
		/* get recordsets config */
		Recordset rs = getConfig().getRecordsets();
		Recordset rs1 = null;
		List<Recordset> rsMultiple = null;
		
		/* for each defined recordset */
		while (rs.next())
		{
			/* get parameters */
			String id = (String)rs.getValue("id");
			String source = (String)rs.getValue("source");
			String scope = (String)rs.getValue("scope");
			String onempty = (String)rs.getValue("onempty");
			String maxRows = (String)rs.getValue("maxrows");
			int limit = 0;
			if (maxRows!=null)
				limit = Integer.parseInt(maxRows);
			String dataSrc = rs.getString("datasource");
			String params = rs.getString("params");
			String totalCols = rs.getString("totalCols");
			String multiple = rs.getString("multiple");

			/* create recordset using appropiate source */			
			if (source.equals("sql"))
			{

				if (db==null)
					throw new Throwable("La conexion a BD es nula, revisar config.xml para ver si el atributo <config nodb='true'>, eso causa una conexion nula.");
				
				String fetchSize = (String)rs.getValue("fetchsize");
				if (fetchSize==null)
					db.setFetchSize(0);
				else
					db.setFetchSize(Integer.parseInt(fetchSize));
				
				String sqlFile = getResource(id);
				sqlFile = this.getSQL(sqlFile, inputParams);
				// 获取需要分割的参数
				String paramlist = "";	// 参数名称，多个用英文,隔开
				String typelist = "";	// 参数类型，多个用英文,隔开，与参数名称一一对应:varchar、datetime、date、integer、numeric
				Element ele = eleMap.get(id);
				if( null != ele ){
					paramlist = ele.getAttribute("splitparam");
					typelist = ele.getAttribute("splittype");
				}
				if( StringUtils.isNotBlank(paramlist) && StringUtils.isNotBlank(typelist) ){
					String pArr[] = paramlist.split(",");
					String tArr[] = typelist.split(",");
					if( pArr.length != tArr.length ){
	                    throw new Throwable("The parameter splitparam and splittype lengths does not match.");
					}
					StringBuffer param = null;
					int plen = pArr.length;
					for( int k = 0; k < plen; k++ ){
						param = new StringBuffer();
						String name = pArr[k];
						String type = tArr[k];
						// 获取参数值
						String paramvalue = inputParams.getString(name);
						paramvalue = ( StringUtils.isNotBlank(paramvalue) ? paramvalue : "" );
						String vArr[] = paramvalue.split(",");
						int vlen = vArr.length;
						for( int j = 0; j < vlen; j++ ){
							String value = vArr[j];
							if ( StringUtils.isBlank(value) ){	// 传入参数值为空
								if( "varchar".equals(type) ){
									param.append(" OR ").append(name).append(" IS NULL OR ").append(name).append(" = ''");
								}else{
									param.append(" OR ").append(name).append(" IS NULL ");
								}
							}else{	// 传入参数值非空
								switch (type) {
									case "datetime":
										param.append(" OR ").append(name).append(" = '").append(value).append("'::timestamp");
										break;
									case "date":
										param.append(" OR ").append(name).append(" = '").append(value).append("'::date");
										break;
									case "integer":
										param.append(" OR ").append(name).append(" = ").append(value);
										break;
									case "numeric":
										param.append(" OR ").append(name).append(" = ").append(value);
										break;
									default:
										param.append(" OR ").append(name).append(" = '").append(value).append("'");
										break;
								}
							}
						}
						if( param.length() >= 0 ){
							String replaceparam = param.toString();
							replaceparam = " AND (" + replaceparam.substring(replaceparam.indexOf("OR")+2) + ")";
							sqlFile = StringUtil.replace(sqlFile, "${" + name + "}", replaceparam);
						}else{
							sqlFile = StringUtil.replace(sqlFile, "${" + name + "}", "");
						}
					}
				}
				
				if (params!=null)
				{
					//PATCH 2010-09-13 permite varios recordset de parametros separados por punta y coma
					String pams[] = StringUtil.split(params, ";");
					for (int i = 0; i < pams.length; i++) {
						// PATCH 2005-04-05 support for alternative input parameters recordset for SQL templates
						Recordset rsParams = getRecordset(pams[i]);
						if (rsParams.getRecordCount()>0)
							rsParams.first();
						else
							throw new Throwable("The recordset [" + pams[i] + "] used to replace SQL template values is empty.");
						sqlFile = this.getSQL(sqlFile, rsParams);	
					}
				}
				
				if (multiple!=null && multiple.equalsIgnoreCase("true")) {
					if (dataSrc!=null)
							rsMultiple = dbGetMultiple(dataSrc, sqlFile);
					else 
						rsMultiple = db.getMultiple(sqlFile);
					
				} else {
					
					//PATCH 2005-03-14 support datasource defined at recordset level
					if (dataSrc==null) {
						if (limit>0)
							rs1 = db.get(sqlFile, limit);
						else
							rs1 = db.get(sqlFile);
					} else {
						rs1 = dbGet(dataSrc, sqlFile, limit);
					}
					
					if (onempty!=null) {
						//patch 2009-07-08 - do not evaluate anymore recordsets if on-empty rule matches
						if (rs1.getRecordCount()==0) {
							rc = Integer.parseInt(onempty);
							break;
						}
					}
				}
				
			}
			else if (source.equals("session"))
			{
				rs1 = (Recordset)getSession().getAttribute(id);
				//PATCH 2005-03-01 - enhance error message if session attribute is null
				if (rs1==null)
					throw new Throwable("Recordset [" + id + "] not found in Session attribute, maybe the application was reloaded, destroying the session.");
			}
			else if (source.equals("request"))
			{
				rs1 = (Recordset)getRequest().getAttribute(id);
				if (rs1==null)
					throw new Throwable("Request attribute [" + id + "] does not contain a recordset.");
			}
			else if (source.equals("textfile"))
			{
				rs1 = this.getRsFromFlatFile(id);
			}
			else if (source.equals("class"))
			{
			    IRecordsetProvider rsProv = (IRecordsetProvider)getObject(id);
				rs1 = rsProv.getRecordset(inputParams);
				if (onempty!=null){
					if (rs1.getRecordCount()==0)
					rc = Integer.parseInt(onempty);	
				}				
			} else if (source.equals("total"))
			{
				//patch 2010-04-08 soporte para sumar o totalizar en memoria un campo
				if ( source.equals("total") && totalCols==null)
					throw new Throwable ("El atributo [totalCols] no ha sido definido para el elemento: recordset.");
				if ( source.equals("total") && params==null)
					throw new Throwable ("El atributo [params] no ha sido definido para el elemento: recordset.");
				
				//obtener array de los valores
				String cols[] = StringUtil.split(totalCols, ";");
				//armar recordset de total
				rs1 = new Recordset();
				for (int i = 0; i < cols.length; i++) {
					rs1.append(cols[i], java.sql.Types.DOUBLE);
				}
				rs1.addNew();
				
				//recordset de donde se sumara cada registro
				Recordset rsParams = getRecordset(params);
				
				//realizar operacion de sumatoria
				computeTotal(rs1, rsParams, cols);
			}
			else
			{
				throw new Throwable("Invalid recordset source in config.xml (" + getConfig().path + "). Source attribute values can be sql, session, textfile or request only.");
			}

			if (rsMultiple!=null) {
				int c = 0;
				for (Recordset recordset : rsMultiple) {
					String newID = id;
					if (c>0)
						newID = id + "." + c;
					publish(newID, recordset);
					c++;
					
					recordset.setID(newID);
					Recordset info = recordset.getRecordsetInfo();
					String infoID = newID + ".metadata";
					publish(infoID, info);

					if (scope.equals("session"))
						getSession().setAttribute(newID, recordset);
					else if (scope.equals("request"))
						getRequest().setAttribute(newID, recordset);
					else if (!scope.equals("transaction"))
						throw new Throwable("Invalid recordset scope in config.xml (" + getConfig().path + "). Scope attribute values can be transaction, session or request only.");
					
				}
				rsMultiple=null;
			} else {
				
				/* publish this recordset */
				publish(id, rs1);
				
				/* get recordset simple metadata (recordcount, pagecount, etc) */
				rs1.setID(id);
				Recordset info = rs1.getRecordsetInfo();
				
				/* publish this new recordset */
				String infoID = id + ".metadata";
				publish(infoID, info);
				
				/* persist recordset if necessary (in session or request object */
				if (scope.equals("session"))
					getSession().setAttribute(id, rs1);
				else if (scope.equals("request"))
					getRequest().setAttribute(id, rs1);
				else if (!scope.equals("transaction"))
					throw new Throwable("Invalid recordset scope in config.xml (" + getConfig().path + "). Scope attribute values can be transaction, session or request only.");
			}
			
		}
		return rc;
	}
	

}
