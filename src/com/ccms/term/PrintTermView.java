package com.ccms.term;

import java.sql.Connection;

import javax.sql.DataSource;

import dinamica.Db;
import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class PrintTermView extends GenericOutput{

	public void print(TemplateEngine te, GenericTransaction data) throws Throwable
	{
		super.print(te, data);

		//获取数据库连接
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");
				
		if (getConfig().transDataSource!=null)
			dataSourceName = getConfig().transDataSource;
		
		if (jndiPrefix==null)
			jndiPrefix="";
		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);
		
		Connection conn = null;
		try
		{
			if(this.getConnection()==null)
			{
				conn = ds.getConnection();
				this.setConnection(conn);
			}
		
			Recordset rsTerm = data.getRecordset("query-term.sql");
			String question_bank_id = rsTerm.getString("question_bank_id");
			
			Recordset rsType = data.getRecordset("query-type.sql");
	
			StringBuilder buf = new StringBuilder(4000);
			
			Db db = getDb();
			
			//获取模板
			String detail_item = getResource("query-items.sql");
			String detail_matrix_item = getResource("detail-matrix-item.sql");
			String detail_matrix = getResource("group-matrix.htm");
			String detail_list = getResource("detail-list.sql");
			String group_list = getResource("group-list.htm");
			String group_skip_rule = getResource("group-skip-rule.htm");
			String group_out_rule = getResource("group-out-rule.htm");
			String group_item = getResource("group-master.htm");
			String group_types = getResource("group-types.htm");
			String detail_domain = getResource("query_domain.sql");
			String detail_droplist = getResource("group-droplist.htm");
			
			rsType.top();
			while(rsType.next()){
				StringBuilder itemBuf = new StringBuilder();

				String itemSql = StringUtil.replace(detail_item, "${type_id}", rsType.getString("type_id"));
				itemSql = StringUtil.replace(itemSql, "${tags}", rsType.getString("tags"));
				itemSql = StringUtil.replace(itemSql, "${item_num}", rsType.getString("item_num"));
				itemSql = StringUtil.replace(itemSql, "${question_bank_id}", question_bank_id);
				Recordset rsMaster = db.get(itemSql);
				while (rsMaster.next())
				{
					StringBuilder listBuf = new StringBuilder();
					String tuid = rsMaster.getString("item_id");
					
					String is_matrix = rsMaster.getString("is_matrix");
					//判断是否是矩阵类型题
					if("1".equals(is_matrix)){
						String matrixSql = StringUtil.replace(detail_matrix_item, "${item_id}", tuid);
						Recordset mRs = db.get(matrixSql);
						while(mRs.next()){
							TemplateEngine mt = new TemplateEngine(getContext(),getRequest(), detail_matrix);
							mt.replace(mRs,"");
							listBuf.append(mt.toString());
						}
					}
					//添加所有选择项
					String listSql = StringUtil.replace(detail_list, "${item_id}", tuid);
					Recordset rs = db.get(listSql);
					while(rs.next()){
						String namespace = rs.getString("namespace");
						StringBuffer sb = new StringBuffer();
						if(namespace != null && namespace.length() > 0){
							String queryDomain = StringUtil.replace(detail_domain, "${domain_namespace}", namespace);
							TemplateEngine nt = new TemplateEngine(getContext(),getRequest(), queryDomain);
							Recordset rsDomain = db.get(nt.toString());
							rsDomain.top();
							while(rsDomain.next()){
								nt = new TemplateEngine(getContext(),getRequest(), detail_droplist);
								nt.replace(rsDomain,"");
								sb.append(nt.toString());
							}
						}
						
						String list = StringUtil.replace(group_list, "${droplist}", sb.toString());
						TemplateEngine nt = new TemplateEngine(getContext(),getRequest(), list);
						nt.replace(rs,"");
						listBuf.append(nt.toString());
					}
					
					//添加入口跳跃逻辑
					String skipSql = getResource("query-skip-rule.sql");
					skipSql = StringUtil.replace(skipSql, "${item_id}", tuid);
					Recordset skipRs = db.get(skipSql);
					while(skipRs.next()){
						TemplateEngine st = new TemplateEngine(getContext(),getRequest(), group_skip_rule);
						st.replace(skipRs,"");
						listBuf.append(st.toString());
					}
					
					//添加出口逻辑
					String outSql = getResource("query-out-rule.sql");
					outSql = StringUtil.replace(outSql, "${item_id}", tuid);
					Recordset outRs = db.get(outSql);
					while(outRs.next()){
						TemplateEngine ot = new TemplateEngine(getContext(),getRequest(), group_out_rule);
						ot.replace(outRs,"");
						listBuf.append(ot.toString());
					}
					
					String item = StringUtil.replace(group_item, "${list}", listBuf.toString());
					
					TemplateEngine t = new TemplateEngine(getContext(),getRequest(), item);
					t.replace(rsMaster,"");
					itemBuf.append(t.toString());
				}
				String types = StringUtil.replace(group_types, "${item}", itemBuf.toString());
				
				TemplateEngine t = new TemplateEngine(getContext(),getRequest(), types);
				t.replace(rsType,"");
				buf.append(t.toString());
				
			}
			
			te.replace("${group}", buf.toString());
		}
		catch (Throwable e)
		{
			throw e;
		}
		finally
		{
			if(conn != null){
				conn.close();
			}
		}
	}
}
