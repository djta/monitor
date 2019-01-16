SELECT
   s.skill_id
FROM
	hr_staff_skill s
WHERE
    s.userlogin = ${fld:id}
