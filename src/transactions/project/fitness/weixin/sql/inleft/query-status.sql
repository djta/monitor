SELECT a.code, a.org_id 
FROM cc_inleft a 
WHERE a.code = (
	SELECT b.code FROM cc_inleft b 
	WHERE b.customercode = ${fld:customercode} 
	AND b.cardcode = ${fld:cardcode} AND b.org_id = ${fld:org_id} 
	ORDER BY b.indate DESC, b.intime DESC LIMIT 1
) 
AND a.org_id = ${fld:org_id} 
AND lefttime IS NULL 
