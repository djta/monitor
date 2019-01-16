update 
    t_subject
set
    is_deleted = '1'
where 
    tuid = ${fld:id}
