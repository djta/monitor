delete from 
	os_wfm_node_rule
where 
    node_to_id in (
	select 
		t.tuid 
	from 
		os_wfm_node_to t 
		right join os_wfm_node n 
		on t.node_id = n.tuid
	where
		n.wfm_id = ${fld:id}
    )
