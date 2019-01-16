SELECT
	t.tuid  as field_id
	, t.field_name_cn as field_name
FROM
	t_field t
where
	t.tuid = ${fld:field_id}