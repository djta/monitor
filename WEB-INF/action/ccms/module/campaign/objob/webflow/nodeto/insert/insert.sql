INSERT	INTO
    cs_job_node_to
(
	tuid
	, node_id
	, next_node
)
VALUES
(
	${seq:nextval@seq_cs_job_node_to}
	, ${fld:node_id}
	, ${fld:next_node}
)
