select tuid from hr_menu 
where menu_name = ${fld:menu_name} and is_deleted = 0 
and tuid != ${fld:tuid} 
and tenantry_id = ${def:tenantry} and org_id = ${def:org} 
