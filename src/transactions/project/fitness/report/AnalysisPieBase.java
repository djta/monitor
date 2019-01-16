package transactions.project.fitness.report;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;
import dinamica.xml.Element;

/**
 * Analysis Pie
 * @author C
 * 2018-03-08
 */
public class AnalysisPieBase extends GenericTransaction {
	
	protected static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
	
	protected static double fee = 0d;
	protected static int num = 0;
	protected static String descr = "";

	protected static final String COMPAREFLAG_tongbi = "T";	// 同比标志
	protected static final String COMPAREFLAG_huanbi = "H";	// 环比标志
	protected static final String COMPAREFLAG_other = "";	// 其他

	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		rc = super.service(inputParams);
		
		TimeZone zone = TimeZone.getTimeZone("GMT+8");

		Db db = getDb();
		
		Element q[] = getConfig().getDocument().getElements("query");
		if ( q != null){
			for (int t = 0; t < q.length; t++) {
				String queryName = q[t].getString();

				Date fdate = inputParams.getDate("fdate");	// 开始日期
				Calendar fcal = Calendar.getInstance(zone);
				fcal.setTime(fdate);
				Date tdate = inputParams.getDate("tdate");	// 结束日期
				Calendar tcal = Calendar.getInstance(zone);
				tcal.setTime(tdate);
				
				Recordset rs = this.getData(db, queryName, fcal, tcal, zone, COMPAREFLAG_other);
				publish(queryName, rs);
				
				Recordset tRs = null;
				Recordset hRs = null;
				if( inputParams.containsField("compareflag") && !inputParams.isNull("compareflag") ){
					String compareflag = inputParams.getString("compareflag");
					compareflag = (null != compareflag ? compareflag : "");
					/** 同比 */
					if( compareflag.contains(COMPAREFLAG_tongbi) ){
						Calendar tfcal = Calendar.getInstance(zone);
						tfcal.setTime(fdate);
						tfcal.add(Calendar.YEAR, -1);
						Calendar ttcal = Calendar.getInstance(zone);
						ttcal.setTime(tdate);
						ttcal.add(Calendar.YEAR, -1);
						tRs = this.getData(db, queryName, tfcal, ttcal, zone, compareflag);
					}
					/** 环比 */
					if( compareflag.contains(COMPAREFLAG_huanbi) ){
						Calendar tfcal = Calendar.getInstance(zone);
						tfcal.setTime(fdate);
						tfcal.add(Calendar.MONTH, -1);
						Calendar ttcal = Calendar.getInstance(zone);
						ttcal.setTime(tdate);
						ttcal.add(Calendar.YEAR, 0);
						hRs = this.getData(db, queryName, tfcal, ttcal, zone, compareflag);
					}
				}
				tRs = null != tRs ? tRs : this.createResultData();
				hRs = null != hRs ? hRs : this.createResultData();
				publish("tb-"+queryName, tRs);
				publish("hb-"+queryName, hRs);
			}
		}
		
		return rc;
	}

	private Recordset createResultData() throws RecordsetException{
		Recordset rs = new Recordset();
		rs.append("descr", Types.VARCHAR);
		rs.append("fee", Types.DOUBLE);	// 总费用
		rs.append("num", Types.INTEGER);	// 数量
		return rs;
	}

	private Recordset getData(Db db, String queryName, Calendar fcal, Calendar tcal, TimeZone zone, String compareflag) throws Throwable{
		Recordset rs = this.createResultData();

		String fdate = sdfDate.format(fcal.getTime());
		String tdate = sdfDate.format(tcal.getTime());
		
		String query = getResource(queryName);
		query = StringUtil.replace(query, "${fld:fdate}", "'"+fdate+"'::date");
		query = StringUtil.replace(query, "${fld:tdate}", "'"+tdate+"'::date");
		query = getSQL(query, null);
		Recordset rsData = db.get(query);
		
		while( rsData.next() ){
			rs.addNew();
//			if( compareflag.contains(COMPAREFLAG_tongbi) ){	/** 同比 */
//				rs.setValue("descr", this.getStringValue(rsData, "descr") + "[同]");
//			}else if( compareflag.contains(COMPAREFLAG_huanbi) ){	/** 环比 */
//				rs.setValue("descr", this.getStringValue(rsData, "descr") + "[环]");
//			}else{
				rs.setValue("descr", this.getStringValue(rsData, "descr"));
//			}
			rs.setValue("fee", this.getDoubleValue(rsData, "fee"));
			rs.setValue("num", this.getIntegerValue(rsData, "num"));
		}
		return rs;
	}
		
	private double getDoubleValue(Recordset rs, String fieldName) throws Throwable{
		Double value = 0d;
		if( rs.containsField(fieldName) && null != rs.getValue(fieldName) ){
			value = rs.getDouble(fieldName);	
		}
		return value;
	}

	private Integer getIntegerValue(Recordset rs, String fieldName) throws Throwable{
		int value = 0;
		if( rs.containsField(fieldName) && null != rs.getValue(fieldName) ){
			value = rs.getInteger(fieldName);	
		}
		return value;
	}
	
	private String getStringValue(Recordset rs, String fieldName) throws Throwable{
		String value = "";
		if( rs.containsField(fieldName) && null != rs.getValue(fieldName) ){
			value = rs.getString(fieldName);	
		}
		return value;
	}
}
