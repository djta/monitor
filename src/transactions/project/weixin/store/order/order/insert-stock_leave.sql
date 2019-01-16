insert into ws_leave_stock
(
leave_stock_id
,storehouse_id
,leave_stock_date
,leave_catogry
,description
,state
,created
,createdby
,updated
,updatedby
,snapshot
)
values
(${seq:nextval@seq_ws_leave_stock}
,${fld:storehouse_id}
,{ts '${def:timestamp}'}
,'1'
,''
,'1'
,{ts '${def:timestamp}'}
,'${def:user}'
,null
,null
,0
)