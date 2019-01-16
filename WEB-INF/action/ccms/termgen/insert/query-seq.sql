SELECT
	(case when ${fld:score_id} is null then ${seq:nextval@seq_term_score} else ${fld:score_id} end) as seq
FROM
	dual