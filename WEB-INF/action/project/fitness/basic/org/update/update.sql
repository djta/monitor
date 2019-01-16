update hr_org 
set
	org_name = ${fld:org_name}
	,remark = ${fld:remark}
	,show_order = ${fld:show_order}
	,short_name = ${fld:short_name}
where org_id = ${fld:tuid}