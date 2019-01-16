insert into hr_team_staff
(

   tuid,
   team_id,
   user_id,
   created,
   createdby,
   userlogin
   
)
select ${seq:nextval@seq_hr_team_staff},
	${fld:team_id},
	${fld:member_user},
	{ts '${def:timestamp}'},
	'${def:user}',
	  (select  userlogin from hr_staff where user_id= 	${fld:member_user})
from dual




