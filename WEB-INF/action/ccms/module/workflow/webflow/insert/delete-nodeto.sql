delete from 
	os_wfm_node_to
where 
	exists(
		select 1 from os_wfm_node where wfm_id = ${fld:wfm_id} and (tuid = os_wfm_node_to.node_id or tuid=os_wfm_node_to.next_node)
	)