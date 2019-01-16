select 
	tuid    as  id
	, template_name  as description
	, title as template_content
from 
	cc_mms_template
where
	is_enabled = '1'
and
	subject_id = ${fld:subject_id}
order by 
	id
