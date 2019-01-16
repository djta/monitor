select 
	t.tuid,
	f.name as staff_name,
	t.msg_title,
	t.send_date,
	t.limit_date,
	t.remark
from t_msg_outbox t 
left join hr_staff f on t.sender_id = f.userlogin
where nvl(t.delete_flag,'0') = '0' 
and t.sender_id = '${def:user}' 
and nvl(t.send_flag,'0') = ${fld:send_flag} 
	
	${filter}

order by t.send_date desc, t.tuid
