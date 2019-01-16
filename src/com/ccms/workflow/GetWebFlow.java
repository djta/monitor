package com.ccms.workflow;

import java.io.BufferedInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import dinamica.GenericTableManager;
import dinamica.Recordset;

public class GetWebFlow extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		String query = getSQL(getResource("query.sql"),inputParams);
		Statement s = getConnection().createStatement();
		ResultSet rs = s.executeQuery(query);
		BufferedInputStream buf = null;
		
		Recordset queryRs = new Recordset();
		queryRs.append("xml_value", Types.VARCHAR);
		queryRs.addNew();
    	queryRs.setValue("xml_value", "");

		if(rs.next()){
			String DatabaseType = getContext().getInitParameter("db");
			if("postgresql".equalsIgnoreCase(DatabaseType)){
				byte[] xml = rs.getBytes(1);
				if(xml != null){
					queryRs.setValue("xml_value", new String(xml));
				}
			}else{
				Blob blob = rs.getBlob(1);
				if(blob != null){
					int bytes = 0;
					byte buffer[] = new byte[(int)blob.length()];
					buf = new BufferedInputStream( blob.getBinaryStream() );
					StringBuffer sb = new StringBuffer();
					while( bytes != -1 )
					{
						bytes = buf.read(buffer);
						if (bytes>0)
							sb.append(new String(buffer).trim());
					} 			 
					queryRs.setValue("xml_value", sb.toString());
				}
			}
			
		}
		publish("query.sql",queryRs);
		
		if (buf!=null) buf.close();
		if (s!=null) s.close();
		
		return rc;

	}
}
