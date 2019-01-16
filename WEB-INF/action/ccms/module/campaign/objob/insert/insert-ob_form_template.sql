INSERT INTO
cs_job_form
(
    tuid
    ,job_id
    ,document_id
    ,form_type
    ,show_order
)
select
      ${seq:nextval@${schema}seq_default}
    , ${seq:currval@seq_cs_job}
    , document_id
    , form_type
    , show_order
from
	cs_job_form_template
where
	template_id=${fld:template_id}
