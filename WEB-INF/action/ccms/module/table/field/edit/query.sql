SELECT
    f.tuid
    ,f.table_id
    ,f.field_code
    ,f.field_code_alias
    ,f.field_name_cn
    ,f.field_name_en
    ,f.field_type
    ,ft.name_alias   as  field_type_alias
    ,f.field_length
    ,f.decimal_length
    ,f.format_mark
    ,f.plugin_code
    ,f.plugin_control
    ,f.is_mandatory
    ,f.show_type
    ,f.is_virtual_type
    ,f.default_value
    ,f.fk_schema
    ,f.fk_tab
    ,f.fk_fld_id
    ,f.fk_fld_alias
    ,f.fk_fld_anchor
    ,f.fk_references
    ,f.fk_fld_deleted
    ,f.fk_sql
    ,f.insert_phrase
    ,f.update_phrase
    ,f.remark
    ,f.domain_namespace
    ,f.show_order
    ,f.is_formula 
FROM
	t_field f
	left join t_table t
	on f.table_id = t.tuid
	left join t_field_type ft
	on upper(f.field_type) = upper(ft.name)
WHERE
	f.tuid=${fld:id}
