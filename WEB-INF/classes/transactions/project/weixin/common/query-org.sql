select 
			org_id,org_code,org_name,show_order 
				from hr_org t1 where t1.tenantry_id='${tenantryId}' 
				AND pid='${pid}'