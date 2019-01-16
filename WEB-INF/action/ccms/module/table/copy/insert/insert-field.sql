INSERT	INTO
t_field
(
	tuid, table_id, field_code, field_name,field_name_cn, field_name_en, field_length, is_mandatory, 
       default_value, field_desc, fk_schema, fk_tab, fk_fld_id, fk_fld_alias, 
       fk_fld_anchor, fk_references, deleted, show_type, enabled, remark, 
       state, created, createdby, updated, updatedby,  format_mark, 
       field_type, decimal_length, field_code_alias, plugin_code, insert_phrase, 
       update_phrase, show_order, domain_namespace, plugin_control, is_virtual_type ,is_formula,fk_sql
)
select
	${field_id}
	,${table_id}
	,field_code, field_name,field_name_cn, field_name_en, field_length, is_mandatory, 
       default_value, field_desc, fk_schema, fk_tab, fk_fld_id, fk_fld_alias, 
       fk_fld_anchor, fk_references, deleted, show_type, enabled, remark, 
       state, created, createdby, updated, updatedby,  format_mark, 
       field_type, decimal_length, field_code_alias, plugin_code, insert_phrase, 
       update_phrase, show_order, domain_namespace, plugin_control, is_virtual_type ,is_formula,fk_sql
from
	t_field
where
	tuid = ${old_field_id}