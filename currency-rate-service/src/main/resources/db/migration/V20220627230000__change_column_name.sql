--
-- rename columns
--
ALTER TABLE currency_rate RENAME COLUMN source TO currency_rate_provider_id;
ALTER TABLE currency_rate RENAME COLUMN source_created_on TO provider_created_on;