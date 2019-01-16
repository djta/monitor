delete from 
    t_attachment_files
where 
    table_code='t_faq' 
	and pk_value 
	in (select tuid::varchar from t_faq where is_delete = '1');