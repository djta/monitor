/** 固定时间启用 */
SELECT d.code, d.org_id 
FROM cc_card d 
WHERE d.status = 2 
AND d.startdate IS NOT NULL AND d.startdate <= '${def:date}'::date 
AND d.isgoon = 0 AND d.starttype = 2 
