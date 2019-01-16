SELECT
	p.owner as userlogin
	,h.alias as authuser
	,n.document_id
	,n.node_name as op_title
	,(select action_name from os_wfm_node_to where tuid=${fld:__action_id__}) as op_action
	,${fld:__pk_value__} as pk_value
FROM
	os_currentstep p
	inner join hr_staff h
	on p.owner = h.userlogin
	inner join os_wfm_node n
	on p.step_id = n.tuid
where
	p.entry_id = ${fld:__wfentry_id__}
and
	p.step_id = ${fld:__current_step_id__}
and
	h.alias is not null
and
	p.owner <> '${def:user}'
and
	h.alias = '${def:user}'