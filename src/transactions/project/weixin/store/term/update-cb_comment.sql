update cb_comment
set term_score_tuid = ${fld:term_score_tuid}
,updated = {ts '${def:timestamp}'}
,updatedby = '${def:user}'
where
comment_id = ${fld:relation_id}