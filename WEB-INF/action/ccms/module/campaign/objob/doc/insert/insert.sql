INSERT INTO
cs_job_form
(
    tuid
    ,job_id
    ,document_id
    ,form_type
    ,show_order
)
VALUES
(
      ${seq:nextval@${schema}seq_default}
    , ${fld:job_id}
    , ${fld:document_id}
    , ${fld:form_type}
    , ${fld:show_order}
)
