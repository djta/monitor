update
	os_wfentry
set
	wfm_id = ${fld:wfm_id}
	,table_id = ${fld:table_id}
	,pk_value = ${seq:currval@seq_os_wfm_countersign_comment}
	,parent_id = ${parent_id}
	,parent_node_id = ${fld:parent_node_id}
	,created = {ts '${def:timestamp}'}
	,createdby = '${caller}'
where
	id = ${id}