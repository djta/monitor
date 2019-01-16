select 
	count(1) as sitetargetnum
from cc_contract c 
where contracttype = 0 and type = 2 and status >= 2 and org_id = ${fld:org_id} and source='3'
and to_char(createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and salemember = ${fld:userlogin}

union all

select 
	count(1) as sitetargetnum
from cc_contract c 
where contracttype = 0 and type = 2 and status >= 2 and org_id = ${fld:org_id} and source='3'
and salemember1 is not null and salemember1 != '' 
and to_char(createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and salemember1 = ${fld:userlogin}
