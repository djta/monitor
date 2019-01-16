package transactions.project.weixin.service.binding;


import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class WeixinBinding extends GenericTableManager{
	

	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		Db db=getDb();
		//判断新境的帐号是否存在
		String nodup = getResource("nodup.sql");
		Recordset rsNodup=db.get(getSQL(nodup, inputParams));
		if(rsNodup.getRecordCount()==0){//新增一个帐号

			String insert_user = getSQL(getResource("insert-user.sql"), inputParams);
			String insert_t_staff = getSQL(getResource("insert_t_staff.sql"), inputParams);
			String insert_passlog = getSQL(getResource("insert-passlog.sql"), inputParams);
			String insert_account = getSQL(getResource("insert_account.sql"), inputParams);
			
			db.exec(insert_user);
			db.exec(insert_t_staff);
			db.exec(insert_passlog);
			db.exec(insert_account);

			String query_skill = getSQL(getResource("query-default-skill.sql"), inputParams);
			Recordset rsQuerySkill = db.get(query_skill);
			rsQuerySkill.first();
			Integer aSkill_id[] = new Integer[rsQuerySkill.getRecordCount()];
			
			for(int i=0;i<rsQuerySkill.getRecordCount();i++){
				aSkill_id[i]=rsQuerySkill.getInteger("skill_id");
				rsQuerySkill.next();
			}
				//新境技能
				String colNames = "skill_id;skill_id";
				String sqlFiles = "insert-roles.sql;insert-skills.sql";

				if(colNames != null && colNames.length() > 0 && sqlFiles != null && sqlFiles.length() > 0) {
		    		String[] colNamesA = colNames.split(";");   //多个语句体用分号(;)分隔       "xxx1,yyy1;xxx2,yyy2"
		    		String[] sqlFilesA = sqlFiles.split(";");   //SQL语句
		            
		    		if(colNamesA.length != sqlFilesA.length){
		    			throw new Throwable("字段与语句数量不对应，请检查！");
		    		}
		    		for (int i=0;i<colNamesA.length;i++)
		    		{
		                String sqlFile = sqlFilesA[i];
		        		String sql = getSQL(getResource(sqlFile), inputParams);
		        		save(sql, colNamesA[i],aSkill_id);
		    		}
		    	}


				//删除原有的绑定
				String delete_weixin = getSQL(getResource("delete_weixin.sql"), inputParams);
				//绑定微信号
				String binding_weixin = getSQL(getResource("insert_weixin.sql"), inputParams);
				db.exec(delete_weixin);
				db.exec(binding_weixin);
				
				rc=1;

		}else{ //更新信息
			rsNodup.first();
			//如果密码和用户相同，则订为本人想更新微信的绑定关系
			if(inputParams.getString("passwd").equals(rsNodup.getString("passwd")) && inputParams.getString("userlogin").equals(rsNodup.getString("userlogin"))){
				//删除原有的绑定
				String delete_weixin = getSQL(getResource("delete_weixin.sql"), inputParams);
				//绑定微信号
				String binding_weixin = getSQL(getResource("insert_weixin.sql"), inputParams);
				db.exec(delete_weixin);
				db.exec(binding_weixin);
				
				/*String query_sid = getSQL(getResource("query-weixin-sid.sql"), inputParams);
				Recordset rsQuerySid = db.get(query_sid);
				if(rsQuerySid.getRecordCount()>0){
					rsQuerySid.first();
					String service_tuid = rsQuerySid.getString("service_tuid");
					String message = "更改微信绑定成功!";
					String to_user = inputParams.getString("weixin_userid");
					send_msg(service_tuid,to_user,message);
				}*/
				rc=3;
			}else{
				/*String query_sid = getSQL(getResource("query-weixin-sid.sql"), inputParams);
				Recordset rsQuerySid = db.get(query_sid);
				if(rsQuerySid.getRecordCount()>0){
					rsQuerySid.first();
					String service_tuid = rsQuerySid.getString("service_tuid");
					String message = "帐号不存在，或者密码不正确!";
					String to_user = inputParams.getString("weixin_userid");
					send_msg(service_tuid,to_user,message);
				}*/
				rc=4;
			}
		}

		return rc;
	}

	/*public void send_msg(String service_tuid, String to_user,String message) throws Throwable
	{
		Db db = getDb();
		try {
			JSONObject jsonObject = null;
			String accessToken = WeixinUtil.getAccessTokenForService(db,
					service_tuid);
			jsonObject = WeixinUtil.sendWeixinMessageTextForService(
					accessToken, to_user, message);

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	public void save(String sql, String colNames,Integer[] aSkill_id) throws Throwable
	{
		
		Db db = getDb();
		String[] colNameA = colNames.split(",");   //单语句体中,多个字段,用逗号(,)分隔  "xxx1,yyy1"
		Recordset detail = new Recordset();
		for (int i=0;i<colNameA.length;i++)
		{
            detail.append(colNameA[i], java.sql.Types.INTEGER);
		}

		if(aSkill_id.length>0){/*先判断值是否存在*/
            int recordCount = aSkill_id.length;
            for(int i=0;i<recordCount;i++){
    			detail.addNew();
    			for (int j=0;j<colNameA.length;j++){
    				Integer colValue[] = new Integer[aSkill_id.length];
    				for (int k = 0;k<aSkill_id.length;k++){
    					colValue[k]=aSkill_id[k];
    				}
            		if(colValue!=null && colValue[i] != null){
        				detail.setValue(colNameA[j], colValue[i]);
            		}else{
        				detail.setValue(colNameA[j], null);
            		}
    			}
    			String cmd = getSQL(sql, detail);
    			db.addBatchCommand(cmd);
            }
    		db.exec();
    	}
	}

}
