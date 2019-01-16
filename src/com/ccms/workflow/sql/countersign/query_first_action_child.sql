SELECT
	a.tuid as action_id
	,w.wfm_name
	,w.table_id
	,w.tuid as wfm_id
FROM
	os_wfm_node pn
	inner join os_wfm w
	on pn.child_wfm_id = w.tuid
	inner join os_wfm_node n
	on n.wfm_id = w.tuid
	inner join os_wfm_node_to a
	on n.tuid = a.node_id
WHERE
    pn.tuid = ${fld:__parent_node_id__}
and
	n.node_type = '0'