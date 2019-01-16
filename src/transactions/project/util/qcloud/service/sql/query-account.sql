select 
	tuid,
	secretid,
	secretkey,
	signature,
	expire_time 
from et_account 
where status = 1 limit 1
