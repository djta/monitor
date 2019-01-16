SELECT
    t.table_code
    ,t.table_alias
FROM
	t_report f
	left join t_table t 
	on f.table_id=t.tuid
WHERE
    f.tuid = ${fld:report_id}
