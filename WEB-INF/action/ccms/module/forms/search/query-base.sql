SELECT
    f.tuid
    ,f.subject_id
    ,f.table_id
    ,f.form_name_cn as form_name
    ,f.remark
    ,t.table_alias
FROM
	t_form f
	left join t_table t
	on  f.table_id = t.tuid
WHERE
    1 = 1
and
    f.deleted = '0'
${filter}
${orderby}