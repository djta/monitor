select 
	tuid,
	template_name,
	template_content
from 
	cc_sms_template
where 
	is_enabled = '1'
	
	${filter}
	${orderby}