SELECT
	max(rs.tuid) as tuid
FROM
	t_import_skill rs
	inner join hr_staff_skill ss
	on rs.skill_id = ss.skill_id
WHERE
	rs.imp_id = ${fld:imp_id}
and 
	ss.userlogin='${def:user}'

