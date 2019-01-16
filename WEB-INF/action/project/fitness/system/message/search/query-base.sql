select 
	concat('<input type="radio" name="messagelist" value="', m.tuid, '"/>') AS checklink,
	(case 
		 when m.issystem = 1 then '系统消息'
		 when m.issystem = 2 then '报警消息'
	end) as issystem,
	(select name from hr_staff where userlogin = m.senduser and org_id = ${def:org}) as senduser,
	m.content,
	m.sendtime,
	viewtime
from cc_message m
where m.status=1 and m.recuser = '${def:user}' and m.org_id = ${def:org}
${filter}
${orderby}
