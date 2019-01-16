package com.ccms.quartz.pub.dialect;

public class MySQLDialect extends Dialect {

	public String getLimitString(String sql, int offset, int limit) {
		boolean hasOffset = offset > 0;
		return new StringBuffer( sql.length()+20 )
			.append(sql)
			.append( hasOffset ? " limit "+offset+", "+limit : " limit "+limit)
			.toString();
	}
	
}
