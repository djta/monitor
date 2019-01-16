INSERT INTO t_sys_quartz_log
(
	tuid,
	job_name,
	job_cron,
	begin_time,
	end_time,
	next_fire_time,
	exec_remark
) 
VALUES
(
	${tuid},
	'${job_name}',
	'${job_cron}',
	{ts '${def:timestamp}'},
	{ts '${def:timestamp}'},
	{ts '${fire_time}'},
	'${exec_remark}'
)