select tuid from hr_menu 
where menu_name = ${fld:menu_name} and pid = (select pid from hr_menu where tuid = ${fld:tuid}) 
and is_deleted = 0 and tuid != ${fld:tuid}
