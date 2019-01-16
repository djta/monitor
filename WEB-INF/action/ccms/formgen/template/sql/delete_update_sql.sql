update  
	${table}
set 
	${delete_field} = '1'
where 
    ${bpk_field} = ${fld:__pk_value__}
