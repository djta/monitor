update cc_sms 
set 
	sender = 'sys'
	,send_time = {ts '${def:timestamp}'}
	,status = '${status}'
	,status_desc = '${status_desc}'
	,remark = '${remark}'
	,message_id = '${message_id}' 
where tuid = ${fld:tuid}
