update cs_job_model set is_enabled = ${fld:enabled_status}
where 
	tuid = ${fld:id}
