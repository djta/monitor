UPDATE cc_card d 
SET 
	nowcount = (nowcount-1) 
WHERE code = ${fld:cardcode} AND isgoon = 0
AND org_id = ${fld:org_id} AND exists(
	select 1 from cc_cardtype t where t.org_id = ${fld:org_id}
	and t.code = d.cardtype and t.type = 1
)