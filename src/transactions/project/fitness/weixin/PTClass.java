package transactions.project.fitness.weixin;

import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 私教刷课
 * @author C
 * 2018-02-07
 */
public class PTClass extends GenericTransaction{
	
	public int service(Recordset inputs) throws Throwable{
		int rc = super.service(inputs);
		int resultcode = 0;	// 0未处理 1成功 2失败
		String resultmsg = "";
		try {
			Db db = getDb();
			
			String queryData = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/query-data.sql");
			queryData = getSQL(queryData, inputs);
			Recordset rsData = db.get(queryData);
			if( null != rsData && rsData.getRecordCount() > 0 ){
				rsData.first();
				String updateQrcode = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/update-qrcode.sql");
				updateQrcode = getSQL(updateQrcode, rsData);
	
				// 判断是否有可以签课
				String queryHoliday = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/nodup-holiday.sql");
				queryHoliday = getSQL(queryHoliday, rsData);
				Recordset rsHoliday = db.get(queryHoliday);
				if( null != rsHoliday && rsHoliday.getRecordCount() > 0 ){	// 俱乐部放假
					resultcode = 2;
					resultmsg = "俱乐部放假期间不允许签课";
					this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
				}else if( rsData.isNull("preparecode") || null == rsData.getString("preparecode") || "".equals(rsData.getString("preparecode")) ){
					resultcode = 2;
					resultmsg = "请先预约后再签课";
					this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
				}else{
					// 执行签课逻辑
					String insertPtlog = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/insert-ptlog.sql");
					String updatePtrest = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/update-ptrest.sql");
					String updatePtprepare = getLocalResource("/transactions/project/fitness/weixin/sql/ptclass/update-ptprepare.sql");
					
					updatePtprepare = getSQL(updatePtprepare, rsData);
					db.addBatchCommand(updatePtprepare);
					updatePtrest = getSQL(updatePtrest, rsData);
					db.addBatchCommand(updatePtrest);
					insertPtlog = getSQL(insertPtlog, rsData);
					db.addBatchCommand(insertPtlog);
					db.exec();
					this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
					resultcode = 1;
				}
			}else{
				resultcode = 2;
				resultmsg = "未找到该教练对应的私教课";
				return rc;
			}
		} catch(Throwable t) {
			resultcode = 2;
			resultmsg = "签课异常";
			t.printStackTrace();
		} finally {
			Recordset rsMsg = new Recordset();
			rsMsg.append("resultcode", Types.INTEGER);
			rsMsg.append("resultmsg", Types.VARCHAR);
			rsMsg.addNew();
			rsMsg.setValue("resultcode", resultcode);
			rsMsg.setValue("resultmsg", resultmsg);
			publish("rsMsg", rsMsg);
		}
		return rc;
	}
	
	private void updateQrcodeResult(Db db, String updateQrcode, int resultcode, String resultmsg) throws Throwable{
		updateQrcode = StringUtils.replace(updateQrcode, "${resultcode}", String.valueOf(resultcode));
		updateQrcode = StringUtils.replace(updateQrcode, "${resultmsg}", resultmsg);
		db.exec(updateQrcode);
	}
}
