update t_faq
	set is_delete = '0'
where 
    tuid in 
    (with RECURSIVE faq as ( 
		select a.tuid,a.superior_id from t_faq a where a.tuid=${fld:tuid} and a.is_delete = '1'
		union all  
		select k.tuid,k.superior_id from t_faq k inner join faq c on c.superior_id = k.tuid where k.is_delete = '1'
		)select tuid from faq);
