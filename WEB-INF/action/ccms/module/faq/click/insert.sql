insert into
    t_faq_click
(
    tuid
    ,user_id
    ,faq_id
    ,click_time
)
values
(
    ${seq:nextval@${schema}seq_default}
    ,'${def:user}'
    ,'${fld:tuid}'
    ,{ts '${def:timestamp}'}
)
