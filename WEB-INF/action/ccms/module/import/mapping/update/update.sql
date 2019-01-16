UPDATE
    t_import_mapping
SET
	 old_data = ${fld:old_data}    
	, mapping_value = ${fld:mapping_value}    
	, namespace_mapping = ${fld:namespace_mapping}
WHERE
	tuid = ${fld:tuid}
