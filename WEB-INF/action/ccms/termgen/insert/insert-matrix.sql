INSERT INTO
t_term_matrix_score
(
    tuid
    ,score_item_id
    ,matrix_item_id
    ,list_id
    ,matrix_score
    ,matrix_text
    ,remark
)
VALUES
(
      ${seq:nextval@seq_term_score}
    , ${score_item_id}
    , ${matrix_item_id}
    , ${list_id}
    , ${matrix_score}
    , '${matrix_text}'
    , ''
)
