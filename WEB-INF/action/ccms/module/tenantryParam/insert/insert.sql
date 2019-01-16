INSERT	INTO
    t_tenantry_domain
(
	tuid
	, domain_value
	, domain_text_cn
	, domain_text_en
	, is_default
	, namespace
	, parent_namespace
	, parent_domain_value
	, remark
	, show_order
	, tenantry_id
	,created
	,createdby
)
VALUES
(
	${seq:nextval@seq_tenantry_domain}
	, ${fld:domain_value}
	, ${fld:domain_text_cn}
	, ${fld:domain_text_en}
	, ${fld:is_default}
	, ${fld:namespace}
	, ${fld:parent_namespace}
	, ${fld:parent_domain_value}
	, ${fld:remark}
	, ${fld:show_order}
	, ${def:tenantry}
	, {ts '${def:timestamp}'}
	, '${def:user}'
)