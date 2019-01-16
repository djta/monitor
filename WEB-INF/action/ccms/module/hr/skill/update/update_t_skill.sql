UPDATE
	hr_skill
SET
	skill_name = ${fld:skill_name}
	,skill_scope = ${fld:skill_scope}
	,remark = ${fld:remark}
	,is_default = ${fld:is_default}
WHERE
	skill_id = ${fld:tuid}
