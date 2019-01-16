select
	hs.userlogin  as id
	,hs.name
from 
	hr_staff hs
	left join ${schema}s_user su on su.userlogin = hs.userlogin
where
  hs.staff_category = '0'
and  not exists (select member_code from  cb_shift_member csm where csm.member_code=hs.userlogin and csm.shift_id = ${fld:shift_id})
and su.enabled=1	
	${filter}

