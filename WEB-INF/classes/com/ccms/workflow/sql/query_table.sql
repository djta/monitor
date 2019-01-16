SELECT
	t.pk_value
	,b.bpk_field
	,b.table_code
FROM
	os_wfentry t
	inner join t_table b
	on t.table_id = b.tuid
WHERE
    t.id = ${id}
