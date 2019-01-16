INSERT INTO
t_msg_outbox
(
    tuid
    ,sender_id
    ,send_date
    ,msg_title
    ,msg_content
    ,delete_flag
    ,limit_date
    ,remark
    ,send_flag
    ,topic_name
)
VALUES
(
      ${seq:nextval@seq_msg_box}
    , '${def:user}'
    , {ts '${def:timestamp}'}
    , ${fld:msg_title}
    , ${fld:msg_content}
    , '0'
    , ${fld:limit_date}
    , ${fld:remark}
    , ${fld:send_flag}
    , ${fld:topic_name}
)
