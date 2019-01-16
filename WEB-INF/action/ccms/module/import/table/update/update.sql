UPDATE
	t_import_table
SET
 imp_id			=${fld:imp_id}
,table_id		=${fld:table_id}
,tab_name		=${fld:tab_name}
,parent_id	=${fld:parent_id}
,bpk_field_alias = ${fld:bpk_field_alias}
,if_new_flag		=${fld:if_new_flag}
,remark			=${fld:remark}
,updated		={ts '${def:timestamp}'}
,updatedby  ='${def:user}'
,data_operator_type = ${fld:data_operator_type}
WHERE
	tuid	=${fld:tuid}