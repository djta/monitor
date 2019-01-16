UPDATE
	t_document_params
SET
	document_id     =${fld:document_id}
	,params_code     =${fld:params_code}
	,params_url     =${fld:params_url}
	,params_value     =${fld:params_value}
	,remark	 =${fld:remark}
WHERE
	tuid	=${fld:tuid}
