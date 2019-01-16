update t_domain
set
	is_enabled = ${fld:flag}
where 
    tuid = ${fld:id}
