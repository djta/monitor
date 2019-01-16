delete from
    t_faq_favorite
where
    faq_id = ${fld:faq_id}
    and staff_id = '${def:user}' 