INSERT	INTO
    t_field
(
	tuid
	, table_id
	, field_code
	, field_code_alias
	, field_name
	, field_name_cn
	, field_name_en
	, field_type
	, field_length
	, decimal_length
	, format_mark
	, plugin_code
	, plugin_control
	, is_mandatory
	, default_value
	, fk_schema
	, fk_tab
	, fk_fld_id
	, fk_fld_alias
	, fk_fld_anchor
	, fk_references
	, fk_sql
	, show_type
	, is_virtual_type
	, insert_phrase
	, update_phrase
	, remark
	, domain_namespace
	, show_order
	, created
	, createdby
	, is_formula 
	, fk_fld_deleted
)
VALUES
(
	${seq:nextval@seq_field}
	, ${fld:table_id}
	, ${fld:field_code}
	, ${fld:field_code_alias}
	, ${fld:field_name_cn}
	, ${fld:field_name_cn}
	, ${fld:field_name_en}
	, ${fld:field_type}
	, ${fld:field_length}
	, ${fld:decimal_length}
	, ${fld:format_mark}
	, ${fld:plugin_code}
	, ${fld:plugin_control}
	, ${fld:is_mandatory}
	, ${fld:default_value}
	, ${fld:fk_schema}
	, ${fld:fk_tab}
	, ${fld:fk_fld_id}
	, ${fld:fk_fld_alias}
	, ${fld:fk_fld_anchor}
	, ${fld:fk_references}
	, ${fld:fk_sql}
	, ${fld:show_type}
	, ${fld:is_virtual_type}
	, ${fld:insert_phrase}
	, ${fld:update_phrase}
	, ${fld:remark}
	, ${fld:domain_namespace}
	, ${fld:show_order}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, '${fld:is_formula}'
	, ${fld:fk_fld_deleted}
)
