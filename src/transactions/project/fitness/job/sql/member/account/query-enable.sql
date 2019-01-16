SELECT concat(COALESCE(g.memberhead,''), c.mobile) AS userlogin, c.code AS customercode 
FROM cc_customer c 
INNER JOIN hr_org g ON g.org_id = c.org_id 
WHERE EXISTS(
	SELECT 1 FROM cc_card d WHERE c.code = d.customercode AND d.isgoon = 0 
	AND c.org_id = d.org_id AND d.status != 0 AND d.status != 6 
) 
AND c.status != 0 
AND EXISTS (
	SELECT 1 FROM hr_staff f 
	INNER JOIN security.s_user u ON f.user_id = u.user_id 
	/**WHERE f.userlogin = concat(COALESCE(g.memberhead,''), c.mobile) AND f.is_member = 1 */
	WHERE f.user_id = c.user_id AND f.is_member = 1 
	AND (f.status = 2 OR u.enabled = 0)
)
