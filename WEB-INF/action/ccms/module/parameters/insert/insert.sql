INSERT	INTO
    t_domain
(
	tuid
	, domain_value
	, domain_text_cn
	, domain_text_en
	, is_default
	, namespace
	, parent_namespace
	, parent_domain_value
	, is_enabled
	, remark
	, show_order
	, subject_id
)
VALUES
(
	${seq:nextval@seq_domain}
	, ${fld:domain_value}
	, ${fld:domain_text_cn}
	, ${fld:domain_text_en}
	, ${fld:is_default}
	, ${fld:namespace}
	, ${fld:parent_namespace}
	, ${fld:parent_domain_value}
	, '1'
	, ${fld:remark}
	, ${fld:show_order}
	, ${fld:subject_id}
)
