SELECT
    f.tuid
    ,f.item_id
    ,f.item_name
    ,f.item_code
    ,f.show_order
    ,f.remark
    ,s.item_name as item_name1
    ,case when f.show_type='0' then '文本输入'
          when  f.show_type='1' then '多选一'
	  when  f.show_type='2' then '复选'
	  else ''
	end as show_type
FROM
	t_term_item_matrix f
	left join t_term_item s
	on  f.item_id = s.tuid
WHERE
	s.tuid = ${fld:item_id1}
${orderby}