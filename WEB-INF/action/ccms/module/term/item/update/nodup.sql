select
	1
from 
	t_term_item t4
	left join t_term_type t5 on t4.type_id = t5.tuid
	left join t_term t6 on t5.term_id = t6.tuid
where 
	t6.tuid = (
			select 
				DISTINCT
				term_id
			from 
				t_term_item t1
				left join t_term_type t2 on t1.type_id = t2.tuid
				left join t_term t3 on t2.term_id = t3.tuid
			where 
				t1.type_id = ${fld:type_id}
			and 
				t1.item_code = ${fld:item_code}
			and 
				t1.tuid <> ${fld:tuid}
	)

