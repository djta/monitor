select 
	t.tuid,
	m.item_name,
	t.list_name,
	t.list_code,
	t.list_score,
	(case when t.show_type='0' then '标签' 
		when t.show_type='1' then '文本' 
		when t.show_type='2' then '标签加文本'
		when t.show_type='3' then '下拉框' end) as show_type,
	(case when t.is_unspeak='1' then '是' else '否' end) as is_unspeak,
	t.show_order,
	t.list_score_code,
	t.namespace,
	t.remark
 from 
 	t_term_list t
 	left join t_term_item m on t.item_id = m.tuid
 where
 	item_id = ${fld:item_id}
 	
 	${filter}
 	${orderby}