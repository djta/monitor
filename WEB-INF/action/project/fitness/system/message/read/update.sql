update cc_message
set viewtime = '${def:timestamp}'
where recuser = '${def:user}'
and org_id = ${def:org} and viewtime is null

