select
	b.*
from 
	t_term_type t
	inner join t_term_item d
	on t.tuid = d.type_id
	inner join t_term_list b
	on d.tuid = b.item_id
where 
	t.term_id = ${fld:term_id}