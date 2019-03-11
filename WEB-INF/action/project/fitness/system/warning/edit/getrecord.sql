select 	
	tuid,
	(case when m.issystem = 0 then '互动消息'
		 when m.issystem = 1 then '系统消息'
		 when m.issystem = 2 then '合同审批提醒'
	end) as issystem,
	(select name from hr_staff where userlogin = m.senduser and org_id = ${def:org}) as senduser,
	m.content,
	m.sendtime,
	viewtime
from cc_message m
where tuid = ${fld:tuid} and m.org_id = ${def:org}
