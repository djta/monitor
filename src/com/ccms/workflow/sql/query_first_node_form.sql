SELECT
	document_id
FROM
	os_wfm_node
WHERE
    wfm_id = ${wfm_id}
and
	node_type = '0'