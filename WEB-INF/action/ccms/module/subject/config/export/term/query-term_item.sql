select
	d.*
from 
	t_term_type t
	inner join t_term_item d
	on t.tuid = d.type_id
where 
	t.term_id = ${fld:term_id}