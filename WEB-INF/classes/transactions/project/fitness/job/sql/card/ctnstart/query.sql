SELECT b.code, b.org_id  
FROM cc_card b 
INNER JOIN cc_card a ON a.code = b.code AND a.isgoon = 1 AND b.org_id = a.org_id 
LEFT JOIN cc_customer r ON r.code = b.customercode AND r.org_id = b.org_id 
INNER JOIN cc_cardtype c ON b.cardtype = c.code 
WHERE b.isgoon = 0 AND b.status IN (1,6) 
AND (
	a.starttype = 0 OR a.starttype = 1 
	OR (a.starttype = 2 AND a.startdate <= '${def:date}'::date) 
) 
AND (
	(c.type = 0 AND '${def:date}'::date > b.enddate) /** 时效卡 */
	OR (c.type = 1 AND (b.nowcount <= 0 OR '${def:date}'::date > b.enddate)) /** 计次卡 */
	OR (c.type = 2 AND (20.0 > (COALESCE(r.moneycash,0.0) + COALESCE(r.moneygift,0.0)) OR '${def:date}'::date > b.enddate)) /** 基金卡 */
)
