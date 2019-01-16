select (case when ${fld:datetime}::timestamp<'${def:timestamp}'::timestamp 
then 1 else 0 end) as status from dual 