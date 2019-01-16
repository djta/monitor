UPDATE cc_card a 
SET 
	passwd = b.passwd
	,startdate = (
		CASE WHEN a.starttype = 0 THEN b.enddate + interval '1 day' ELSE a.startdate END
	)
	,enddate = (
		CASE WHEN a.starttype = 0 THEN b.enddate + concat((1 + a.totalday + COALESCE(a.giveday,0)),' day')::interval ELSE a.enddate END
	)
	,status = 1
	,isgoon = 0 
FROM cc_card b 
WHERE a.code = ${fld:code} AND a.org_id = ${fld:org_id} AND a.code = b.code AND a.org_id = b.org_id 
AND a.isgoon = 1 AND b.isgoon = -1 
