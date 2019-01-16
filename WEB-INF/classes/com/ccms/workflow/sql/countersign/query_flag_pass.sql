select
	t.result_flag as is_pass
from
	os_wfm_countersign_comment t
	inner join os_wfentry w
	on t.tuid = w.pk_value 
where
	w.parent_id = ${id}
and
	w.parent_node_id = ${step_id}
and
	w.created = (
		select
			max(tt.created)
		from
			os_wfm_countersign_comment tt
			inner join os_wfentry ww
			on tt.tuid = ww.pk_value 
		where
			ww.parent_id = ${id}
		and
			ww.parent_node_id = ${step_id}
	)