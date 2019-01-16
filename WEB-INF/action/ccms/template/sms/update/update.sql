UPDATE
	cc_sms_template
SET
	template_name = ${fld:template_name}
	,template_content = ${fld:template_content}
	,account_id = ${fld:account_id}
WHERE
	tuid = ${fld:tuid}

