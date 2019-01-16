update
	os_currentstep
set
	owner = '${owner}'
where
	entry_id = ${fld:__wfentry_id__}
and
	step_id = ${step_id}