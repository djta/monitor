INSERT INTO
t_term_list_score
(
    tuid
    ,score_item_id
    ,list_id
    ,list_score
    ,list_text
    ,remark
    ,list_dropdown_value
)
VALUES
(
      ${seq:nextval@seq_term_score}
    , ${score_item_id}
    , ${list_id}
    , ${list_score}
    , case when '${list_text}'='""' then '' else '${list_text}' end
    , ''
    , '${list_dropdown_value}'
)
