INSERT INTO
t_term_score
(
    tuid
    ,term_id
    ,term_score
    ,userlogin
    ,term_date
    ,start_time
    ,end_time
    ,relation_id
)
VALUES
(
      ${tuid}
    , ${fld:term_id}
    , ${fld:term_score}
    , '${def:user}'
    , {d '${def:date}'}
    , {ts ${fld:start_time}}
    , {ts '${def:timestamp}'}
    , ${fld:relation_id}
)
