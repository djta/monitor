update 
	hr_org
set
	org_name = ${fld:org_name}
	,remark = ${fld:remark}
	,org_grade = ${fld:org_grade}
	,show_order = ${fld:show_order}
	,short_name = ${fld:short_name}
	,org_type = ${fld:org_type}
where
	org_id = ${fld:tuid}