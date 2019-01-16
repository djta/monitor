SELECT
    f.tuid
    , f.field_code
    , f.field_name_cn
    , f.field_name_en
    , f.field_length
    , f.field_type
    , ft.name_alias    as  type_alias
    , f.fk_tab
    , f.show_type
    , f.created
    , f.remark
    , f.domain_namespace
    , t.table_alias
    , s.subject_name
    , f.show_order
FROM
	t_field f 
	left join t_table t
	on f.table_id = t.tuid
	left join t_subject s
	on t.subject_id = s.tuid
	left join t_field_type ft
	on upper(f.field_type) = upper(ft.name)
WHERE
    1 = 1
and f.deleted='0'
${filter}
order by 
	f.field_code
