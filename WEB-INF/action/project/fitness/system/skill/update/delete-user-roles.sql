delete from ${schema}s_user_role
where
	user_id in (select ss.user_id from hr_staff_skill ss where ss.skill_id=${fld:tuid})