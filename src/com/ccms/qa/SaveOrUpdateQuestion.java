package com.ccms.qa;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
/**
 * 
* @ClassName: SaveQuestion
* @Description: 保存部签的问题
* @author Oasahi
* @date 2014年7月26日 上午11:01:37
*
 */
public class SaveOrUpdateQuestion extends GenericTableManager
{

	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		super.service(inputParams);
		//判断标签是否超过5个
		String[] tags = inputParams.getString("tagstr").split(",");
		if (tags == null || tags.length == 0) {
			rc=1;//标签未设置
			return rc;
		}
		if (tags.length > 5) {
			rc=2;//标签个数太多
			return rc;
		}		
		String insert_tag=getResource("insert_tag.sql");
		String insert_post=getResource("insert_post.sql");
		String insert_question=getResource("insert_question.sql");
		String update_tag=getResource("update_tag.sql");
		String update_post=getResource("update_post.sql");
		String update_question=getResource("update_question.sql");
		String insert_tag_relationships=getResource("insert_tag_relationships.sql");
		String insert_tag_relationships2=getResource("insert_tag_relationships2.sql");
		String delete_tag_relationships=getResource("delete_tag_relationships.sql");
		String query_tag=getResource("query_tag.sql");
		String query_tag_id=getResource("query_tag_id.sql");
		Db db = getDb();
		//判断主键是否为空
		String tuid = inputParams.getString("tuid");
		if(tuid==null || "".equals(tuid)){
			String qtype=inputParams.getString("qtype");
			if(qtype==null || "".equals(qtype)){
				rc=3;//问答类型没设置
				return rc;
			}
			insert_post = getSQL(insert_post, inputParams);
			db.exec(insert_post);
			insert_question = getSQL(insert_question, inputParams);
			db.exec(insert_question);

			for (String tag : tags) {
				String qt=StringUtil.replace(query_tag,"${tag_name}", tag);
				qt=getSQL(qt, inputParams);
				Recordset rs_qt = getDb().get(qt);
				String it="";
				Integer tag_id;
				if(rs_qt.getRecordCount()==0){//新标签
					it=StringUtil.replace(insert_tag,"${tag_name}", tag);
					it=getSQL(it, rs_qt);
					it=getSQL(it, inputParams);
					db.exec(it);
					//取得当前标签ID
					String get_tag_id = getSQL(query_tag_id, inputParams);
					Recordset rs_curr_tag_id = db.get(get_tag_id);
					rs_curr_tag_id.first();
					tag_id = rs_curr_tag_id.getInteger("tag_id");
				}else{//旧标签
					rs_qt.first();
					if(rs_qt.getInt("delete_flag")==1){
						it=StringUtil.replace(update_tag,"${tag_name}", tag);
						it=getSQL(it, rs_qt);
						db.exec(it);
					}
					tag_id = rs_qt.getInteger("tag_id");
				}
				//关系表中插入
				String tag_relationships = getSQL(insert_tag_relationships,inputParams);
				tag_relationships = StringUtil.replace(tag_relationships,"${tag_id}", tag_id.toString());
				db.exec(tag_relationships);
			}

		}else{
			update_post = getSQL(update_post, inputParams);
			db.exec(update_post);
			update_question = getSQL(update_question, inputParams);
			db.exec(update_question);
			delete_tag_relationships = getSQL(delete_tag_relationships, inputParams);
			db.exec(delete_tag_relationships);
			
			for (String tag : tags) {
				String qt=StringUtil.replace(query_tag,"${tag_name}", tag);
				qt=getSQL(qt, inputParams);
				Recordset rs_qt = getDb().get(qt);
				String it="";
				Integer tag_id;
				if(rs_qt.getRecordCount()==0){//新标签
					it=StringUtil.replace(insert_tag,"${tag_name}", tag);
					it=getSQL(it, rs_qt);
					it=getSQL(it, inputParams);
					db.exec(it);
					//取得当前标签ID
					String get_tag_id = getSQL(query_tag_id, inputParams);
					Recordset rs_curr_tag_id = db.get(get_tag_id);
					rs_curr_tag_id.first();
					tag_id = rs_curr_tag_id.getInteger("tag_id");
				}else{//旧标签
					rs_qt.first();
					if(rs_qt.getInt("delete_flag")==1){
						it=StringUtil.replace(update_tag,"${tag_name}", tag);
						it=getSQL(it, rs_qt);
						db.exec(it);
					}
					tag_id = rs_qt.getInteger("tag_id");
				}
				//关系表中插入
				String tag_relationships2 = getSQL(insert_tag_relationships2,inputParams);
				tag_relationships2 = StringUtil.replace(tag_relationships2,"${tag_id}", tag_id.toString());
				db.exec(tag_relationships2);
			}
			

		}
		//如果为空,则新增问题
		//否则更新

		return rc;
	}
}