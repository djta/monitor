select
    t.tuid
    ,rule_id
    ,clause_code 
    ,d.field_name_${def:locale}  as  field_name
    ,clause_filter
    ,clause_value
    ,is_node
    ,t.parent_id
    ,logic_type
    ,phrase_name
    ,namespace
    ,t.field_type
    ,t.filter_type
    ,f.filter_type as rule_filter_type
    ,t.version
    ,t.field_id
    ,e.col_name as phrase_name1
from
	 t_import_rule_filter t
	 left join t_import_rule f 	on t.rule_id = f.tuid
	 left join t_import_table b on b.tuid = f.tab_id 
	 left join t_field d 	on (b.table_id=d.table_id and t.clause_code=d.field_code)
	 left join t_import_field e on (e.tuid = t.field_id)
WHERE
	t.tuid=${fld:id}
