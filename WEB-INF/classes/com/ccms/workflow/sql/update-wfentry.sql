update
	os_wfentry
set
	wfm_id = ${wfm_id}
	,table_id = ${fld:table_id}
	,p_pk_value = ${fld:__p_pk_value__}
	,pk_value = ${fld:bpk_field_value}
	,parent_id = ${fld:__parent_wfentry_id__}
	,parent_node_id = ${fld:__parent_node_id__}
	,created = {ts '${def:timestamp}'}
	,createdby = '${def:user}'
where
	id = ${id}