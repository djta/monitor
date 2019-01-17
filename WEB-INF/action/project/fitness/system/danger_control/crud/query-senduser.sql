select 
	(select name from hr_staff where userlogin = m.senduser and org_id = ${def:org}) as sendusername,
	senduser
from cc_message m
where m.status=1 and m.recuser = '${def:user}' and m.org_id = ${def:org}
group by senduser
