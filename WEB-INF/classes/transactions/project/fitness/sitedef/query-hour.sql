select 
	to_char((case when MIN(opening_date) is null then '00:00:00' else MIN(opening_date) end)::time, 'hh24') as minhour,
	to_char((case when MAX(closed_date) is null then '00:00:00' else MAX(closed_date) end)::time, 'hh24') as maxhour
from cc_sitedef
where status = 1 and org_id = ${def:org} 
and (case when ${fld:sitetype} is null then 1=1 else sitetype = ${fld:sitetype} end)