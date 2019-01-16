select distinct
'<input type="checkbox" class="duoxuan" name="duoxuan" code="'||f.name||'" value="'||f.userlogin||'"/>' as application_id,
	f.user_id,
    f.name
from hr_staff f 
left join  ${schema}s_user  u on  u.user_id=f.user_id
where 1=1 and f.org_id = ${def:org}
 AND f.is_member = 0 
 and f.staff_category = ${fld:group_type}
AND  f.user_id <>'100' --超管隐藏zzn
order by f.user_id desc
