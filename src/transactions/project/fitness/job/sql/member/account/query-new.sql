SELECT c.code, g.memberhead, c.mobile, concat(COALESCE(g.memberhead,''), c.mobile) AS userlogin, c.name, c.org_id 
FROM cc_customer c 
INNER JOIN hr_org g ON g.org_id = c.org_id 
WHERE EXISTS(
	SELECT 1 FROM cc_card d WHERE c.code = d.customercode AND d.isgoon = 0 AND d.org_id = c.org_id 
	AND d.status != 0 AND d.status != 6 
) AND c.status != 0 
AND NOT EXISTS (SELECT 1 FROM hr_staff f WHERE f.userlogin = concat(COALESCE(g.memberhead,''), c.mobile)) 
AND NOT EXISTS (SELECT 1 FROM security.s_user f WHERE f.userlogin = concat(COALESCE(g.memberhead,''), c.mobile)) 
AND c.mobile IS NOT NULL AND c.mobile != ''
