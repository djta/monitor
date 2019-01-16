SELECT
    f.tuid
    ,f.show_name as show_name_faq
    ,f.lable
    ,f.content
    ,f.tuid as faq_file_id
    ,'' as  file_link
    ,busi_type_id
    ,tenantry_id
    ,start_date
    ,end_date
    ,is_expired
    ,is_bulletin
    ,create_date
    ,/*(with RECURSIVE faq as ( 
		select a.tuid,a.show_name,a.superior_id from t_faq a where a.tuid=f.tuid
		union all  
		select k.tuid,k.show_name,k.superior_id  from t_faq k inner join faq c on c.superior_id = k.tuid
		)select string_agg(show_name,'<-') from faq ) 
	*/ '' as superior
FROM
      t_faq f
WHERE
      f.tuid = ${fld:tuid}