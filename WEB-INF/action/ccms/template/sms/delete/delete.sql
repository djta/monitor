UPDATE
	cc_sms_template
SET
	is_enabled = '0'
WHERE
	tuid = ${fld:tuid}

