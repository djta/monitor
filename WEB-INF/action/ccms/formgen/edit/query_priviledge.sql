SELECT
	case when max(nvl(is_update,'0'))='1' then 'inline' else 'none'  end as  is_can_update,
	case when max(nvl(is_delete,'0'))='1' then 'inline' else 'none'  end as  is_can_delete
FROM
	t_form_skill f
	inner join t_skill s
	on f.skill_id = s.skill_id
WHERE
    f.form_id = ${fld:form_id}
and exists
    (select ss.skill_id from t_staff_skill ss where ss.userlogin='${def:user}' and ss.skill_id=s.skill_id)