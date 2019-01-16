package com.ccms.util.calendar;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.ValidatorUtil;

/**
 * 日历
 */
public class GridCalendar extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {
		super.service(inputs);

		Db db = getDb();
		String query = getSQL(getResource("query.sql"), inputs);

		String nowDate = getRequest().getParameter("date");// 保存目前显示的日期
		String systemDate = "";// 显示系统的日期
		String operatorFlag = getRequest().getParameter("operatorFlag");// 供判断上一月与下一月的操作

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// set date
		Date calDate = ValidatorUtil.testDate(nowDate, "yyyy-MM-dd");
		if (calDate == null)
			calDate = new Date();
		Calendar mDate = Calendar.getInstance();
		mDate.setTime(calDate);
		if ("privious".equals(operatorFlag)) {// 判断页面点击了哪个按钮
			mDate.add(Calendar.MONTH, -1);// 减一个月，变为上月
		} else if ("next".equals(operatorFlag)) {
			mDate.add(Calendar.MONTH, 1);// 加一个月，变为下月
		}
		calDate = mDate.getTime();

		nowDate = sdf.format(calDate);// 格式化当前日期
		Date sysDate = new Date();
		systemDate = sdf.format(sysDate);// 格式化系统日期

		Calendar c = Calendar.getInstance();
		c.setLenient(true);
		c.setTime(calDate);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		// end patch

		// recordset with selected year and month
		Recordset rsDate = new Recordset();
		rsDate.append("year", Types.INTEGER);
		rsDate.append("month", Types.INTEGER);
		rsDate.addNew();
		rsDate.setValue("year", new Integer(c.get(Calendar.YEAR)));
		rsDate.setValue("month", new Integer(c.get(Calendar.MONTH) + 1));

		// 根据当前月来查询内容
		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM");
		query = StringUtil.replace(query, "${now}", sdfNow.format(calDate));
		Recordset queryResult = db.get(query);

		// set first day of month
		c.set(Calendar.DATE, 1);

		// define recordset
		Recordset rs = createCalendarRecordset();

		// fill empty slots until 1st day of month
		for (int i = Calendar.SUNDAY; i < c.get(Calendar.DAY_OF_WEEK); i++) {
			rs.addNew();
			rs.setValue("onclick", "");
			rs.setValue("html", null);
			rs.setValue("class", "calEmptyCell");
		}

		// feed recordset with selected month
		for (int i = 1; i <= c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
			c.set(Calendar.DATE, i);
			rs.addNew();
			rs.setValue("day", new Integer(i));
			rs.setValue("onclick", "");

			rs.setValue("html", this.getCellHTML(c, queryResult));
			rs.setValue("class", this.getCellStyle(c, queryResult));
		}

		// fill empty slots from last day of month until next saturday
		if (c.get(Calendar.DAY_OF_WEEK) < Calendar.SATURDAY) {
			for (int i = c.get(Calendar.DAY_OF_WEEK); i < Calendar.SATURDAY; i++) {
				rs.addNew();
				rs.setValue("onclick", "");
				rs.setValue("html", null);
				rs.setValue("class", "calEmptyCell");
			}
		}

		// publish recordset as a request attribute for HGrid output class
		getRequest().setAttribute("calendar", rs);
		getRequest().setAttribute("nowDate", nowDate);
		getRequest().setAttribute("systemDate", systemDate);
		publish("calconfig", rsDate);

		return 0;
	}

	Recordset createCalendarRecordset() throws Throwable {
		Recordset rs = new Recordset();
		rs.append("day", Types.INTEGER);
		rs.append("onclick", Types.VARCHAR);
		rs.append("html", Types.VARCHAR);
		rs.append("class", Types.VARCHAR);
		return rs;
	}

	protected String getCellHTML(Calendar c, Recordset queryResult)
			throws Throwable {
		String rtnStr = "<div class=\"daytable\"><div class=\"daydiv fleft\">" + String.valueOf(c.get(Calendar.DATE))
				+ "</div></div><div style=\"height: 100px; overflow-x: hidden; overflow-y: auto;\" class=\"dayevent\">";
		queryResult.top();
		while (queryResult.next()) {
			int day = queryResult.getInt("day");
			if (day == c.get(Calendar.DATE)) {
				String is_can_evaluate = queryResult.getString("is_evaluate");// 对应的操作
				rtnStr += is_can_evaluate;
			}
		}
		rtnStr += "</div>";
		return rtnStr;
	}

	protected String getCellStyle(Calendar c, Recordset queryResult) throws Throwable {
		String styleName = "calDay";
		boolean flag = false;
		queryResult.top();
		while (queryResult.next()) {
			int day = queryResult.getInt("day");
			if (c.get(Calendar.DATE) == day) {
				if( queryResult.containsField("daily_style") && StringUtils.isNotBlank(queryResult.getString("daily_style")) ){
					styleName = queryResult.getString("daily_style");
				}else{
					flag = true;
				}
				break;
			}
		}
		if (flag == true)
			styleName = "calCurDay";

		return styleName;
	}

}
