select 
	userid as user_id,username  as userlogin
From std_UserInfo o
where
	not exists(select 1 from security.s_user  u where u.user_id=o.userid or o.username=u.userlogin)

and (select count(1) from std_UserInfo a where  o.username=a.username)=1