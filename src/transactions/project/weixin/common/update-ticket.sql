update
wx_service
set
jsapi_ticket='${ticket}'
,ticket_created=now()
,ticket_expires=7000
where
tuid='${tuid}'
