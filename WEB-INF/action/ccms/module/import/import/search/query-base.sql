select
	tuid
	,imp_name
	,before_class_name
	,pre_class_name
	,post_class_name
	,validator_class_name
	,title_line_num
	,(select subject_name from t_subject where tuid=subject_id) as subject_name
	,remark
	,created
	,(select name from hr_staff where userlogin=p.createdby) as createdby
	,updated
	,(select name from hr_staff where userlogin=p.updatedby) as updatedby
from
	t_import p
where
	subject_id = ${fld:subject_id}
${filter}
${orderby}
