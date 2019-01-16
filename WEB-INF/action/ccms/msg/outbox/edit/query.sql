SELECT
    s.tuid,
    s.msg_title,
    s.limit_date,
    s.remark,
    s.msg_content,
    s.topic_name,
    nvl(s.send_flag,'0') as send_flag
FROM
	t_msg_outbox s
WHERE
    s.tuid = ${fld:id}
