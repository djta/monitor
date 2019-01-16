SELECT
    fp.tuid
    ,fp.document_id
    ,fp.params_code
    ,fp.params_url
    ,fp.params_value
    ,fp.remark
FROM
	t_document_params fp
WHERE
    1 = 1
${filter}
