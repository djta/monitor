INSERT	INTO
t_import_table
(
tuid
,imp_id
,table_id
,tab_name
,parent_id
,bpk_field_alias
,if_new_flag
,remark
,created
,createdby
,data_operator_type
)
VALUES
(
${seq:nextval@seq_import_table}
,${fld:imp_id}
,${fld:table_id}
,${fld:tab_name}
,${fld:parent_id}
,${fld:bpk_field_alias}
,case when ${fld:if_new_flag} is null then '1'
			else ${fld:if_new_flag} end
,${fld:remark}
,{ts '${def:timestamp}'}
,'${def:user}'
,${fld:data_operator_type}
)