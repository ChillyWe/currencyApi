--
-- create tables
--
CREATE TABLE currency_rate (
        id int8 NOT NULL AUTO_INCREMENT,
        base varchar(255) NULL,
        currency_quote_name_id varchar(255) NULL,
        created_on timestamp NULL,
        rate float8 NULL,
        reverse_rate float8 NULL,
        source varchar(255) NULL,
        source_created_on timestamp NULL,
        version int4 NOT NULL,
        CONSTRAINT currency_rate_pkey PRIMARY KEY (id)
);

CREATE TABLE currency_quote_name (
        id varchar(40) NOT NULL,
        name varchar(255) NULL,
        source varchar(255) NULL,
        version int4 NOT NULL,
        CONSTRAINT currency_quote_name_pkey PRIMARY KEY (id)
);
--
-- foreign keys
--
ALTER TABLE currency_rate
ADD CONSTRAINT fk_currency_quote_name_currency_rate_id FOREIGN KEY (currency_quote_name_id) REFERENCES currency_quote_name(id);
