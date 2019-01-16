SELECT
	n.child_wfm_id
	,w.pk_value as p_pk_value
	,t.step_id as parent_node_id
	,t.entry_id as parent_id
	,n.step_type
	,case when n.step_type='3' then n.document_id else (select max(document_id) from os_wfm_node where wfm_id=n.child_wfm_id and node_type='0') end as document_id
	,case when n.step_type='3' then n.node_name else (select max(node_name) from os_wfm_node where wfm_id=n.child_wfm_id and node_type='0') end as node_name
FROM
	os_currentstep t
	inner join os_wfentry w
	on t.entry_id = w.id
	inner join os_wfm_node n 
	on t.step_id = n.tuid
WHERE
    t.entry_id = ${fld:__wfentry_id__}
and
	(n.step_type = '4' or n.step_type = '3')
and
	n.tuid = ${fld:__current_step_id__}
and
	n.child_wfm_id is not null
and
	t.owner = '${def:user}'
and
	not exists(
		select 1 from os_wfentry where parent_id=t.entry_id and parent_node_id=t.step_id
	)