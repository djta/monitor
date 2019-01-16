select tuid from hr_menu_btn 
where menu_id = ${fld:menu_id} and btn_name = ${fld:btn_name} and is_deleted = 0 
and tuid != ${fld:tuid}
