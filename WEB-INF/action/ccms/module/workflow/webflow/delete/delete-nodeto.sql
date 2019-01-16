delete from 
	os_wfm_node_to
where 
    node_id in (
	select 
		t.tuid 
	from 
		os_wfm_node t
	where
		t.wfm_id = ${fld:id}
    )
