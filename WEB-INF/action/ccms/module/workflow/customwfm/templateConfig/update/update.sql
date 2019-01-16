UPDATE
	os_wfm
SET
	table_id = ${fld:table_id}
	,wfm_name = ${fld:wfm_name}
	,wfm_status = ${fld:wfm_status}
	,is_template = ${fld:is_template}
	,remark	= ${fld:remark}
	,wfm_type = ${fld:wfm_type}
	,logo_path = ${fld:logo_path}
	,xml_value = ?
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
WHERE
	tuid = ${fld:wfm_id}
