update ${schema}s_user set 
  fname= ${fld:newname}
where
  user_id = (
  select user_id from ${schema}s_user where userlogin='${def:user}'
  )
