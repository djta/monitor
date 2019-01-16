package transactions.project.weixin.store.term;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class AfterSaveTerm extends GenericTransaction{

	@Override
	public int service(Recordset inputs) throws Throwable {
		Db db = getDb();

		String relation_id = inputs.getString("relation_id");
		String term_score_tuid = inputs.getString("term_score_tuid");
		if(relation_id!=null && term_score_tuid!=null && relation_id.length()>0 && term_score_tuid.length()>0){
			String update = getLocalResource("update-cb_comment.sql");
			db.exec(getSQL(update,inputs));
		}
		
		return 0;
	}

}
