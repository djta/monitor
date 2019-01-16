package transactions.project.fitness.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.xml.Element;

/**
 * EPR
 * @author C
 * 2016-07-26
 */
public class EPRReport extends GenericTableManager {

	@SuppressWarnings("deprecation")
	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		String _month = (inputParams.containsField("month") && !inputParams.isNull("month") ? inputParams.getString("month") : null);
		if( !StringUtils.isNotBlank(_month) ){
			throw new Throwable("所选月份不能为空");
		}
		Db db = getDb();
		// 获取列头
		String queryFields = getResource("query-fields.sql");
		Recordset rs = db.get(queryFields);
		
		// 获取数据
		Map<Integer, Recordset> dataMap = new HashMap<Integer, Recordset>();
		Element[] qlist = getConfig().getDocument().getElements("query");
		if ( null != qlist ){
			for (int i = 0; i < qlist.length; i++) {
				if( null == qlist[i] || "".equals(qlist[i]) ){
					continue;
				}
				String queryName = qlist[i].getString();
				String query = getResource(queryName);
				Recordset rsdata = db.get(getSQL(query, inputParams));
				dataMap.put(i, rsdata);
			}
		}
		
		// 获取月份
		TimeZone zone = TimeZone.getTimeZone("GMT+8");
		Calendar cal = Calendar.getInstance(zone);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Date fdate = sdf.parse(_month + "-01 00:00:00");	// 起始日期
		cal.setTime(fdate);
		cal.add(Calendar.MARCH, 1);
		Date maxdate = cal.getTime();	// 结束日期
		
		// 按日期拼接数据
		cal.setTime(fdate);
		while( true ){
			Date tdate = cal.getTime();
			if( tdate.getTime() >= maxdate.getTime() ){
				break;
			}
			rs.addNew();
			rs.setValue("c_idate", tdate);
			rs.setValue("day", tdate.getDate());
			
			// 遍历Map中的值
			for (Recordset rsdata : dataMap.values()) {
				// 获取列头
				HashMap<String, RecordsetField> dataFieldList = rsdata.getFields();
				for(RecordsetField rsFields : dataFieldList.values() ){
					String fieldName = rsFields.getName();
					if( !rs.containsField(fieldName) ){
						continue;
					}
					int idx = rsdata.findRecord("c_idate", tdate);
					if( idx < 0 ){
						continue;
					}
					rsdata.setRecordNumber(idx);
					rs.setValue(fieldName, rsdata.getValue(fieldName));
				}
			}
			cal.add(Calendar.DATE, 1);
		}
		publish("_rs", rs);
		return rc;
	}
	
}
