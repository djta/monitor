select 

From  std_UserInfo t1 where not exists(select 1 from hr_staff t2 where t1.username=t2.userlogin

)
and username!='admin'
AND
not exists(select 1 from "security".s_user t3 where t1.userid=t3.user_id)
