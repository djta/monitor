SELECT
    s.msg_title,
    s.msg_content
FROM
	t_msg_inbox s
WHERE
    s.tuid = ${fld:id}
