SELECT q.id AS quote_id, s.id as subject_id, q.quote_text, q.attributed_to, s.subject_text
  FROM quote_schema.quote AS q
  LEFT OUTER JOIN quote_schema.quote_subject AS qs
    ON q.id = qs.quote_id
  LEFT OUTER JOIN quote_schema.subject AS s
    ON qs.subject_id = s.id;
