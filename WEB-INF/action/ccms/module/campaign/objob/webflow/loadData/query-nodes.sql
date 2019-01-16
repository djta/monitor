SELECT
	f.tuid as node_id
	, f.node_name
	, f.node_width
	, f.node_height
	, f.node_x
	, f.node_y
FROM
	cs_job_node f
WHERE
	f.job_id = ${fld:job_id}