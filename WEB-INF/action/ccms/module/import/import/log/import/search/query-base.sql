select
	tuid
	,imp_id
	,(select imp_name from t_import where tuid=imp_id) as imp_name
	,load_date
	,load_time
	,total_record
	,file_name
	,insert_record
	,update_record
	,error_count
	,import_batch
	,remark
	,created
	,(select name from hr_staff where userlogin=t_import_history.createdby) as createdby
from
	t_import_history
where
1=1
	${filter}
${orderby}