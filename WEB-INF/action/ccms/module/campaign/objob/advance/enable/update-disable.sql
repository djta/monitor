update cs_job_template set is_enabled = ${fld:enabled_status}
where 
	tuid = ${fld:id}
