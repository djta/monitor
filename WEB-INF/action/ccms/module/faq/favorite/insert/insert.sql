insert into
    t_faq_favorite
(
    tuid
    ,faq_id
    ,category_id
    ,staff_id
    ,created
)
values
(
	${seq:nextval@${schema}seq_default}
    ,${fld:faq_id}
    ,${fld:category_id}
    ,'${def:user}'
    ,{ts '${def:timestamp}'}
)
