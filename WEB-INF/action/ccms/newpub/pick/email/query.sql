select 
	tuid    as  id
	, template_name  as description
	, subject_name
from 
	cc_email_template
where
	subject_id = ${fld:subject_id}
order by 
	id
