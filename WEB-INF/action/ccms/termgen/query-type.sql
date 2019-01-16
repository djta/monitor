SELECT
	t.tuid  as type_id
	,(t.item_num - (select count(1) from t_term_item where type_id=t.tuid)) as item_num
	,t.tags
	,t.type_name
	,t.show_order as code
FROM
	t_term_type t
WHERE
	t.term_id = ${fld:term_id}
order by 
	show_order