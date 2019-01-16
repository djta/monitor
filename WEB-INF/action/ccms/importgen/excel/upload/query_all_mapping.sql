SELECT
	old_data,
	mapping_value,
	namespace_mapping
FROM
	t_import_mapping
where
	subject_id = ${def:subject}