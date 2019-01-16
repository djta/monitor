select 
	tts.tuid
	,tts.term_id
	,tts.userlogin
	,tts.term_score
	,tt.term_name 
	,hs.name
from 
	t_term_score tts 
left join t_term tt 
	on tts.term_id=tt.tuid left join hr_staff hs on tts.userlogin=hs.userlogin
	where 
	1=1
	${filter}
	${orderby}
