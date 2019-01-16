INSERT INTO t_table_data_log
(
    tuid 
    ,table_code
    ,pk_value
    ,field_code
    ,before_value 
    ,after_value 
    ,createdby
    ,created
    ,op_type
    ,snapshot
)
VALUES
(
      ${seq:nextval@${schema}seq_default}
    , 'os_entry_comment'
    , ${seq:currval@seq_os_entry_comment}
    , 'comments'
    , null
    , ${fld:owenadvice}
    , '${def:user}'
    , {ts '${def:timestamp}'}
    , '0'
    , 0
)
