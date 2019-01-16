delete from 
	os_wfm_node_rule
where 
	exists(
		select 1 from os_wfm_node_to t where tuid = os_wfm_node_rule.node_to_id
		and exists(
			select 1 from os_wfm_node where wfm_id = ${fld:wfm_id} and (tuid = t.node_id or tuid=t.next_node)
		)
	)