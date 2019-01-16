SELECT
	t.table_code
	,f.report_sql
	,case when t.delete_field is null then null else concat(concat(t.table_code,'.'),t.delete_field) end as delete_field
FROM
	t_report f
	left join t_table t 
	on f.table_id=t.tuid
WHERE
	f.tuid = ${fld:report_id}

