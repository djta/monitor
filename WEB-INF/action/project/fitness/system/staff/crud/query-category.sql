SELECT 
	domain_value, domain_text_cn 
FROM t_domain 
WHERE namespace = 'staffCategory' AND is_enabled = '1'
AND (
	case when exists(
		select 1 from hr_staff_skill sk 
		inner join hr_skill k on sk.skill_id = k.skill_id 
		where sk.userlogin = '${def:user}' and k.skill_scope = '4' 
	) then true else domain_value != '4' end 
)
