delete  from
	hr_authority_list 
where 
	entity_id in (
		select 
			tuid 
		from 
			hr_authority_entity  
		where 
			authority_id=${fld:id})