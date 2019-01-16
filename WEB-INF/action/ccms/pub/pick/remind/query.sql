select 
	tuid    as  id
	, template_name  as description
	, subject_name
from 
	cc_remind_template
where
	is_enabled = '1'
order by 
	id
