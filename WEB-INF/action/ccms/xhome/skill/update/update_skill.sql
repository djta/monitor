UPDATE hr_skill 
SET
	skill_name = ${fld:skill_name}
	,remark = ${fld:remark}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}' 
WHERE skill_id = ${fld:tuid}
