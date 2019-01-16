INSERT	INTO
    t_import_mapping
(
	tuid
	, old_data
	, mapping_value
	, namespace_mapping
	, subject_id
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	, ${fld:old_data}
	, ${fld:mapping_value}
	, ${fld:namespace_mapping}
	, ${def:subject}
)
