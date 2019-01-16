delete from hr_post_staff
where userlogin = (select userlogin from hr_staff where user_id = ${fld:id})