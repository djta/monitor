//$Id: OracleDialect.java,v 1.17 2005/09/30 20:56:08 steveebersole Exp $
package com.ccms.quartz.pub.dialect;

public class OracleDialect extends Dialect {

	public String getLimitString(String sql, int offset, int limit) {
		boolean hasOffset = offset > 0;
		sql = sql.trim();
		boolean isForUpdate = false;
		if ( sql.toLowerCase().endsWith(" for update") ) {
			sql = sql.substring( 0, sql.length()-11 );
			isForUpdate = true;
		}
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		if (hasOffset) {
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		}
		else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) row_ where rownum <= ").append(limit+offset).append(" ) row_2 where rownum_ > ").append(offset);
		}
		else {
			pagingSelect.append(" ) where rownum <= ").append(limit);
		}

		if (isForUpdate) pagingSelect.append(" for update");
		
		return pagingSelect.toString();
	}
}
