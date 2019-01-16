select 
	count(1) as testresultnum
from cc_testresult
where org_id = ${fld:org_id} and status = 1
and to_char(created::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and createdby = ${fld:userlogin}
