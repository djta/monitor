select 
	count(1) as renewalnum
from cc_contract
where (salemember = ${fld:userlogin} or salemember1 = ${fld:userlogin})
and status!=0 and (type = 7 or type = 9 and type = 11)
and to_char(createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and org_id = ${fld:org_id}
