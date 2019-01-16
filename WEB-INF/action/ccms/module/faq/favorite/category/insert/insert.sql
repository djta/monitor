INSERT INTO t_faq_category
(
    tuid,
    category_name,
    staff_id
)
VALUES
(
	${seq:nextval@${schema}seq_default},
    ${fld:name},
    '${def:user}'
)
