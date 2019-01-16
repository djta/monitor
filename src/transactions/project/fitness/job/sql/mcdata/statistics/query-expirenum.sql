select 
	count(card.code) as expirenum
from cc_card card
left join cc_customer cust on card.customercode = cust.code and card.org_id = cust.org_id
where card.isgoon = 0 and card.status = 6 and card.org_id = ${fld:org_id}
and to_char(card.enddate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and cust.mc = ${fld:userlogin}

