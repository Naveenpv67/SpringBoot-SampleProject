ALTER TABLE xelerator_monthly_file
ADD COLUMN year_month TEXT GENERATED ALWAYS AS (date_trunc('month', settlement_date)::date) STORED;

ALTER TABLE xelerator_monthly_file
ADD COLUMN year_month TEXT GENERATED ALWAYS AS (to_char(settlement_date, 'YYYY-MM')) STORED;

