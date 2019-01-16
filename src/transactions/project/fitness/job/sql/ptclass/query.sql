SELECT 
	p.code AS ptpreparecode,
	p.ptid,
	p.ptrestcode,
	p.cardcode,
	p.customercode,
	to_char(p.starttime, 'hh24:mi') AS starttime,
	to_char(p.endtime, 'hh24:mi') AS endtime,
	p.org_id,
	t.ptlevelcode,
	t.ptleftcount,
	t.ptfactfee,
	t.scale 
FROM cc_ptprepare p 
INNER JOIN cc_ptrest t ON t.code = p.ptrestcode AND t.org_id = p.org_id 
WHERE p.org_id = ${fld:org_id} AND p.preparedate <= {d '${def:date}'} - 1 
AND p.status = 1 
