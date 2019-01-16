package transactions.project.fitness.contract;

import java.math.BigDecimal;
import java.sql.Types;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * 查询获取卡号
 * @author C
 * 2016-08-15
 */
public class SearchContractCardForJson extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);
		String colname = getConfig().getConfigValue("colname", "");
		String sqlFile = getConfig().getConfigValue("sql-template", "query-base.sql");
		String pagingName = getConfig().getConfigValue("paging-recordset", "query.sql");
		String totalName = getConfig().getConfigValue("total-recordset", "query-page.sql");
		String qBase = getResource(sqlFile);
		StringBuffer qFilter = new StringBuffer(256);

		if (colname != null && colname.length() > 0) {
			// 拼接查询条件
			String[] params = colname.split(",");
			for (int j = 0; j < params.length; j++) {
				if (inputs.getValue(params[j]) != null) {
					qFilter.append(getResource("clause-" + params[j] + ".sql"));
				}
			}
		}

		// 分页排序相关
		String page = getRequest().getParameter("pageNo");
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		String pageSizeStr = getRequest().getParameter("pageSize");

		String orderby = "";
		Integer currPage = 1;
		Integer pageSize = 10;
		if (page != null && page.length() > 0) {
			try {
				currPage = Integer.parseInt(page);
			} catch (Throwable a) {

			}
		}
		if (pageSizeStr != null && pageSizeStr.length() > 0) {
			try {
				pageSize = Integer.parseInt(pageSizeStr);
				if (pageSize > 500) {
					pageSize = 500;
				}
			} catch (Throwable a) {

			}
		}
		if (sort != null && sort.length() > 0) {
			orderby = " order by " + sort;
			if (order != null && order.length() > 0) {
				orderby += " " + order;
			}
		}

		// 替换变量
		qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());
		qBase = getSQL(qBase, inputs);

		String sql = StringUtil.replace(qBase, "${orderby}", orderby);
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize * (currPage - 1), pageSize);

		String queryCount = "select count(1) as record_total from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));

		Recordset rs = getDb().get(sql);
		
		String queryCardType = getResource("query-cardtype.sql");
		while(rs.next()){
			// 获取卡号
			String relatecode = rs.getString("vc_relatecode");
			if( null != relatecode && relatecode.contains(";") ){
				String[] relateArr = relatecode.split(";");
				Integer contracttype = rs.getInteger("i_contracttype");
				Integer type = rs.getInteger("i_type");
				String cardcode = "";
				if( 0 == contracttype && 2 == type ){
					cardcode = this.getArrValue(relateArr, 6); 
				}else if( 0 == contracttype && (1 == type || 12 == type) ){
					cardcode = this.getArrValue(relateArr, 11);
				}else{
					cardcode = this.getArrValue(relateArr, 1);
				}
				rs.setValue("vc_code", cardcode);
			}
			// 获取卡类型
			String cardtype = "";
			String _queryCardType = getSQL(queryCardType, rs);
			Recordset rsCardType = getDb().get(_queryCardType);
			if( null != rsCardType && rsCardType.getRecordCount() > 0 ){
				rsCardType.first();
				cardtype = rsCardType.getString("vc_name");
			}
			rs.setValue("vc_cardtype", cardtype);
		}
		
		Recordset rsCount = getDb().get(queryCount);
		rsCount.first();
		Integer _total = rsCount.getInteger("record_total");
		Recordset rsPage = new Recordset();
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		BigDecimal b1 = new BigDecimal(_total);
		BigDecimal b2 = new BigDecimal(pageSize);
		Integer _pageCount = b1.divide(b2, java.math.BigDecimal.ROUND_UP).intValue();
		rsPage.setValue("total", _total);
		rsPage.setValue("pageno", currPage);
		rsPage.setValue("pages", _pageCount);

		getRequest().getSession().setAttribute(pagingName, rs);
		publish(pagingName, rs);
		publish(totalName, rsPage);

		return rc;
	}
	
	private String getArrValue(String[] arr, int idx){
		String value = "";
		if( null != arr && arr.length > idx ){
			value = arr[idx];
			if( null == value || "".equals(value) ){
				value = "";
			}
		}
		return value;
	}

}
