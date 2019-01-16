SELECT
    tuid
    , template_name
    , if_manual_push_flag
    , round_type
    , data_switch_flag
    , data_push_flag
    , reserve_accuracy
    , job_priority
    , job_quota
    , campaign_id
    , subject_id
    , task_duplicate_flag 
    , task_duplicate_scope
    , to_date
    , from_date
    , model_id
FROM
	cs_job_template t
WHERE
	t.tuid=${fld:id}
