update t_faq
	set is_delete = '1'
where 
    tuid = ${fld:tuid}
