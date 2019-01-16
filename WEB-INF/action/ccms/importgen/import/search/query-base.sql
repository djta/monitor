select
	tuid
	,imp_name
	,(select subject_name from t_subject where tuid=subject_id) as subject_name
	,remark
from
	t_import p
where
	subject_id = ${fld:subject_id}
${filter}