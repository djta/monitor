package com.ccms.dinamica;

import java.sql.Types;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class GetMenu extends GenericTransaction
{

	public int service(Recordset inputParams) throws Throwable
	{
		int rc = super.service(inputParams);
		
		Db db = getDb();
		Recordset rsMenu = new Recordset();
		rsMenu.append("id", Types.VARCHAR);
		rsMenu.append("pid", Types.VARCHAR);
		rsMenu.append("title", Types.VARCHAR);
		rsMenu.append("item_type", Types.VARCHAR);
		rsMenu.append("path", Types.VARCHAR);
		rsMenu.append("service_id", Types.INTEGER);
		
		String menu_id = inputParams.getString("menu_id");
		
		String queryMenu = getSQL(getResource("query-menu_item.sql"),inputParams);
		String queryItem = getSQL(getResource("query-child_menu.sql"),inputParams);
		
		//查询第一层
		setChildren(rsMenu, db, menu_id, queryMenu, queryItem);
		
		publish("query.sql", rsMenu);
		return rc;
		
	}

	public void setChildren (Recordset rsMenu, Db db, String pid, String queryMenu, String queryItem) throws Throwable {
		String query = StringUtil.replace(queryMenu, "${menu_id}", pid);
		Recordset rsChild = db.get(query);
		while(rsChild.next()){
			rsMenu.addNew();
			rsChild.copyValues(rsMenu);
			setChildren(rsMenu, db, rsChild.getString("id"), queryMenu, queryItem);
		}
		//查询父层
		query = StringUtil.replace(queryItem, "${menu_id}", pid);
		Recordset rsItem = db.get(query);
		while(rsItem.next()){
			rsMenu.addNew();
			rsItem.copyValues(rsMenu);
		}
	}
}
