SELECT
	f.tuid
	,f.subject_id
	,f.table_id
	,f.report_name_cn
	,case   when f.report_type='0' then '平面报表'
	    when f.report_type='1' then '分组报表'
	    when f.report_type='2' then '交叉报表'
	else '未知类型'
	end    as  report_type_alias
	,case   when f.engine_type='0' then '琴棋报表'
	    when f.engine_type='1' then '简表'
	    when f.engine_type='2' then '图表'
	else '未知类型'
	end    as  engine_type_alias
	,f.engine_type
	,f.remark
	,t.table_alias
FROM
	t_report f
	left join t_table t
	on  f.table_id = t.tuid

WHERE
	f.deleted = '0'
    
	${filter}

${orderby}

    