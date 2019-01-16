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

/**
 * Analysis Org
 * @author C
 * 2017-12-20
 */
public class AnalysisOrgPos extends GenericTransaction {
	
	protected static final String DATATYPE_day = "0";	// 按天
	protected static final String DATATYPE_week = "1";	// 按周
	protected static final String DATATYPE_month = "2";	// 按月
	protected static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");

	protected static final String COMPAREFLAG_tongbi = "T";	// 同比标志
	protected static final String COMPAREFLAG_huanbi = "H";	// 环比标志
	protected static final String COMPAREFLAG_other = "";	// 其他

	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		rc = super.service(inputParams);
		
		TimeZone zone = TimeZone.getTimeZone("GMT+8");
		
		Date fdate = inputParams.getDate("fdate");	// 开始日期
		Calendar fcal = Calendar.getInstance(zone);
		fcal.setTime(fdate);
		Date tdate = inputParams.getDate("tdate");	// 结束日期
		Calendar tcal = Calendar.getInstance(zone);
		tcal.setTime(tdate);

		Db db = getDb();
		
		// 类型：0按天 1按周 2按月
		String dataType = "";
		if( inputParams.containsField("datatype") && !inputParams.isNull("datatype") && null != inputParams.getValue("datatype") ){
			dataType = inputParams.getString("datatype");
		}
		dataType = (!DATATYPE_week.equals(dataType) && !DATATYPE_month.equals(dataType) ? DATATYPE_day : dataType);
		
		Recordset rs = this.getData(db, fcal, tcal, dataType, zone, COMPAREFLAG_other, inputParams);
		publish("query-pos.sql", rs);

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
				tRs = this.getData(db, tfcal, ttcal, dataType, zone, compareflag, inputParams);
			}
			/** 环比 */
			if( compareflag.contains(COMPAREFLAG_huanbi) ){
				Calendar tfcal = Calendar.getInstance(zone);
				tfcal.setTime(fdate);
				tfcal.add(Calendar.MONTH, -1);
				Calendar ttcal = Calendar.getInstance(zone);
				ttcal.setTime(tdate);
				ttcal.add(Calendar.YEAR, 0);
				hRs = this.getData(db, tfcal, ttcal, dataType, zone, compareflag, inputParams);
			}
		}
		tRs = null != tRs ? tRs : this.createResultData();
		hRs = null != hRs ? hRs : this.createResultData();
		publish("query-pos-tb.sql", tRs);
		publish("query-pos-hb.sql", hRs);
		
		return rc;
	}
	
	private Recordset createResultData() throws RecordsetException{
		Recordset rs = new Recordset();
		rs.append("createdate", Types.VARCHAR);
		rs.append("cust_num", Types.INTEGER);	// 成交总人数
		rs.append("p1_cust_num", Types.INTEGER);	// P1成交人数
		rs.append("p2_cust_num", Types.INTEGER);	// P2成交人数
		rs.append("p1_num", Types.INTEGER);	// P1人数
		rs.append("p2_num", Types.INTEGER);	// P2人数
		rs.append("exper_num", Types.INTEGER);	// 体验课总人数
		return rs;
	}
	
	private Recordset getData(Db db, Calendar fcal, Calendar tcal, String dataType, TimeZone zone, String compareflag, Recordset inputParams) throws Throwable{
		Calendar cal = Calendar.getInstance(zone);
		
		Recordset rs = this.createResultData();
		
		String fdate = sdfDate.format(fcal.getTime());
		String tdate = sdfDate.format(tcal.getTime());
		// 成交总人数
		String queryPos = getResource("query-cust.sql");
		queryPos = StringUtil.replace(queryPos, "${fld:fdate}", "'"+fdate+"'::date");
		queryPos = StringUtil.replace(queryPos, "${fld:tdate}", "'"+tdate+"'::date");
		queryPos = getSQL(queryPos, inputParams);
		Recordset rsPos = db.get(queryPos);
		// P1人数
		String queryP1 = getResource("query-p1.sql");
		queryP1 = StringUtil.replace(queryP1, "${fld:fdate}", "'"+fdate+"'::date");
		queryP1 = StringUtil.replace(queryP1, "${fld:tdate}", "'"+tdate+"'::date");
		queryP1 = getSQL(queryP1, inputParams);
		Recordset rsP1 = db.get(queryP1);
		// P1成交人数
		String queryP1Cust = getResource("query-p1-cust.sql");
		queryP1Cust = StringUtil.replace(queryP1Cust, "${fld:fdate}", "'"+fdate+"'::date");
		queryP1Cust = StringUtil.replace(queryP1Cust, "${fld:tdate}", "'"+tdate+"'::date");
		queryP1Cust = getSQL(queryP1Cust, inputParams);
		Recordset rsP1Cust = db.get(queryP1Cust);
		// P2人数
		String queryP2 = getResource("query-p2.sql");
		queryP2 = StringUtil.replace(queryP2, "${fld:fdate}", "'"+fdate+"'::date");
		queryP2 = StringUtil.replace(queryP2, "${fld:tdate}", "'"+tdate+"'::date");
		queryP2 = getSQL(queryP2, inputParams);
		Recordset rsP2 = db.get(queryP2);
		// P2成交人数
		String queryP2Cust = getResource("query-p2-cust.sql");
		queryP2Cust = StringUtil.replace(queryP2Cust, "${fld:fdate}", "'"+fdate+"'::date");
		queryP2Cust = StringUtil.replace(queryP2Cust, "${fld:tdate}", "'"+tdate+"'::date");
		queryP2Cust = getSQL(queryP2Cust, inputParams);
		Recordset rsP2Cust = db.get(queryP2Cust);
		// 体验课总人数
		String queryExper = getResource("query-exper.sql");
		queryExper = StringUtil.replace(queryExper, "${fld:fdate}", "'"+fdate+"'::date");
		queryExper = StringUtil.replace(queryExper, "${fld:tdate}", "'"+tdate+"'::date");
		queryExper = getSQL(queryExper, inputParams);
		Recordset rsExper = db.get(queryExper);

		Calendar thisCal = fcal;
		while( thisCal.getTimeInMillis() <= tcal.getTimeInMillis() ){
			Calendar startCal = Calendar.getInstance(zone);
			startCal.setTime(thisCal.getTime());

			rs.addNew();
			Date thisDate = startCal.getTime();
			Date createdate = null;
			if( compareflag.contains(COMPAREFLAG_tongbi) ){	/** 同比 */
				Calendar compareCal = Calendar.getInstance(zone);
				compareCal.setTime(thisDate);
				compareCal.add(Calendar.YEAR, 1);
				createdate = compareCal.getTime();
			}else if( compareflag.contains(COMPAREFLAG_huanbi) ){	/** 环比 */
				Calendar compareCal = Calendar.getInstance(zone);
				compareCal.setTime(thisDate);
				compareCal.add(Calendar.MONTH, 1);
				createdate = compareCal.getTime();
			}else{
				createdate = thisDate;
			}
			
			if( DATATYPE_week.equals(dataType) ){	// 按周
				rs.setValue("createdate", ErpTools.syncFormatDate(sdfDate, createdate));
				thisCal.add(Calendar.DATE, 6);
			}else if( DATATYPE_month.equals(dataType) ){	// 按月
				rs.setValue("createdate", ErpTools.syncFormatDate(sdfMonth, createdate));
				thisCal.add(Calendar.MONTH, 1);
			}else{	// 按天
				rs.setValue("createdate", ErpTools.syncFormatDate(sdfDate, createdate));
				thisCal.add(Calendar.DATE, 1);
			}
			
			int custNum = 0, experNum = 0, p1CustNum = 0, p1Num = 0, p2CustNum = 0, p2Num = 0;
			// 总成交人
			custNum = this.calNum(rsPos, cal, startCal, thisCal, dataType);
			// P1
			p1Num = this.calNum(rsP1, cal, startCal, thisCal, dataType);
			// P1成交人数
			p1CustNum = this.calNum(rsP1Cust, cal, startCal, thisCal, dataType);
			// P2
			p2Num = this.calNum(rsP2, cal, startCal, thisCal, dataType);
			// P2成交人数
			p2CustNum = this.calNum(rsP2Cust, cal, startCal, thisCal, dataType);
			// 体验课人数
			experNum = this.calNum(rsExper, cal, startCal, thisCal, dataType);

			rs.setValue("cust_num", custNum);
			rs.setValue("exper_num", experNum);
			rs.setValue("p1_cust_num", p1CustNum);
			rs.setValue("p1_num", p1Num);
			rs.setValue("p2_cust_num", p2CustNum);
			rs.setValue("p2_num", p2Num);
		}
		return rs;
	}
	
	
	/***
	 * 按日期计算数量
	 * @param rs
	 * @param cal
	 * @param startCal
	 * @param endCal
	 * @param dataType
	 * @return
	 * @throws Throwable
	 */
	private int calNum(Recordset rs, Calendar cal, Calendar startCal, Calendar endCal, String dataType) throws Throwable{
		int num = 0;
		if( rs.getRecordCount() > 0 ){
			Date thisDate = startCal.getTime();
			Recordset rsTemp = rs.copy();
			rsTemp.sort("createdate");
			while( rsTemp.next() ){
				Date cdate = rsTemp.getDate("createdate");
				cal.setTime(cdate);
				long thisMillis = cal.getTimeInMillis();
				if( thisMillis == startCal.getTimeInMillis() && DATATYPE_day.equals(dataType) ){
					num++;
				}else if( DATATYPE_week.equals(dataType) ){
					if( thisMillis >= startCal.getTimeInMillis() && thisMillis <= endCal.getTimeInMillis() ){
						num++;
					}
				}else if( ErpTools.syncFormatDate(sdfMonth, thisDate).equals(ErpTools.syncFormatDate(sdfMonth, cdate)) && DATATYPE_month.equals(dataType) ){
					num++;
				}
			}
		}
		return num;
	}
}
