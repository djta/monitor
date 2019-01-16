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
select
      ${seq:nextval@${schema}seq_default}
    , 'os_entry_comment'
    , ${fld:tuid}
    , 'comments'
    , comments
    , ${fld:owenadvice}
    , '${def:user}'
    , {ts '${def:timestamp}'}
    , '1'
    , (snapshot+1)
from
	os_entry_comment
where
	tuid = ${fld:tuid}
and
	comments <> ${fld:owenadvice}