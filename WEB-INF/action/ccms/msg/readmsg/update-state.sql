update
	t_msg_inbox
set
	read_flag = '1'
	,read_date = {ts '${def:timestamp}'}
where tuid = ${fld:id}
and nvl(read_flag,'0') = '0'