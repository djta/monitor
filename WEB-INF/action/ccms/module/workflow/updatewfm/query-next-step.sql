SELECT
	n.step_type as next_step_type
	,a.next_node
	,${fld:__pk_value__} as pk_value
	,${fld:__wfentry_id__} as wfentry_id
	,${fld:__current_step_id__} as current_step_id
	,n.document_id
	,n.child_wfm_id
	,n.node_type
FROM
	os_wfm_node n
	inner join os_wfm_node_to a
	on a.next_node = n.tuid
where
	a.tuid = ${fld:__action_id__}