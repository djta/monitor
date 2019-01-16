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
    ,${fld:tuid}
    ,${fld:skill_id}
)
