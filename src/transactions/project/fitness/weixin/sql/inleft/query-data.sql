SELECT 
	q.tuid,
	d.code AS cardcode,
	d.customercode,
	c.name AS cust_name,
	d.org_id,
	d.cardtype,
	ct.type,
	c.moneyleft,
	c.moneycash,
	c.moneygift,
	d.status,
	d.startdate,
	d.enddate,
	d.count,
	d.nowcount 
FROM wx_qrcode q 
INNER JOIN cc_card d ON d.code = q.pk_value AND d.isgoon = 0 AND d.org_id = q.org_id 
INNER JOIN cc_cardtype ct on d.cardtype = ct.code and d.org_id = ct.org_id 
INNER JOIN cc_customer c ON c.code = d.customercode AND c.org_id = d.org_id 
WHERE q.tuid = ${scaneid} AND q.data_type = 0 
AND q.status != 0 AND q.resultcode = 0 
AND d.status != 0 AND d.status != 6 
LIMIT 1
