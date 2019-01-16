select 
	sum(ptleftcount) as ptinventorynum
from cc_ptrest
where ptid = ${fld:userlogin} and org_id = ${fld:org_id}
