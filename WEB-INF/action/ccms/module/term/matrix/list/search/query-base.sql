SELECT
    f.tuid
    ,f.item_id
    ,f.list_name
    ,f.list_code
    ,f.list_score
    ,case when f.show_type='0' then '标签'
	  when f.show_type='1' then '文本'
	  when f.show_type='2' then '标签加文本'
	  else ''
     end as show_type
    ,case when f.is_unspeak='0' then '否'
	  when f.is_unspeak='1' then '是'
	  else ''
     end as is_unspeak
    ,f.show_order
    ,f.remark
    ,s.item_name
FROM
	t_term_list f
	left join t_term_item s
	on  f.item_id = s.tuid
WHERE
  	s.tuid = ${fld:item_id1}
${filter}
${orderby}
    