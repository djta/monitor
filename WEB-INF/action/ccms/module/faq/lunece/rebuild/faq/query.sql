SELECT
	t.tuid,
	/*(select string_agg(f.show_name,'->') from (with RECURSIVE faq as ( 
		select a.tuid,a.show_name,a.superior_id,ARRAY[a.tuid] as path from t_faq a where a.tuid=t.tuid
		union all  
		select k.tuid,k.show_name,k.superior_id,ARRAY[k.tuid] as path  from t_faq k inner join faq c on c.superior_id = k.tuid
		)select show_name from faq order by path ) as f)*/
	'' as superior,
	t.show_name,
	t.content,
	t.lable,
	case when t.is_expired='1' then '已经过期' else '未过期' end 
	as is_expired,
	t.create_date
FROM
	t_faq t 
WHERE
	(t.is_delete = '0' or t.is_delete is null or t.is_delete = '')
	and t.tenantry_id = ${def:tenantry}
