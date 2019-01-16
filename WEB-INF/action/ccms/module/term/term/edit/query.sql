select
 	tuid
	,term_name
	,term_type
	,pre_class
	,post_class
	,remark
	,status
	,logo_path
	,introduction
	,ending
	,(select t2.term_name from t_term t2 where t2.tuid=t1.question_bank_id) as question_bank_name
from
	t_term t1
WHERE
	tuid = ${fld:tuid}