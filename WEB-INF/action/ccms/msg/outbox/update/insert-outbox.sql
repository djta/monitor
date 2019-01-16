UPDATE
	t_msg_outbox
SET
	send_date = {ts '${def:timestamp}'}
	,msg_title = ${fld:msg_title}
	,msg_content = ${fld:msg_content}
	,limit_date = ${fld:limit_date}
	,remark = ${fld:remark}
	,send_flag = ${fld:send_flag}
	,topic_name = ${fld:topic_name}
WHERE
	tuid	=${fld:tuid}
