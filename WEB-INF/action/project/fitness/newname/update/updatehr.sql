update hr_staff set 
  name= ${fld:newname}
where
  user_id = (
  select user_id from hr_staff where userlogin='${def:user}'
  )
