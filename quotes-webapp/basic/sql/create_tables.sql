
CREATE SCHEMA IF NOT EXISTS quote_schema;

CREATE SEQUENCE quote_schema.quote_pk_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

CREATE SEQUENCE quote_schema.subject_pk_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

CREATE TABLE IF NOT EXISTS quote_schema.quote (
    id bigint NOT NULL DEFAULT nextval('quote_schema.quote_pk_seq'::regclass),
    quote_text text NOT NULL,
    attributed_to text NOT NULL,
    CONSTRAINT pk_quote_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS quote_schema.subject (
    id bigint NOT NULL DEFAULT nextval('quote_schema.subject_pk_seq'::regclass),
    subject_text text NOT NULL,
    CONSTRAINT pk_subject_id PRIMARY KEY (id),
    CONSTRAINT unique_subject_text UNIQUE (subject_text)
);

CREATE TABLE IF NOT EXISTS quote_schema.quote_subject (
    quote_id bigint NOT NULL,
    subject_id bigint NOT NULL,
    CONSTRAINT pk_quote_subject PRIMARY KEY (quote_id, subject_id),
    CONSTRAINT fk_quote_subject_quote FOREIGN KEY (quote_id) REFERENCES quote_schema.quote (id),
    CONSTRAINT fk_quote_subject_subject FOREIGN KEY (subject_id) REFERENCES quote_schema.subject (id)
);
