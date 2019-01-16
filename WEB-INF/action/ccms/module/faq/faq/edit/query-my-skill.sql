select
	f.skill_id
from
	t_faq_skill f
where
	f.faq_id = ${fld:tuid}