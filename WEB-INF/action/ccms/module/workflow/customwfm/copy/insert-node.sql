INSERT INTO os_wfm_node
(
	tuid
	, wfm_id
	, node_name
	, node_type
	, document_id
	, remark
	, step_type
	, old_status
	, status
	, child_wfm_id
	, countersign_type
	, countersign_per
	, countersign_post
)
VALUES
(
	  ${fld:tuid}
	, ${fld:wfm_id}
	, ${fld:node_name}
	, ${fld:node_type}
	, ${fld:document_id}
	, ${fld:remark}
	, ${fld:step_type}
	, ${fld:old_status}
	, ${fld:status}
	, ${fld:child_wfm_id}
	, ${fld:countersign_type}
	, ${fld:countersign_per}
	, ${fld:countersign_post}
)
