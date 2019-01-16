SELECT
	   f.tuid
      ,f.show_name
      ,/*(select string_agg(f.show_name,'->') from (with RECURSIVE faq as ( 
		select a.tuid,a.show_name,a.superior_id,ARRAY[a.tuid] as path from t_faq a where a.tuid=f.tuid
		union all  
		select k.tuid,k.show_name,k.superior_id,ARRAY[k.tuid] as path  from t_faq k inner join faq c on c.superior_id = k.tuid
		)select show_name from faq order by path ) as f)*/
		'' as superior
      ,f.lable
      ,f.content
      ,f.tuid as faq_file_id
      ,'' as  file_link
      ,f.is_expired
      ,f.create_date
FROM
      t_faq f
WHERE
      f.is_delete = '1'