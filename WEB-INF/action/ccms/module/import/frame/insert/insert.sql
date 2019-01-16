insert into
    t_import_rule
(
    tuid
    ,tab_id
    ,rule_name
    ,rule_order
    ,remark
    ,version
    ,filter_type
    ,created
    ,createdby
)
values
(
    ${seq:nextval@seq_import_rule}
    ,${fld:tab_id}
    ,${fld:rule_name}
    ,${fld:rule_order}
    ,${fld:remark}
    ,${fld:version}
    ,${fld:filter_type}
    ,{ts '${def:timestamp}'}
    ,'${def:user}'
)
