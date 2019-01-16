UPDATE
    t_field
SET
	 field_code         = ${fld:field_code}    
	, field_code_alias  = ${fld:field_code_alias}    
	, field_name        = ${fld:field_name_cn}    
	, field_name_cn     = ${fld:field_name_cn}    
	, field_name_en     = ${fld:field_name_en}    
	, field_type        = ${fld:field_type}    
	, field_length      = ${fld:field_length}  
	, decimal_length    = ${fld:decimal_length}
	, format_mark       = ${fld:format_mark}  
	, plugin_code       = ${fld:plugin_code}  
	, plugin_control    = ${fld:plugin_control}  
	, is_mandatory      = ${fld:is_mandatory}  
	, default_value     = ${fld:default_value} 
	, fk_schema         = ${fld:fk_schema}     
	, fk_tab            = ${fld:fk_tab}        
	, fk_fld_id         = ${fld:fk_fld_id}     
	, fk_fld_alias      = ${fld:fk_fld_alias}  
	, fk_fld_anchor     = ${fld:fk_fld_anchor} 
	, fk_references     = ${fld:fk_references} 
	, fk_fld_deleted    = ${fld:fk_fld_deleted}
	, fk_sql	    = ${fld:fk_sql} 
	, show_type         = ${fld:show_type}     
	, is_virtual_type   = ${fld:is_virtual_type}     
	, insert_phrase     = ${fld:insert_phrase}     
	, update_phrase     = ${fld:update_phrase}     
	, remark            = ${fld:remark}        
	, domain_namespace  = ${fld:domain_namespace}      
	, show_order        = ${fld:show_order}
	, updated = {ts '${def:timestamp}'}
	, updatedby = '${def:user}'
	, is_formula = ${fld:is_formula}
WHERE
	tuid	=${fld:tuid}
