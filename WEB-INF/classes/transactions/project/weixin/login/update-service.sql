update
wx_service
set 
access_token='${access_token}'
,token_created=now()
,token_expires=7000
where
tuid='${service_id}'