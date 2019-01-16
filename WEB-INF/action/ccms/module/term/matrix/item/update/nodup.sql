select item_code from t_term_item_matrix
where item_code = ${fld:item_code}
and tuid <> ${fld:tuid}

