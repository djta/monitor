INSERT	INTO
    t_field
(
	tuid
	, table_id
	, field_code
	, field_code_alias
	, field_name
	, field_name_cn
	, field_name_en
	, field_type
	, field_length
	, decimal_length
	, is_mandatory
	, show_type
	, remark
	, show_order
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	, ${fld:table_id}
	, ${fld:field_code}
	, ${fld:field_code_alias}
	, ${fld:field_name_cn}
	, ${fld:field_name_cn}
	, ${fld:field_name_en}
	, ${fld:field_type}
	, ${fld:field_length}
	, ${fld:decimal_length}
	, ${fld:is_mandatory}
	, ${fld:show_type}
	, ${fld:remark}
	, ${fld:show_order}
)
