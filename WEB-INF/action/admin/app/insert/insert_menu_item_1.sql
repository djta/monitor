insert into ${schema}s_menu_item
(menu_item_id, service_id, menu_id, position)
values
(${seq:nextval@${schema}seq_menu_item}, ${seq:currval@${schema}seq_service}, ${seq:currval@${schema}seq_menu}, 3)
