package transactions.project.fitness.doc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import transactions.project.fitness.util.ErpTools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 根据模板生成word
 * @author C
 * 2016-07-11
 */
public class WordTemplateReplace extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		// 获取主键
		String vc_contractcode = "";
		if( inputParams.containsField("pk_value") && null != inputParams.getString("pk_value") ){
			vc_contractcode = inputParams.getString("pk_value");
		}
		if( StringUtils.isBlank(vc_contractcode) ){
			throw new Throwable("主键编号pk_value不能为空");
		}
		// 获取模板地址
		String templatePath = getConfig().getConfigValue("doc-template","");
		if( StringUtils.isBlank(templatePath) ){
			throw new Throwable("节点模板地址doc-template不能为空");
		}

		// 获取模板中需要替换的参数列表
		String params = getConfig().getConfigValue("params","");
		params = ( StringUtils.isNotBlank(params) ? params : "" );
		String[] paramArr = params.split(";");
		// 获取替换参数对应的字段名
		String fields = getConfig().getConfigValue("fields","");
		fields = ( StringUtils.isNotBlank(fields) ? fields : "" );
		String[] fieldArr = fields.split(";");
		int len = paramArr.length;
		if( len != fieldArr.length ){
			throw new Throwable("节点参数列表params与节点字段列表fields的长度不一致");
		}
		
		// 查询数据
		String queryData = getResource("query-data.sql");
		Recordset rsData = db.get(getSQL(queryData, inputParams));
		if( rsData.getRecordCount() <= 0 ){
			throw new Throwable("未找到相应的数据");
		}

		// 组装需替换参数列表
		rsData.first();
		Map<String, Object> mapAll = new HashMap<String, Object>();
		for( int i = 0; i < len; i++ ){
			String key = paramArr[i];	// 替换参数
			if( StringUtils.isBlank(key) ){
				continue;
			}
			String fieldname = fieldArr[i];	// 字段名
			Object value = null;
			if( StringUtils.isNotBlank(fieldname) ){
				value = rsData.getValue(fieldname);
			}
			if( null == value || "".equals(value) ){
				mapAll.put(key, "");
			}else{
				if( "headpic".equals(key) ){
					Map<String,Object> headerpic = new HashMap<String, Object>();  
					headerpic.put("width", 225);  
					headerpic.put("height", 240);  
					headerpic.put("type", "jpg");  
					headerpic.put("content", value);  
					mapAll.put(key, headerpic);
				}else{
					mapAll.put(key, value);
				}
			}
		}
		
		// 打印类型
		String print_type = "";
		if( inputParams.containsField("print_type") && null != inputParams.getString("print_type") ){
			print_type = inputParams.getString("print_type");
		}
		if( StringUtils.isBlank(print_type) || ( !"contract".equals(print_type) && !"ticket".equals(print_type) ) ){
			print_type = "ticket";
		}
		
		// 根据模板生成word
		String subpath = print_type;	// 新文档保存子路径
		String docPath  = ErpTools.replaceWordContent(getRequest(), templatePath, subpath, mapAll, vc_contractcode);
		if( StringUtils.isBlank(docPath) ){
			throw new Throwable("文档生成失败");
		}
		
		// 输出
		Recordset rsdoc = new Recordset();
		rsdoc.append("docpath", Types.VARCHAR);
		rsdoc.addNew();
		rsdoc.setValue("docpath", docPath);
		publish("_rsdoc", rsdoc);
		return rc;
	}
}
