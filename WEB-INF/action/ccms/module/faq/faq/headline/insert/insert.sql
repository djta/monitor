insert into
    t_faq_headline
(
    tuid
    ,faq_id
    ,from_date
    ,t_date
    ,created
    ,cratedby
)
values
(
    ${seq:nextval@${schema}seq_default}
    ,${fld:faq_id}
    ,${fld:from_date}
    ,${fld:t_date}
    ,{ts '${def:timestamp}'}
    ,'${def:user}'
)
