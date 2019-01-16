UPDATE
	t_import_rule
SET
rule_name 					=${fld:rule_name}
,rule_order					=${fld:rule_order}
,remark							=${fld:remark}
,version						=${fld:version}
,filter_type					=${fld:filter_type}
,tab_id							=${fld:tab_id}
,updated						={ts '${def:timestamp}'}
,updatedby  				='${def:user}'
WHERE
	tuid	=${fld:tuid}