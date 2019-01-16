SELECT
	remark
FROM
	t_term_conclusion
where
	term_id = ${fld:term_id}
and
	from_score <= ${fld:term_score}
and
	to_score >= ${fld:term_score}
order by
	show_order
