package transactions.project.fitness.weixin;

import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 会员入场/退场（第三方设备扫码）
 * @author C
 * 2018-02-05
 */
public class InleftQrcode extends GenericTransaction{
		
	public int service(Recordset inputs) throws Throwable{
		int rc = super.service(inputs);
		int resultcode = 0;	// 0未处理 1成功 2失败
		String resultmsg = "";
		Db db = getDb();
		
		try {
			String queryData = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/query-data-qrcode.sql");
			queryData = getSQL(queryData, inputs);
			Recordset rsData = db.get(queryData);
			if( null != rsData && rsData.getRecordCount() > 0 ){
				rsData.first();
				String updateQrcode = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/update-qrcode.sql");
				updateQrcode = getSQL(updateQrcode, rsData);
				
				// 判断当前是入场还是退场
				String queryStatus = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/query-status.sql");
				queryStatus = getSQL(queryStatus, rsData);
				Recordset rsStatus = db.get(queryStatus);
				if( null != rsStatus && rsStatus.getRecordCount() > 0 ){	// 退场
					rsStatus.first();
					// 执行退场逻辑
					String insertMessage = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tleft/insert-message.sql");
					String updateInleft = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tleft/update-inleft.sql");
					
					insertMessage = getSQL(insertMessage, rsData);
					db.addBatchCommand(insertMessage);
					updateInleft = getSQL(updateInleft, rsStatus);
					db.addBatchCommand(updateInleft);
					db.exec();
					resultcode = 1;
					this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
				}else{	// 入场
					// 判断是否可入场
					Integer status = rsData.getInteger("status");
					Integer type = rsData.getInteger("type");
					String queryHoliday = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tin/nodup-holiday.sql");
					queryHoliday = getSQL(queryHoliday, rsData);
					Recordset rsHoliday = db.get(queryHoliday);
					if( null != rsHoliday && rsHoliday.getRecordCount() > 0 ){	// 俱乐部放假
						resultcode = 2;
						resultmsg = "俱乐部放假期间不允许入场";
						this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
					}else if( null != status && 3 == status.intValue() ){	// 存卡中
						resultcode = 2;
						resultmsg = "该卡处于存卡中";
						this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
					}else if( null != status && 3 == status.intValue() ){	// 挂失中
						resultcode = 2;
						resultmsg = "该卡处于挂失中";
						this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
					}else if( null != status && 3 == status.intValue() ){	// 停卡中
						resultcode = 2;
						resultmsg = "该卡处于停卡中";
						this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
					}else if( null != type && 1 == type.intValue() ){	// 计次卡
						Integer nowcount = ( !rsData.isNull("nowcount") && null != rsData.getValue("nowcount") ? rsData.getInteger("nowcount") : 0 );
						if( 0 <= nowcount.intValue() ){
							resultcode = 2;
							resultmsg = "该次卡剩余次数为0";
							this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
						}
					}else if( null != type && 2 == type.intValue() ){	// 基金卡
						double moneycash = ( !rsData.isNull("moneycash") && null != rsData.getValue("moneycash") ? rsData.getDouble("moneycash") : 0d );
						double moneygift = ( !rsData.isNull("moneygift") && null != rsData.getValue("moneygift") ? rsData.getDouble("moneygift") : 0d );
						if( moneycash <= 0d && moneygift <= 0d ){
							resultcode = 2;
							resultmsg = "该基金卡剩余金额不足";
							this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
						}
					}else{
						// 执行入场逻辑
						String insertMessage = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tin/insert-message.sql");
						String insertInleft = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tin/insert-inleft.sql");
						String updateCardCount = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tin/update-cardnowcount.sql");
						String updateCardDate = getLocalResource("/transactions/project/fitness/weixin/sql/inleft/tin/update-cardstartenddate.sql");
						
						insertMessage = getSQL(insertMessage, rsData);
						db.addBatchCommand(insertMessage);
						insertInleft = getSQL(insertInleft, rsData);
						db.addBatchCommand(insertInleft);
						updateCardCount = getSQL(updateCardCount, rsData);
						db.addBatchCommand(updateCardCount);
						updateCardDate = getSQL(updateCardDate, rsData);
						db.addBatchCommand(updateCardDate);
						db.exec();
						this.updateQrcodeResult(db, updateQrcode, resultcode, resultmsg);	// 更新处理结果
						resultcode = 1;
					}
				}
			}else{
				resultcode = 2;
				resultmsg = "该卡号无效或已过期";
			}
		} catch(Throwable t) {
			resultcode = 2;
			resultmsg = "入场刷卡异常";
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
