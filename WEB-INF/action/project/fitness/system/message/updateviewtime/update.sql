update cc_message
set viewtime = '${def:timestamp}'
where tuid::varchar in (select regexp_split_to_table(${fld:tuid},',') from dual)
and org_id = ${def:org} and viewtime is null

