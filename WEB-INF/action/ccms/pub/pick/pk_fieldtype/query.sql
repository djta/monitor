select 
	name    as  id
	,name_alias as description
	,name
from 
	t_field_type
where 	
	name in('integer','varchar') 
order by 
	name_alias
