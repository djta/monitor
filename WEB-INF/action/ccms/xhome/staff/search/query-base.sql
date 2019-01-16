select
	'<input type="radio" name="datalist" value="'||f.user_id||'" code="'||f.userlogin||'" />' AS radiolink,
	f.user_id,
	f.userlogin,
	f.name,
	f.mobile,
	f.vc_contact,
''::varchar as skill_name,
	case when f.sex='0' then '男'
	     when f.sex='1' then '女'
	end as sex,
	case when u.enabled = 1 then '正常'
	     when u.enabled = 0 then '被禁止'
	end as state,
	case when u.enabled = 1 then concat(concat('<a href="javascript:void(0);" title="禁用用户" onclick="javascript:onBlock(',f.user_id),')">禁用</a>')
	     when u.enabled = 0 then concat(concat('<a href="javascript:void(0);" title="启用用户" onclick="javascript:onEnable(',f.user_id),')">启用</a>')
	end as state_operator 
from hr_staff f 
inner join ${schema}s_user u on f.user_id = u.user_id 
where 1=1 
${filter}

order by f.user_id desc
