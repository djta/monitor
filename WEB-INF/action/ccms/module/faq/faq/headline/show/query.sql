SELECT
	t.tuid,
	t.show_name as msg_title
FROM
	t_faq_headline s
	left join t_faq t
	on s.faq_id = t.tuid
WHERE

	(s.from_date <= {d '${def:date}'} or s.from_date is null)
and
	(s.t_date >= {d '${def:date}'} or s.t_date is null)