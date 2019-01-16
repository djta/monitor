package transactions.project.util;

import org.apache.commons.lang.StringUtils;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SearchExport extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);
		String colname = getConfig().getConfigValue("colname", "");
		String sqlFile = getConfig().getConfigValue("sql-template", "");
		if( null != sqlFile && !"".equals(sqlFile) ){
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
			
			// 排序
			String orderby = "";
			String sort = getRequest().getParameter("sort");
			String order = getRequest().getParameter("order");
			if(sort != null && sort.length() > 0){
				int orderCount = 0;
				// 排序字段分割：多个排序字段用;隔开
				String[] sortArr = sort.split(";");
				order = (order != null && order.length() > 0 ? order : "");
				// 倒序/顺序字段分割
				String[] orderArr = order.split(";");
				// 排序拼接
				for(int i = 0; i < sortArr.length; i++){
					if( null == sortArr[i] || sortArr[i].length() <= 0 ){
						continue;
					}
					String sortStr = sortArr[i];
					if( null != sortStr && sortStr.length() > 0 ){
						if( orderCount == 0 ){
							orderby += sortStr;
						}else{
							orderby += "," + sortStr;
						}
						orderCount++;
						if( orderArr.length >= i+1 && null != orderArr[i] && orderArr[i].length() > 0 ){
							orderby += " " + orderArr[i];
						}
					}
				}
				if( StringUtils.isNotBlank(orderby) ){
					orderby = " order by " + orderby;
				}
			}
			
			// 替换变量
			qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());
			qBase = StringUtil.replace(qBase, "${orderby}", orderby);
			qBase = getSQL(qBase, inputs);
			Recordset rs = getDb().get(qBase);

			// 获取图片附件查询脚本
			String imgFile = getConfig().getConfigValue("img-template", "");
			if( StringUtils.isNotBlank(imgFile) ){
				String sqlImg = getResource(imgFile);
				while(rs.next()){
					String _sqlImg = getSQL(sqlImg, rs);
					Recordset rsImg = getDb().get(_sqlImg);
					rs.setChildrenRecordset(rsImg);
				}
			}
			publish(sqlFile, rs);
		}
		return rc;
	}
}
