package transactions.project.fitness.weixin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

/***
 * 从微信服务器下载用户图片
 * @author C
 * 2016-02-27
 */
public class DownloadWxImageMarket extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Db db = getDb();
			// 提交参数验证
			String org_id = (inputParams.containsField("org_id") ? inputParams.getString("org_id") : null);

			
			String mediaIdParam = (inputParams.containsField("media_id") ? inputParams.getString("media_id") : null);
			String fieldNameParam = (inputParams.containsField("field_name") ? inputParams.getString("field_name") : null);
			String[] mediaIdArr = mediaIdParam.split(";");
			int idLen = ( null != mediaIdArr ? mediaIdArr.length : 0 );
			String[] fieldNameArr = fieldNameParam.split(";");
			int nameLen = ( null != fieldNameArr ? fieldNameArr.length : 0 );
			if( nameLen < 0 || idLen < 0 ){
				throw new Throwable("提交参数server_id、field_name不能为空");
			}else if( idLen != nameLen ){
				throw new Throwable("提交参数server_id、field_name内长度不匹配");
			}
			// service_id
			String query = getLocalResource("/transactions/project/weixin/common/query-service.sql");
			Recordset rs = db.get(query);
			if( null == rs || rs.getRecordCount() <= 0 ){
				throw new Throwable("参数service_id不能为空");
			}
			rs.first();
			Integer service_id = rs.getInteger("service_id");
			// 文件保存路径
			String savePath = getRealSavePath(getRequest(), "upload", "staff/"+org_id+"/", false);
			Integer user_id = (inputParams.containsField("user_id") ? inputParams.getInteger("user_id") : null);
			if( null == user_id || user_id.intValue() <= 0 ){
				throw new Throwable("提交参数user_id不能为空");
			}
			String insertSql = getResource("insert-att_files.sql");
			for( int i = 0; i < idLen; i++ ){
				if( null == mediaIdArr[i] || "".equals(mediaIdArr[i]) 
						|| null == fieldNameArr[i] || "".equals(fieldNameArr[i]) ){
					continue;
				}
				String mediaId = mediaIdArr[i];
				String fieldName = fieldNameArr[i];
				// 生成文件名
				String filename = user_id + "_" + fieldName + "_" + sdf.format(new Date()) + ".jpg";
				// 将图片保存至本地服务器
				WeixinUtil.getFileByMediaId(db, service_id, mediaId, savePath, filename);
				// 将图片路径回写至hr_staff表中
				String _update = getSQL(insertSql, inputParams);
				
				
				_update = StringUtils.replace(_update, "${field_name}", fieldName);
				_update = StringUtils.replace(_update, "${file_path}", savePath + filename);
				

				db.exec(_update);
			}
		} catch (Throwable e) {
			rc = 1;
			e.printStackTrace();
		}
		return rc;
	}
	
	private static String getRealSavePath(HttpServletRequest request, String parentPath, String subPath, boolean bPersist) {
		// 获得物理路径
		String strCurPath = request.getSession().getServletContext().getRealPath("/");
		String fileSeperator = System.getProperty("file.separator");
		
		int nPosSeperator = strCurPath.lastIndexOf(fileSeperator);//跟目录分割后最后一个文件夹
		String rootPath = strCurPath;//全路径
		if (bPersist) {
			nPosSeperator = rootPath.lastIndexOf(fileSeperator);
			rootPath = strCurPath.substring(0, nPosSeperator);
		}

		// 组织路径
		String totalParentPath = null;
		String totalSubPath = null;
		if ((parentPath != null) && (parentPath.length() > 0)) {
			totalParentPath = rootPath + fileSeperator + parentPath;//全路径名+"第二个参"
			File file = new File(totalParentPath);
			file.mkdirs();
			file = null;

			totalSubPath = rootPath + fileSeperator + parentPath;//全路径名+"第二个参"
			if ((subPath != null) && (subPath.length() > 0)) {
				totalSubPath = totalSubPath + fileSeperator + subPath;//全路径名+"第二个参+第三个参"
			}
			file = new File(totalSubPath);
			file.mkdirs();
			file = null;
		} else {
			totalSubPath = rootPath;//全路径
			if ((subPath != null) && (subPath.length() > 0)) {
				totalSubPath = totalSubPath + fileSeperator + subPath;
			}
			File file = new File(totalSubPath);
			file.mkdirs();
			file = null;
		}
		// 创建路径
		String savePath = totalSubPath + fileSeperator;
		return savePath;
	}
}
