INSERT INTO
cs_job_form_template
(
    tuid
    ,template_id
    ,document_id
    ,form_type
    ,show_order
)
VALUES
(
      ${seq:nextval@seq_default}
    , ${fld:template_id}
    , ${fld:document_id}
    , ${fld:form_type}
    , ${fld:show_order}
)
