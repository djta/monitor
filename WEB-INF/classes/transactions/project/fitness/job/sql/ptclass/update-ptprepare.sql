UPDATE cc_ptprepare 
SET 
	status = 3,
	updatedby = 'sys',
	updated = {ts '${def:timestamp}'} 
WHERE code = ${fld:ptpreparecode} AND org_id = ${fld:org_id} 
