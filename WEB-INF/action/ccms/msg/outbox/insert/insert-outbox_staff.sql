INSERT INTO
t_msg_outbox_item
(
    tuid
    ,outbox_tuid
    ,userlogin
)
VALUES
(
    ${seq:nextval@seq_t_msg_outbox_item}
    , ${seq:currval@seq_msg_box}
    , '${userlogin}'
)
