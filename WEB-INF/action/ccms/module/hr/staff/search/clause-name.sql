and
(
	t.name like ${fld:name}
or
	t.userlogin like ${fld:name}
or  
	t.user_pinyin like ${fld:name}
)