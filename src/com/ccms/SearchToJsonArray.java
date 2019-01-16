package com.ccms;

import java.io.StringWriter;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import dinamica.*;

public class SearchToJsonArray extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);

		String sqlFile = getConfig()
				.getConfigValue("query-sql", "query.sql");
		String[] arraySql = getConfig().getConfigValue("operator", "").split(
				";");
		String[] column = getConfig().getConfigValue("colname", "").split(
				";");

		String qBase = getSQL(getResource(sqlFile), inputs);

		Db db = getDb();
		Recordset rsRecordset = db.get(qBase);
		List<HashMap<String, Object>> parentLis = recordToMap(rsRecordset);
		for (int i = 0; i < parentLis.size(); i++) {
			HashMap<String, Object> ssHashMap = parentLis.get(i);
			for (int j = 0; j < column.length; j++) {
				String replaceValue = String.valueOf(ssHashMap.get(column[j]));
				String childSql = StringUtil.replace(
						getSQL(getResource(arraySql[j]), inputs), "${fld:"
								+ column[j] + "}", replaceValue);
				Recordset childRecordset = db.get(childSql);
				ssHashMap.put(column[j] + "_list", recordToMap(childRecordset));
			}
		}
		Recordset pubshRecordset = new Recordset();
		pubshRecordset.append("json", Types.VARCHAR);
		pubshRecordset.addNew();
		pubshRecordset.setValue("json", writeObject(parentLis));
		publish(sqlFile, pubshRecordset);
		return rc;

	}

	public List<HashMap<String, Object>> recordToMap(Recordset rs)
			throws Throwable {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, RecordsetField> fieldsMap = rs.getFields();
		ArrayList<String> alField = new ArrayList<String>();

		Iterator iterator = fieldsMap.entrySet().iterator();
		while (iterator.hasNext()) {
			String ssString = iterator.next().toString();
			alField.add(ssString.substring(0, ssString.indexOf("=")));
		}
		rs.top();
		while (rs.next()) {
			HashMap<String, Object> hs = new HashMap<String, Object>();
			for (int i = 0; i < alField.size(); i++) {
				String keyString = alField.get(i);
				String valueString = rs.getValue(keyString) == null ? ""
						: String.valueOf(rs.getValue(keyString));
				hs.put(keyString, valueString);
			}
			list.add(hs);
		}

		return list;
	}

	public static String writeObject(Object obj) throws Throwable {
		ObjectMapper mapper = new ObjectMapper();

		StringWriter writer = new StringWriter();
		String re = null;
		JsonGenerator json = new JsonFactory().createGenerator(writer);
		mapper.writeValue(json, obj);
		re = writer.toString();
		json.close();
		writer.close();

		return re;
	}
}
