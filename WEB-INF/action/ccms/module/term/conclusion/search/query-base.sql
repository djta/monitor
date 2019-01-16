SELECT
    tuid
    ,from_score
    ,remark
    ,show_order
    ,to_score
FROM
     t_term_conclusion
WHERE 1=1
	${filter}
	and
	  term_id = ${fld:term_id}
