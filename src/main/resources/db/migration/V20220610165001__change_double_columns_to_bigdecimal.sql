--
-- update float8 columns with decimal
--
ALTER TABLE currency_rate MODIFY COLUMN rate decimal(33,18);
ALTER TABLE currency_rate MODIFY COLUMN reverse_rate decimal(33,18);