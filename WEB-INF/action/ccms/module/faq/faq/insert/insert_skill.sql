insert into
    t_faq_skill
(
    tuid
    ,faq_id
    ,skill_id
)
values
(
    ${seq:nextval@${schema}seq_default}
    ,${seq:currval@seq_faq}
    ,${fld:skill_id}
)
