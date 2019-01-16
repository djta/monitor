delete from 
    t_faq
where 
    tuid = ${fld:tuid} and is_delete = '1';
