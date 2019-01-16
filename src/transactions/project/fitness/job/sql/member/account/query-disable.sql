SELECT f.user_id, f.userlogin 
FROM hr_staff f 
INNER JOIN hr_org g ON g.org_id = f.org_id 
/**INNER JOIN cc_customer c ON f.userlogin = concat(COALESCE(g.memberhead,''), c.mobile) */
INNER JOIN cc_customer c ON f.user_id = c.user_id 
WHERE f.is_member = 1 AND f.status != 0 AND f.status != 2 /** 0离职 1正常 2删除 */
AND NOT EXISTS (
	SELECT 1 FROM cc_card d 
	WHERE d.customercode = c.code AND d.isgoon = 0 AND c.org_id = d.org_id 
	AND d.status != 0 AND d.status != 6
)
