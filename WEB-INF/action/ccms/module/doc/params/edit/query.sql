SELECT
    f.tuid
    ,f.document_id
    ,f.params_code
    ,f.params_url
    ,f.params_value
    ,f.remark
FROM
	t_document_params f
WHERE
	f.tuid=${fld:id}
