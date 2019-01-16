update hr_staff set 
  status = ${fld:status},
  updated =  {ts'${def:timestamp}'},
  created=(case when  ${fld:status}=1 then  {ts'${def:timestamp}'}  else  created end) 
where user_id::varchar in (select regexp_split_to_table(${fld:user_id},',') from dual)
and org_id = ${def:org}
