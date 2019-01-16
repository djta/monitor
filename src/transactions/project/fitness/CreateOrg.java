package transactions.project.fitness;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

/**
 * 修改参数配置表
 * @author Administrator
 *
 */
public class CreateOrg extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		String insertSkill = getResource("insert-skill.sql");
		String insertSkillMenu = getResource("insert-skill-menu.sql");
		String querySkill = getResource("query-skill.sql");
		String querySkillMenu = getResource("query-skill-menu.sql");
		querySkillMenu = getSQL(querySkillMenu, inputParams);
		
		querySkill = getSQL(querySkill, inputParams);
		Recordset rsSkill = db.get(querySkill);
		while(rsSkill.next()){
			String _insertSkill = getSQL(insertSkill, rsSkill);
			db.addBatchCommand(_insertSkill);
			
			String _querySkillMenu = getSQL(querySkillMenu, rsSkill);
			Recordset rsSkillMenu = db.get(_querySkillMenu);
			while(rsSkillMenu.next()){
				String _insertSkillMenu = getSQL(insertSkillMenu, rsSkillMenu);
				db.addBatchCommand(_insertSkillMenu);
			}
			db.exec();
		}
		return rc;
	}
}
