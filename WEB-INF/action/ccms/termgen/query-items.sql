(SELECT
	t.tuid  as item_id
	,t.item_name
	,t.item_code
	,t.is_matrix
	,t.list_show_type
	,t.is_page_break
	,t.show_order
	,s.show_order as show_order1
	,t.remark
	,t.is_matrix_transpose
	,t.is_list_mline
	,t.type_id
	,random() as random_order
FROM
	t_term_item t
	inner join t_term_type s on t.type_id = s.tuid
WHERE
	s.tuid = ${type_id}
order by s.show_order,t.show_order
)
union
(
SELECT
	t.tuid  as item_id
	,t.item_name
	,t.item_code
	,t.is_matrix
	,t.list_show_type
	,t.is_page_break
	,t.show_order
	,s.show_order as show_order1
	,t.remark
	,t.is_matrix_transpose
	,t.is_list_mline
	,${type_id} as type_id
	,random() as random_order
FROM
	t_term_item t
	inner join t_term_type s on t.type_id = s.tuid
WHERE
	s.term_id = ${question_bank_id}
and
	t.tags like '%${tags}%'
order by
	random_order
limit (case when ${item_num} is null or ${item_num}<0 then 0 else ${item_num} end)
)
order by show_order1,show_order