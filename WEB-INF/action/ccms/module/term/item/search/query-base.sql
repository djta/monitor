SELECT
    f.tuid
    ,f.type_id
    ,f.item_name
    ,f.item_code
    ,f.tags
    ,case when f.is_matrix='1' then '是'
	  else '否'
	end as is_matrix
    ,case when f.is_page_break='1' then '是'
	  else '否'
	end as is_page_break
    ,case when f.list_show_type='0' then '文本输入'
          when  f.list_show_type='1' then '多选一'
	  when  f.list_show_type='2' then '复选'
	  else ''
	end as list_show_type
    ,f.show_order
    ,f.remark
    ,s.type_name
    ,case when f.is_matrix='1' then concat('<button class="btn btn-primary btn-md setTermMatrixList_btn" type="button" code="',f.tuid,'" title="矩阵设置">矩阵设置</button>')
	  else concat(concat(concat(concat('<button class="btn btn-primary btn-md setTermItemList_btn" type="button" code="',f.tuid),'" code1="'),f.list_show_type),'" title="选择项定义">选择项定义</button>')
	end as operator
   
FROM
	t_term_item f
	left join t_term_type s on  f.type_id = s.tuid
WHERE
      s.tuid = ${fld:type_id1}
    ${filter}  
	${orderby}