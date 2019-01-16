UPDATE
	os_wfm
SET
	wfm_status = ${fld:wfm_status}
	,remark = ${fld:remark}
	,logo_path = ${fld:logo_path}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
WHERE
	tuid = ${fld:tuid}
