select 
		field_type 
from 
		t_import_field
where
		${fld:field_type} != (
				select 
						max(field_type) 
				from 
						t_import_field  t
				where tab_id in (
						select 
								tuid 
						from 
								t_import_table 
						where 
								imp_id = (
										select 
												imp_id 
										from 
												t_import_table 
										where 
												tuid = ${fld:tab_id}
												)
									)
				and col_name = ${fld:col_name}
				)
and
	tuid <> ${fld:tuid}

and 
	col_name = ${fld:col_name}