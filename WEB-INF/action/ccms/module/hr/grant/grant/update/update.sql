UPDATE
	hr_grant
SET
	grant_name = ${fld:grant_name}
	,authuser = ${fld:authuser}
	,start_time = {ts ${fld:start_time}}
	,end_time = {ts ${fld:end_time}}
	,remark = ${fld:remark}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
WHERE
	tuid = ${fld:tuid}