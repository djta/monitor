select
	fk_schema	as	schema_code
	,fk_tab		as	table_code
	,fk_fld_id		as	field_code
	,fk_fld_alias		as	field_name
	,fk_fld_anchor	as	field_alias
	,fk_references	as	fk_references
from
t_field
where
	tuid = ${fld:pickup_field}
