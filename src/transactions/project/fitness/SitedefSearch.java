package transactions.project.fitness;

import java.sql.Connection;
import java.sql.Types;

import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 
 * @author Administrator
 *
 */
public class SitedefSearch extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		try {
			Db db = getDb();
			String basepath = "/transactions/project/fitness/sitedef/";

			//查询场地设定最小时间和最大时间
			String queryhour = getLocalResource(basepath + "query-hour.sql");
			queryhour = getSQL(queryhour, null);
			queryhour = getSQL(queryhour, inputParams);
			Recordset rshour = db.get(queryhour);
			String sitedefhour = "<td width='130' nowrap></th>";
			int minhour = 0, maxhour = 0;
			while(rshour.next()){
				minhour = rshour.getInt("minhour");
				maxhour = rshour.getInt("maxhour");
				//minhour等于0并且maxhour等于0说明该俱乐部下没有场地
				if(minhour>0&&maxhour>0){
					for(int i=minhour;i<=maxhour;i++){
						sitedefhour+="<td width='100' nowrap>"+i+":00</td>";
					}
				}else{
					sitedefhour="";
				}
			}
			
			//查询场地
			String querysitedef = getLocalResource(basepath + "query-sitedef.sql");
			querysitedef = getSQL(querysitedef, null);
			querysitedef = getSQL(querysitedef, inputParams);
			Recordset rssitedef = db.get(querysitedef);
			String titlestr = "<li>场地类型</li>", sitedeflist = "";
			//循环场地
			while(rssitedef.next()){
				String code = rssitedef.getString("code");
				titlestr+="<li>"+rssitedef.getString("sitename")+"</li>";
				sitedeflist +="<tr class='yysitetr' code='"+code+"'><th></th>";
				for(int j=minhour;j<=maxhour;j++){
					String color = "gzb", pricesstr = "",choose_way="", str = "", price = "", szstr = "", site_timelimitcode = "";
					//根据场地 时间等查询该场地状态
					String querySite=getLocalResource(basepath+"query-sitedetail.sql");
					querySite = getSQL(querySite, null);
					querySite = getSQL(querySite, inputParams);
					querySite = StringUtil.replace(querySite, "${fld:sitecode}", "'"+code+"'");
					querySite = StringUtil.replace(querySite, "${fld:limittime}", "'"+j+"'");
					querySite = StringUtil.replace(querySite, "${fld:times}", "'"+j+":00:00'");
					Recordset rcSite = db.get(querySite);
					rcSite.next();
					
					choose_way = rcSite.getString("choose_way");
					String block_maxnum = rssitedef.getString("block_maxnum");
					String block_price = rssitedef.getString("block_price");
					String sitename = rssitedef.getString("sitename");
					String group_price = rssitedef.getString("group_price");
					String prepare_date = inputParams.getString("prepare_date");
					String group_minnum = rssitedef.getString("group_minnum");
					String group_maxnum = rssitedef.getString("group_maxnum");
					//场地时段定义表code
					site_timelimitcode = rcSite.getString("site_timelimitcode");
					if(choose_way==null){
						choose_way="0";
					}
					if(choose_way.equals("1")){
						//包场
						color = "canyb";
						pricesstr = block_maxnum+"人 "+block_price+"元";
						str="（包）"+sitename+"-"+prepare_date+"-"+j+":00";
						price=block_price;
					}else if(choose_way.equals("2")){
						//拼场
						color = "yellowb";
						pricesstr = group_minnum+"/"+group_maxnum+"人 "+group_price+"元";
						str="（拼）"+sitename+"-"+prepare_date+"-"+j+":00";
						price=group_price;
					}
					//查询是否已预约
					if(choose_way.length()>0){
						int sdnum = rcSite.getInt("sdnum");
						if(choose_way.equals("1")&&sdnum>0){
							color="greyb";
							szstr="1";
						}else if(choose_way.equals("2")&&sdnum>=Integer.parseInt(group_maxnum)){
							color="zongseb";
							szstr="1";
						}
					}
					
					String querydatetime = getLocalResource(basepath + "query-date.sql");
					String strtime = "";
					if(j<10){
						strtime = "0"+j;
					}else{
						strtime = j+"";
					}
					querydatetime = getSQL(querydatetime, null);
					querydatetime = StringUtil.replace(querydatetime, "${fld:datetime}", 
							"'"+inputParams.getString("prepare_date")+" "+strtime+":00:00'");
					Recordset rcdatetime = db.get(querydatetime);
					rcdatetime.next();
					System.out.println(inputParams.getString("prepare_date")+" "+strtime+":00:00"+"-------"+rcdatetime.getString("status"));
					
					if(rcdatetime.getString("status").equals("1")&&color!="greyb"){
						sitedeflist+="<td><div>";
					}else{
						sitedeflist+="<td class='"+color+"' code='"+code+"'><div class='prices'>"+pricesstr+"</div>"
								+"<span style='display:none;' class='yi_bj'></span><div class='tc_yy'>";
						//已预约不显示CheckBox和设置
						if(!szstr.equals("1")){
							sitedeflist+="<input type='checkbox' name='sitedecode' value='"+j+"' code='"+code+"' codestr='"+str+"'"
				            +" codestatus='"+choose_way+"' codeprice='"+price+"' class='kkk' style='margin-top:-3px;'>"
				            +"<span class='sheding' site_timelimitcode='"+site_timelimitcode+"'></span>";
						}else{
							sitedeflist+="<span class='huanchang' code='"+code+"' time='"+j+"'></span>";
						}
					}
					sitedeflist+="</div></td>";
				}
				sitedeflist+="</tr>";
			}
			
			
			Recordset rsQrcode = new Recordset();
			rsQrcode.append("sitedefhour", Types.VARCHAR);
			rsQrcode.append("titlestr", Types.VARCHAR);
			rsQrcode.append("sitedeflist", Types.VARCHAR);
			rsQrcode.append("minhour", Types.VARCHAR);
			rsQrcode.append("maxhour", Types.VARCHAR);
			rsQrcode.addNew();
			rsQrcode.setValue("sitedefhour", sitedefhour);
			rsQrcode.setValue("titlestr", titlestr);
			rsQrcode.setValue("sitedeflist", sitedeflist);
			rsQrcode.setValue("minhour", minhour);
			rsQrcode.setValue("maxhour", maxhour);
			publish("_rsQrcode", rsQrcode);
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			/*Recordset rsQrcode = new Recordset();
			rsQrcode.append("qrcode_id", Types.VARCHAR);
			rsQrcode.append("qrcode_path", Types.VARCHAR);
			rsQrcode.addNew();
			rsQrcode.setValue("qrcode_id", String.valueOf(tuid));
			rsQrcode.setValue("qrcode_path", qrcodePath);
			publish("_rsQrcode", rsQrcode);*/
		}
		return rc;
	}
}
