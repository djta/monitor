select 
	count(1) as msnum 
from cc_guest_visit gv
where gv.org_id = ${fld:org_id} and gv.mc = ${fld:userlogin}
and to_char(gv.visitdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
and (gv.preparecode is null or gv.preparecode = '') and gv.status!=0

