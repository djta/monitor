UPDATE
	hr_team
SET
	team_name=${fld:team_name}
	,leader_id = ${fld:leader_id}
	,remark = ${fld:remark}
WHERE
	team_id	=${fld:tuid}
