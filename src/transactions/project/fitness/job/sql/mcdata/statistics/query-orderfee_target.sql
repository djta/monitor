select 
	sum(case when c.salemember1 is not null and c.salemember1 != '' then c.normalmoney/2.00 else c.normalmoney end) as orderfee_target
from cc_contract c 
where c.contracttype = 0 and c.type in (0, 5, 7, 9, 11) and c.status >= 2 and c.org_id = ${fld:org_id} 
and to_char(c.createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and c.salemember = ${fld:userlogin}
 		
union all

select 
	sum(c.normalmoney/2) as orderfee_target
from cc_contract c 
where c.contracttype = 0 and c.type in (0, 5, 7, 9, 11) and c.status >= 2 and c.org_id = ${fld:org_id}
and c.salemember1 is not null and c.salemember1 != '' 
and to_char(c.createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and c.salemember1 = ${fld:userlogin}
