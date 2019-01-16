select 
    menu_id as parentmenu_id
    , title
  from ${schema}s_menu m
 where app_id = ${fld:app_id}