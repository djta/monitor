select t2.secret,
t2.corp_id,
t1.tenantry_id,
t2.tuid

From hr_staff t1 
join wx_company t2 on t1.tenantry_id=t2.tenantry_id
where
t1.userlogin='${def:user}'
AND
(t2.is_deleted='0' or t2.is_deleted is null)
limit 1