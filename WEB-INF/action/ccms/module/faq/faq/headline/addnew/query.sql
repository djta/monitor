SELECT
    f.tuid as faq_id
    ,f.show_name
    ,h.tuid
    ,h.from_date
    ,h.t_date
FROM
	t_faq f
	left join t_faq_headline h
	on f.tuid = h.faq_id
WHERE
      f.tuid = ${fld:tuid}