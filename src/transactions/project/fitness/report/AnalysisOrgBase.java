package transactions.project.fitness.report;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import transactions.project.fitness.util.ErpTools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;
import dinamica.xml.Element;

/**
 * Analysis Org
 * @author C
 * 2017-12-20
 */
public class AnalysisOrgBase extends GenericTransaction {
	
	protected static final String DATATYPE_day = "0";	// 按天
	protected static final String DATATYPE_week = "1";	// 按周
	protected static final String DATATYPE_month = "2";	// 按月
	protected static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
	
	protected static double fee = 0d;
	protected static int num = 0;
	protected static int num1 = 0;
	protected static int status = 0;
	protected static String descr = "";

	protected static final String COMPAREFLAG_tongbi = "T";	// 同比标志
	protected static final String COMPAREFLAG_huanbi = "H";	// 环比标志
	protected static final String COMPAREFLAG_other = "";	// 其他

	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		rc = super.service(inputParams);
		
		TimeZone zone = TimeZone.getTimeZone("GMT+8");

		Db db = getDb();
		
		// 类型：0按天 1按周 2按月
		String dataType = "";
		if( inputParams.containsField("datatype") && !inputParams.isNull("datatype") && null != inputParams.getValue("datatype") ){
			dataType = inputParams.getString("datatype");
		}
		dataType = (!DATATYPE_week.equals(dataType) && !DATATYPE_month.equals(dataType) ? DATATYPE_day : dataType);
		
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
				
				Recordset rs = this.getData(db, queryName, fcal, tcal, dataType, zone, COMPAREFLAG_other, inputParams);
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
						tRs = this.getData(db, queryName, tfcal, ttcal, dataType, zone, compareflag, inputParams);
					}
					/** 环比 */
					if( compareflag.contains(COMPAREFLAG_huanbi) ){
						Calendar tfcal = Calendar.getInstance(zone);
						tfcal.setTime(fdate);
						tfcal.add(Calendar.MONTH, -1);
						Calendar ttcal = Calendar.getInstance(zone);
						ttcal.setTime(tdate);
						ttcal.add(Calendar.YEAR, 0);
						hRs = this.getData(db, queryName, tfcal, ttcal, dataType, zone, compareflag, inputParams);
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
		rs.append("createdate", Types.VARCHAR);
		rs.append("descr", Types.VARCHAR);
		rs.append("fee", Types.DOUBLE);	// 总费用
		rs.append("num", Types.INTEGER);	// 数量
		rs.append("num1", Types.INTEGER);	// 数量
		rs.append("status", Types.INTEGER);	// 状态
		return rs;
	}

	private Recordset getData(Db db, String queryName, Calendar fcal, Calendar tcal, String dataType, TimeZone zone, String compareflag, Recordset inputParams) throws Throwable{
		Recordset rs = this.createResultData();
		Calendar cal = Calendar.getInstance(zone);

		String fdate = sdfDate.format(fcal.getTime());
		String tdate = sdfDate.format(tcal.getTime());
		
		String query = getResource(queryName);
		query = StringUtil.replace(query, "${fld:fdate}", "'"+fdate+"'::date");
		query = StringUtil.replace(query, "${fld:tdate}", "'"+tdate+"'::date");
		query = getSQL(query, inputParams);
		Recordset rsData = db.get(query);
		
		Calendar thisCal = fcal;
		while( thisCal.getTimeInMillis() <= tcal.getTimeInMillis() ){
			Calendar startCal = Calendar.getInstance(zone);
			startCal.setTime(thisCal.getTime());

			rs.addNew();
			Date thisDate = thisCal.getTime();

			Date createdate = null;
			if( compareflag.contains(COMPAREFLAG_tongbi) ){	/** 同比 */
				Calendar compareCal = Calendar.getInstance(zone);
				compareCal.setTime(thisDate);
				compareCal.add(Calendar.YEAR, -1);
				createdate = compareCal.getTime();
			}else if( compareflag.contains(COMPAREFLAG_huanbi) ){	/** 环比 */
				Calendar compareCal = Calendar.getInstance(zone);
				compareCal.setTime(thisDate);
				compareCal.add(Calendar.MONTH, -1);
				createdate = compareCal.getTime();
			}else{
				createdate = thisDate;
			}
			
			if( DATATYPE_week.equals(dataType) ){	// 按周
				rs.setValue("createdate", ErpTools.syncFormatDate(sdfDate, createdate));
				thisCal.add(Calendar.DATE, 6);
			}else if( DATATYPE_month.equals(dataType) ){	// 按月
				rs.setValue("createdate",  ErpTools.syncFormatDate(sdfMonth, createdate));
				thisCal.add(Calendar.MONTH, 1);
			}else{	// 按天
				rs.setValue("createdate", ErpTools.syncFormatDate(sdfDate, createdate));
				thisCal.add(Calendar.DATE, 1);
			}

			// 计算费用数量
			this.calNum(rsData, cal, startCal, thisCal, dataType);
			rs.setValue("fee", fee);
			rs.setValue("num", num);
			rs.setValue("num1", num1);
			rs.setValue("status", status);
		}
		return rs;
	}
	
	/***
	 * 按日期计算金额
	 * @param rs
	 * @param cal
	 * @param startCal
	 * @param endCal
	 * @param dataType
	 * @return
	 * @throws Throwable
	 */
	private double calNum(Recordset rs, Calendar cal, Calendar startCal, Calendar endCal, String dataType) throws Throwable{
		fee = 0d;num = 0;num1 = 0;descr = "";
		if( rs.getRecordCount() > 0 ){
			Date thisDate = startCal.getTime();
			
			Recordset rsTemp = rs.copy();
			rsTemp.sort("createdate");
			while( rsTemp.next() ){
				Date createdate = rsTemp.getDate("createdate");
				cal.setTime(createdate);
				long thisMillis = cal.getTimeInMillis();
				if( thisMillis == startCal.getTimeInMillis() && DATATYPE_day.equals(dataType) ){
					fee += getDoubleValue(rsTemp, "fee");
					num += getIntegerValue(rsTemp, "num");
					num1 += getIntegerValue(rsTemp, "num1");
					descr = getStringValue(rsTemp, "descr");
				}else if( thisMillis >= startCal.getTimeInMillis() && thisMillis < endCal.getTimeInMillis() && DATATYPE_week.equals(dataType) ){
					fee += getDoubleValue(rsTemp, "fee");
					num += getIntegerValue(rsTemp, "num");
					num1 += getIntegerValue(rsTemp, "num1");
					descr = getStringValue(rsTemp, "descr");
				}else if( ErpTools.syncFormatDate(sdfMonth, thisDate).equals(ErpTools.syncFormatDate(sdfMonth, createdate)) && DATATYPE_month.equals(dataType) ){
					fee += getDoubleValue(rsTemp, "fee");
					num += getIntegerValue(rsTemp, "num");
					num1 += getIntegerValue(rsTemp, "num1");
					descr = getStringValue(rsTemp, "descr");
				}else{
					continue;
				}
			}
		}
		return fee;
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
