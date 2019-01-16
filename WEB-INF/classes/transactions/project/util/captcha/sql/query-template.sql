SELECT 
	tuid AS template_id,
	template_content,
	remark AS content 
FROM cc_sms_template 
WHERE template_name = '${name}' AND is_enabled = '1'
