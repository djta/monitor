SELECT
	distinct
	lower(f.params_code) as params_code
	,f.params_url
	,case when replace(f.params_value,'${DEF','${def') is null then '' else replace(f.params_value,'${DEF','${def') end as params_value
FROM
	t_document_params f
WHERE
	f.document_id = ${document_id}