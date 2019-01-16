package transactions.project.fitness;

import java.sql.Types;

import transactions.project.fitness.util.ErpTools;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SearchData extends GenericTransaction {

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
			// 替换变量
			qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());
			qBase = getSQL(qBase, inputs);
			Recordset rs = getDb().get(qBase);
			publish(sqlFile, rs);
		}

		String basePath = ErpTools.getRealSavePath(getRequest(), null, null, false);	// 文件目录
		basePath = StringUtil.replace(basePath, "\\", "/");
		Recordset rspath = new Recordset();
		rspath.append("localpath", Types.VARCHAR);
		rspath.addNew();
		rspath.setValue("localpath", basePath);
		publish("_rspath", rspath);
		return rc;
	}
}
