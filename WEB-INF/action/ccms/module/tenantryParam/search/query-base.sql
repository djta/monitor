SELECT
    tuid
    ,namespace
    ,domain_value
    ,domain_text_cn
    ,domain_text_en
    ,case when is_default='1' then '是' else '否' end as is_default
    ,parent_namespace
    ,parent_domain_value
    ,remark
FROM
	t_tenantry_domain 
WHERE
	tenantry_id = ${def:tenantry}

${filter}
${orderby}
