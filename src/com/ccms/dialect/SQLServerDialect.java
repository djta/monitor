package com.ccms.dialect;

public class SQLServerDialect extends Dialect {

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		int orderByIndex = querySelect.toLowerCase().lastIndexOf("order by");  
        if (orderByIndex <= 0) {  
        	return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), " top " + limit )
			.toString();
        }
        String sqlOrderBy = querySelect.substring(orderByIndex + 8);  
        String sqlRemoveOrderBy = "select * from (" + querySelect.substring(0, orderByIndex) + ") t";
        return new StringBuffer(querySelect.length() + 100)
                .append("with tempPagination as(")
                .append(sqlRemoveOrderBy)
                .insert( getAfterSelectInsertPoint(querySelect) + 23, " ROW_NUMBER() OVER(ORDER BY " + sqlOrderBy + ") as RowNumber," )  
                .append( ") select * from tempPagination where RowNumber > " + offset + "  and RowNumber <= " + (limit+offset) )  
                .toString();
	}
}
