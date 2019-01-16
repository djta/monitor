INSERT	INTO
t_document_params
(
	tuid
	, document_id
	, params_code
	, params_url
	, params_value
	, remark
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	, ${fld:document_id}
	, ${fld:params_code}
	, ${fld:params_url}
	, ${fld:params_value}
	, ${fld:remark}
)