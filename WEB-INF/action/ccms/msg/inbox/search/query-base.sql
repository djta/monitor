select
	t.tuid,
	f.name as staff_name,
	t.msg_title,
	t.send_date,
	case when nvl(t.read_flag,'0') = '0' then '未读' else '已读' end as read_flag
from t_msg_inbox t 
left join hr_staff f on t.sender_id = f.userlogin 
where nvl(t.delete_flag,'0') = '0'
and t.receiver_id = '${def:user}'
and (t.limit_date <= {d '${def:timestamp}'} or t.limit_date is null) 
/**and t.subject_id = nvl(f.def_subject_id,0) */
	
	${filter}

order by t.send_date desc, t.tuid
