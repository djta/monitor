delete from t_term_matrix_score where score_item_id in (select tuid from t_term_item_score where score_type_id in (select tuid from t_term_type_score  where score_term_id = ${fld:score_id}))
