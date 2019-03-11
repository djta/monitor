package com.ccms.workflow.json;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONObject;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class PublishWebFlow extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {

		String xmlStr = inputParams.getString("xml_value");
		if (xmlStr != null && xmlStr.length() > 10) {
			Db db = getDb();
			
			JSONObject jsonObj = new JSONObject(xmlStr);

			
			String deleteNode = getSQL(getResource("delete-node.sql"), inputParams);
			String insertNode = getResource("insert-node.sql");

			String deleteWfm = getSQL(getResource("delete-wfm.sql"), inputParams);
			String insertWfm = getSQL(getResource("insert-wfm.sql"), inputParams);

			Recordset rsNode = new Recordset();
			rsNode.append("tuid", Types.INTEGER);
			rsNode.append("wfm_id", Types.INTEGER);
			rsNode.append("node_name", Types.VARCHAR);
			rsNode.append("node_type", Types.VARCHAR);
			rsNode.append("document_id", Types.INTEGER);
			rsNode.append("remark", Types.VARCHAR);
			rsNode.append("step_type", Types.VARCHAR);
			rsNode.append("old_status", Types.VARCHAR);
			rsNode.append("status", Types.VARCHAR);

			rsNode.append("child_wfm_id", Types.INTEGER);
			rsNode.append("countersign_per", Types.INTEGER);
			rsNode.append("countersign_type", Types.VARCHAR);
			rsNode.append("countersign_post", Types.INTEGER);


			// step
			Integer wfm_id = inputParams.getInteger("wfm_id");
			JSONObject stepObj = jsonObj.getJSONObject("nodes");
			String[] steps = JSONObject.getNames(stepObj);
			if(steps != null && steps.length > 0){
				for(int i=0;i<steps.length;i++){
					JSONObject step = stepObj.getJSONObject(steps[i]);
					String type = step.getString("type");
					//当空白节点时忽略
					if("blank".equalsIgnoreCase(type)){
						continue;
					}
					Integer node_id =0;
					try {
						node_id = step.getInt("node_id");
					}catch(Throwable e) {
						String querySeq="select\r\n" + 
								"	${seq:nextval@${schema}seq_default} as seq\r\n" + 
								"from \r\n" + 
								"	dual";
						Recordset seqRecordset=db.get(getSQL(querySeq, inputParams));
						seqRecordset.first();
						node_id=seqRecordset.getInt("seq");
						step.put("node_id", node_id);
					}
					 
					if(node_id == null){
						continue;
					}
					String node_name = null;
					try {
						node_name=step.getString("node_name");
					}catch (Throwable e) {
						// TODO: handle exception
						node_name=step.getString("name");
						step.put("node_name", node_name);
					}
					

					
					rsNode.addNew();
					rsNode.setValue("tuid", node_id);
					rsNode.setValue("wfm_id", wfm_id);
					rsNode.setValue("node_name", node_name);
					
				}
			}

			// 删除node
			db.addBatchCommand(deleteNode);
			// 删除wfm
			db.addBatchCommand(deleteWfm);

			db.exec();
			xmlStr=jsonObj.toString();
			// 添加wfm
			String dbType = getContext().getInitParameter("db");
			PreparedStatement p = null;
			try {
				p = getConnection().prepareStatement(insertWfm);
				if("postgresql".equalsIgnoreCase(dbType)){//postgresql 特殊处理
					p.setString(1, xmlStr);
					p.setString(2, xmlStr);
				}else{
					StringReader sr = new StringReader(xmlStr);
					StringReader sr2 = new StringReader(xmlStr);
					p.setClob(1, sr, xmlStr.length());
					p.setClob(2, sr2, xmlStr.length());
				}
				p.execute();
			} catch (SQLException sqe) {
				Throwable t = null;
				String msg = null;
				String date = StringUtil.formatDate(new java.util.Date(), "dd-MM-yyyy HH:mm:ss");

				if (sqe.getNextException() != null) {
					msg = sqe.getNextException().getMessage();
					t = sqe.getCause();
				} else {
					msg = sqe.getMessage();
					t = sqe;
				}
				System.err.println("[WARNING@" + date + "] Db.saveBlob failed: " + msg + " SQL: [" + insertWfm + "]");
				throw t;

			} catch (Throwable e) {
				throw e;
			} finally {
				if (p != null)
					p.close();
			}
			
			// 添加node
			rsNode.top();
			while (rsNode.next()) {
				String insert = getSQL(insertNode, rsNode);
				db.addBatchCommand(insert);
			}

			db.exec();
			

		}

		return 0;

	}
}
