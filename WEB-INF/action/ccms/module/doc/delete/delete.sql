update
	t_document
set
	is_deleted = '1'
where
	tuid = ${fld:id}
