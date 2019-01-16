update
	t_sys_quartz_log
set
	exec_remark = '${exec_remark}'
	,end_time = {ts '${def:timestamp}'}
where
	tuid = ${tuid}