SELECT 
	tuid
	,(CASE WHEN pk_value is not null and pk_value != '' THEN 'true' ELSE 'false' END) AS toflag
	,sender_id AS sender
	,msg_title
	,msg_content
	,send_date
	,pk_value 
FROM t_msg_inbox 
WHERE receiver_id = '${def:user}' 
/**and (pk_value is null or pk_value = '')*/
AND read_flag = '0' AND delete_flag = '0' 
/**
union 

SELECT 
	tuid
	,(CASE WHEN pk_value is not null and pk_value != '' THEN 'true' ELSE 'false' END) AS toflag
	,sender_id AS sender
	,msg_title
	,msg_content
	,send_date
	,pk_value 
FROM t_msg_inbox i 
inner join (
	SELECT 
		max(tuid) as maxid
	FROM t_msg_inbox 
	WHERE receiver_id = '${def:user}' 
	and (pk_value is not null and pk_value != '')
	AND read_flag = '0' AND delete_flag = '0' 
	group by msg_title, pk_value
) t on i.tuid = t.maxid
*/
ORDER BY tuid LIMIT 10