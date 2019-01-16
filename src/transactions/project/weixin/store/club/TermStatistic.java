package transactions.project.weixin.store.club;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class TermStatistic extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable
	{
		int rc = super.service(inputs);
		
		String sqlData = getSQL(getResource("query-busi-data.sql"), inputs);
		String sqlItem = getSQL(getResource("query-item.sql"), inputs);
		String sqlList = getSQL(getResource("query-list-score.sql"), inputs);
		
		Db db = getDb();

		Recordset rsFieldTitle = getRecordset("query-field-title.sql");

		Recordset rsData = db.get(sqlData);
		//查询成绩
		Recordset rsList = db.get(sqlList);
		if(rsList.getRecordCount() > 0){
			
			HashMap<String,HashMap<String,Integer>> itemMap = new HashMap<String,HashMap<String,Integer>>();
			HashMap<String,String> itemOutputMap = new HashMap<String,String>();
			while(rsList.next()){
				String key = rsList.getString("userlogin")+"_"+rsList.getString("item_id");
				if(itemMap.containsKey(key)){
					HashMap<String,Integer> itemListCountMap = itemMap.get(key);
					if(itemListCountMap.containsKey(rsList.getString("term_list_id"))){
						itemListCountMap.put(rsList.getString("term_list_id"),itemListCountMap.get(rsList.getString("term_list_id"))+1);
					}else{
						itemListCountMap.put(rsList.getString("term_list_id"),1);
					}
					itemMap.put(key, itemListCountMap);
				}else{
					HashMap<String,Integer> itemListCountMap = new HashMap<String,Integer>();
					itemListCountMap.put(rsList.getString("term_list_id"), 1);
					itemMap.put(key, itemListCountMap);
				}
			}
			Iterator<Entry<String,HashMap<String,Integer>>> it = itemMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,HashMap<String,Integer>> en = it.next();
				String user_item_id = en.getKey();
				HashMap<String,Integer> listCountMap = en.getValue();
				String item_s = "";
				Iterator<Entry<String,Integer>> it_c = listCountMap.entrySet().iterator();
				while(it_c.hasNext()){
					Entry<String,Integer> en_c = it_c.next();
					String term_list_id = en_c.getKey();
					String list_name = "";
					rsList.top();
					while(rsList.next()){
						if(term_list_id.equalsIgnoreCase(rsList.getString("term_list_id"))){
							list_name = rsList.getString("list_name");
							break;
						}
					}
					item_s += (list_name + "(" + en_c.getValue() + ");");
				}
				itemOutputMap.put(user_item_id, item_s);
			}
			
			//获取题目
			Recordset rsItem = db.get(sqlItem);
			
			//存储最终数据
			Recordset termData = new Recordset();
			int dataColCount = rsFieldTitle.getRecordCount()+rsItem.getRecordCount();
			for(int i=0;i<dataColCount;i++){
				termData.append("data"+i, Types.VARCHAR);
			}
			termData.addNew();
			
			//添加列表头部
			int j=0;
			rsFieldTitle.top();
			while (rsFieldTitle.next()) 
			{
				termData.setValue("data"+j, rsFieldTitle.getString("title"));
				j++;
			}
			
			while(rsItem.next()){
				termData.setValue("data"+j, rsItem.getString("item_code") );
				j++;
			}
			
			//根据人员逐行录入
			rsData.top();
			while(rsData.next()){
				String pk_value = rsData.getString("userlogin");
				termData.addNew();
				j = 0;
				//再次初始化迭代数据
				rsFieldTitle.top();
				while (rsFieldTitle.next()){
						Object rsValue = rsData.getValue(rsFieldTitle.getString("code"));
						termData.setValue("data"+j, rsValue==null?"":rsValue);
						j++;
				}
				rsItem.top();
				while(rsItem.next()){
					String key = pk_value + "_" + rsItem.getString("item_id");
					String mapValue = itemOutputMap.get(key);
					termData.setValue("data"+j, mapValue==null?"":mapValue);
					j++;
				}
			}
			getSession().setAttribute("query.sql", termData);
			rc = 0;
		}else{
			//获取题目
			Recordset rsItem = db.get(sqlItem);
			//存储最终数据
			Recordset termData = new Recordset();
			int dataColCount = rsFieldTitle.getRecordCount()+rsItem.getRecordCount();
			for(int i=0;i<dataColCount;i++){
				termData.append("data"+i, Types.VARCHAR);
			}
			termData.addNew();
			
			//添加列表头部
			int j=0;
			rsFieldTitle.top();
			while (rsFieldTitle.next()) 
			{
				termData.setValue("data"+j, rsFieldTitle.getString("title"));
				j++;
			}
			
			while(rsItem.next()){
				termData.setValue("data"+j, rsItem.getString("item_code") );
				j++;
			}
			getSession().setAttribute("query.sql", termData);
			rc = 0;
		}
		return rc;
	}
}
