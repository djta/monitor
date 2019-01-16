SELECT
(case when skill_scope='1'  then     
    	concat('<input type="checkbox"  data-am-ucheck name="datalist" 
		code="1,8" value="',t.team_id,'','" >')
	   when skill_scope='2'  then     
    	concat('<input type="checkbox"  data-am-ucheck name="datalist" 
		code="2" value="',t.team_id,'','" >')
else 
	   	concat('<input type="checkbox"  data-am-ucheck name="datalist" 
		code="0" value="',t.team_id,'','" >')
	   end) as checkboxlist,
   	t.team_id,
	t.team_name,
	(select  hr_staff.name from hr_staff where   t.leader_id::integer= hr_staff.user_id and org_id = ${def:org}) as leader_name,
	(select  count(*) from  hr_team_staff 
	where  t.team_id = hr_team_staff.team_id 
    GROUP BY  hr_team_staff.team_id) as num,
		(case when skill_scope='0' then '客服'   when skill_scope='2' then '会籍'  else  '教练'end)  as type,
	  (case when t.status='0' then '禁用'   else  '启用'end)  as status,
	t.remark,
	
	
(select  string_agg(hr_staff.name,',') from hr_staff
LEFT JOIN hr_team_staff  ON hr_team_staff.userlogin = hr_staff.userlogin and hr_staff.org_id = ${def:org}
where t.team_id= hr_team_staff.team_id) as member
FROM hr_team t 
LEFT JOIN hr_team_staff ts ON t.team_id = ts.team_id 
LEFT JOIN hr_staff s ON ts.userlogin = s.userlogin 
WHERE 1 = 1 and t.org_id=${def:org} and s.org_id = ${def:org}
${filter}
GROUP BY t.team_id, t.team_name
${orderby}
