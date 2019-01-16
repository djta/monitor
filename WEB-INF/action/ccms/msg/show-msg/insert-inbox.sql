INSERT INTO
t_msg_inbox
(
    tuid
    ,receiver_id
    ,sender_id
    ,msg_title
    ,msg_content
    ,send_date
    ,read_flag
    ,delete_flag
)
values
(
      ${fld:tuid}
    , '${def:user}'
    , ${fld:sender}
    , ${fld:msg_title}
    , ${fld:msg_content}
    , {ts ${fld:send_date}}
    , '0'
    , '0'
)