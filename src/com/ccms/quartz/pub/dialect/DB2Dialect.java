package com.ccms.quartz.pub.dialect;

public class DB2Dialect extends Dialect {

	/**
	 * Render the <tt>rownumber() over ( .... ) as rownumber_,</tt> 
	 * bit, that goes in the select list
	 */
	private String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
			.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase().indexOf("order by");
		
		if ( orderByIndex>0 && !hasDistinct(sql) ) {
			rownumber.append( sql.substring(orderByIndex) );
		}
			 
		rownumber.append(") as rownumber_,");
		
		return rownumber.toString();
	}

	public String getLimitString(String sql, int offset, int limit) {
		boolean hasOffset = offset > 0;
		int startOfSelect = sql.toLowerCase().indexOf("select");
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
					.append( sql.substring(0, startOfSelect) ) //add the comment
					.append("select * from ( select ") //nest the main query in an outer select
					.append( getRowNumber(sql) ); //add the rownnumber bit into the outer query select list
		
		if ( hasDistinct(sql) ) {
			pagingSelect.append(" row_.* from ( ") //add another (inner) nested select
				.append( sql.substring(startOfSelect) ) //add the main query
				.append(" ) as row_"); //close off the inner nested select
		}
		else {
			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); //add the main query
		}
				
		pagingSelect.append(" ) as temp_ where rownumber_ ");
		
		//add the restriction to the outer select
		if (hasOffset) {
			pagingSelect.append("between ").append(limit).append("+1 and ").append(limit);
		}
		else {
			pagingSelect.append("<= ").append(limit);
		}
		
		return pagingSelect.toString();
	}

	private static boolean hasDistinct(String sql) {
		return sql.toLowerCase().indexOf("select distinct")>=0;
	}

}
