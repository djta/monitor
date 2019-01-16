update cc_message
set status = 0
where recuser = '${def:user}'
and org_id = ${def:org}

