select 
	sum(pttotalcount) as marketclassnum
from cc_ptrest
where pttype = 1 and ptid = ${fld:userlogin} and org_id = ${fld:org_id}
and to_char(created::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')

