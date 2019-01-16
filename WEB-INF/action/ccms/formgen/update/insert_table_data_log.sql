INSERT INTO t_table_data_log
(
    tuid 
    ,table_code
    ,pk_value
    ,form_id 
    ,field_code
    ,before_value 
    ,after_value 
    ,createdby
    ,created
    ,incident_code
    ,op_type
    ,snapshot
)
VALUES
(
      ${seq:nextval@seq_t_table_data_log}
    , '${table_code}'
    , '${pk_value}'
    , ${form_id}
    , '${field_code}'
    , '${before_value}'
    , '${after_value}'
    , '${def:user}'
    , {ts '${def:timestamp}'}
    , ${fld:incident_code}
    , '1'
    , ${fld:snapshot}
)
