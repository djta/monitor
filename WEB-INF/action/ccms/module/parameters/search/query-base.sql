SELECT
    t.tuid
    ,s.subject_name
    ,t.namespace
    ,t.domain_value
    ,t.domain_text_cn
    ,t.domain_text_en
    ,case when t.is_default='1' then '是' else '否' end as is_default
    ,case when t.is_enabled='1' then '启用' else '禁用' end as is_enabled
    ,parent_namespace
    ,parent_domain_value
FROM
	t_domain t
	left join t_subject s
	on t.subject_id = s.tuid
WHERE
	1 = 1

${filter}
${orderby}
