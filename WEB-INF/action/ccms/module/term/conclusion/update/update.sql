update
	t_term_conclusion
SET
	from_score = ${fld:from_score}
	,to_score = ${fld:to_score}
	,remark = ${fld:remark}
	,show_order = ${fld:show_order}
where
	tuid = ${fld:tuid}
