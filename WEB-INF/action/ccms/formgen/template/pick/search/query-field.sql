select
	t.fk_schema	as	schema_code
	,t.fk_tab		as	table_code
	,t.fk_fld_id		as	field_code
	,t.fk_fld_alias		as	field_name
	,t.fk_fld_anchor	as	field_alias
	,t.fk_references	as	fk_references
	,t.fk_sql   as  fk_sql
	,t.fk_fld_deleted as delete_field
from
	t_field t
where
	t.tuid = ${fld:pickup_field}
