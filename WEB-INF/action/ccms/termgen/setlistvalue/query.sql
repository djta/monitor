select
        tl.list_name,
        tl.item_show_type,
        tl.list_show_type,
        tl.item_code,
        tl.list_code,
        tls.list_text,
        tls.list_dropdown_value,
        tl.namespace_op
        
from
        t_term_list_score tls
        inner join (select tl.tuid,tl.list_name,tl.list_code,tl.show_type as list_show_type,tti.item_code,tti.type_id,tti.list_show_type as item_show_type,tl.namespace_op From t_term_list tl
        inner join t_term_item tti
		on tl.item_id = tti.tuid) tl
        on tls.list_id = tl.tuid	
        inner join t_term_item_score tis
        on tls.score_item_id = tis.tuid
        inner join t_term_type_score tts
        on tis.score_type_id = tts.tuid
where
        tts.score_term_id = ${fld:score_id}

union

select
        tl.list_name,
        tm.show_type as item_show_type,
        tl.show_type as list_show_type,
        tm.item_code,
        tl.list_code,
        tls.matrix_text as list_text,
        null as list_dropdown_value,
        null as namespace_op
        
from
        t_term_matrix_score tls
        inner join t_term_list tl
        on tls.list_id = tl.tuid
	inner join t_term_item_matrix tm
	on tls.matrix_item_id = tm.tuid
        inner join t_term_item_score tis
        on tls.score_item_id = tis.tuid
        inner join t_term_type_score tts
        on tis.score_type_id = tts.tuid
where
        tts.score_term_id = ${fld:score_id}