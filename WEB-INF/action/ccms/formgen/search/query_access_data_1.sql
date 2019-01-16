AND (
	exists (select 1 from hr_staff s inner join hr_org os on s.org_id=os.org_id where s.userlogin=${table}.${owner_field} and 
		exists (select 1 from hr_org os2 ,hr_staff s2 where os2.org_id=s2.org_id and 
			CHARINDEX(os2.org_path ,os.org_path)>1 and s2.userlogin='${def:user}'))
or
	${table}.${owner_field} = '${def:user}'
)