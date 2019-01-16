select  
	s.tuid
	,s.item_id
	,s.list_name
	,s.list_code
	,case when s.list_score is null then 0 else s.list_score end as list_score
	,s.show_order
	,s.show_type
	,s.is_unspeak
	,s.remark  
	,t.item_code
	,t.list_show_type as parent_type
	,s.namespace
	,s.namespace_op
from 
	t_term_list s
	inner join t_term_item t on s.item_id = t.tuid
where 
	s.item_id = ${item_id}
order by 
	s.show_order asc