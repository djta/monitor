SELECT
    t.tuid  as job_id
    , t.job_name
    , (select max(n.tuid) from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node
    , (select max(n.node_name) from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node_name
    , (select max(n.ob_type) from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node_ob_type
    , (select case when max(n.ob_type)='0' then '外呼' 
		when max(n.ob_type)='1' then '短信' 
		when max(n.ob_type)='2' then '邮件' 
		when max(n.ob_type)='3' then 'DM' 
		when max(n.ob_type)='4' then 'EDM' 
		when max(n.ob_type)='5' then '传输到RME'
		when max(n.ob_type)='6' then '传输到DMS'
		when max(n.ob_type)='9' then '等待'
		end from cs_job_node n where n.node_type='0' and n.job_id = t.tuid) as first_node_ob_type_name
FROM
	cs_job t 
WHERE
    t.tuid = ${fld:job_id}
