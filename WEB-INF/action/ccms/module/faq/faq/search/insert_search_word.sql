insert into
    t_faq_search_word
(
    tuid
    ,search_key
    ,search_time
    ,user_id
)
values
(
   	${seq:nextval@seq_faq_search_word}
    ,${fld:show_name}
    ,{ts '${def:timestamp}'}
    ,'${def:user}'
)
