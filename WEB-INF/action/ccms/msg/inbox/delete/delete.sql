update  
	t_msg_inbox
set
	delete_flag = '1'
where
    tuid = ${fld:id}
