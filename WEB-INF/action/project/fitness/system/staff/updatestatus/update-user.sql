update ${schema}s_user  set 
  enabled = ${fld:status}
where user_id::varchar in (select regexp_split_to_table(${fld:user_id},',') from dual)
