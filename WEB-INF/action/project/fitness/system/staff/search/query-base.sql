select
 	concat('<input type="checkbox"  data-am-ucheck name="datalist" code="',f.userlogin,'" value="',f.user_id,'','" >') as radiolink,
	f.user_id,
    f.userlogin,
    f.name,
    f.name_en,
    f.mobile,
    f.contact as vc_contact,
    f.status as i_statu,
     (case when f.status=0 then '离职'      when f.status=3 then '已删除'  else '在职' end) as i_status ,
   	(
   		case when exists(
   			select 1 from hr_staff_skill fk 
	    	inner join hr_skill k on fk.skill_id = k.skill_id 
	    	where fk.user_id = f.user_id and k.skill_name = '区域总经理'
   		) then (
   			'<a class="region" title="点击设置区域总经理权限" code="'||f.user_id||'" href="javascript:void(0)">'||(
   			select string_agg(k.skill_name, ';') 
	    	from hr_staff_skill fk 
	    	inner join hr_skill k on fk.skill_id = k.skill_id 
	    	where fk.user_id = f.user_id
	    	)||'</a>'
   		) else (
   			select string_agg(k.skill_name, ';') 
	    	from hr_staff_skill fk 
	    	inner join hr_skill k on fk.skill_id = k.skill_id 
	    	where fk.user_id = f.user_id
   		) end
   	) as skill_name,
   	
   	f.salary,
 		(select
   		(case  	when skill_scope='0' then '前台'
   					 when skill_scope='1' then 'PT'
   					 when skill_scope='2' then 'MC'
   					 when skill_scope='3' then '水吧'
   					 when skill_scope='4' then '系统管理员'
   					 when skill_scope='5' then '财务'
   					 when skill_scope='6' then '人力'
   					 when skill_scope='7' then '运营'end)
   		from hr_staff_skill fk  
	    	inner join hr_skill k on fk.skill_id = k.skill_id
	    	where f.user_id=fk.user_id
	    	) AS staff_category,
   	
    replace(f.remark,'''','') AS remark,
    
   ( case when u.enabled='0' then '已删除' else '正常' end) as user_status
from hr_staff f 
inner join  ${schema}s_user  u on  u.user_id=f.user_id 
where 1=1 
 AND f.is_member = 0 
 and (
 	(${fld:org_id} is not null and org_id = ${fld:org_id}) 
	or (${fld:org_id} is null and org_id = ${def:org})
 ) and f.status!=3
and (
	case when exists(
		select 1 from hr_staff_skill sk 
		inner join hr_skill k on sk.skill_id = k.skill_id 
		where sk.userlogin = '${def:user}' and k.skill_scope = '4' 
	) then true else not exists(
		select 1 from hr_staff_skill sk 
		inner join hr_skill k on sk.skill_id = k.skill_id 
		where k.skill_scope = '4' and sk.user_id = f.user_id 
	) end 
) 

${filter}
AND  f.user_id != 100 
order by f.user_id desc
