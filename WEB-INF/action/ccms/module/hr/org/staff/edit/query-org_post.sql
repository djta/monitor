SELECT
   s.org_post_id
FROM
	hr_post_staff s
WHERE
    s.userlogin = ${fld:id}
