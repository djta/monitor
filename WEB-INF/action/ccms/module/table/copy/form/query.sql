SELECT
    t.tuid  as table_id
    , t.table_alias
FROM
	t_table t
where
	t.tuid = ${fld:table_id}