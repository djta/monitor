SELECT
    t.tuid
    ,t.domain_value
    ,t.domain_text_cn
    ,t.domain_text_en
    ,t.is_default
    ,t.namespace
    ,t.parent_namespace
    ,t.parent_domain_value
    ,t.remark
    ,t.show_order
    ,t.subject_id
    ,s.subject_name
FROM
	t_domain	t
	left join t_subject s
	on t.subject_id = s.tuid
WHERE
	t.tuid=${fld:id}
