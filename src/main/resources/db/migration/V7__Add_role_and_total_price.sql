ALTER TABLE users ADD COLUMN role VARCHAR(255);

UPDATE users SET role = 'CLIENT' WHERE role IS NULL;

ALTER TABLE users ALTER COLUMN role SET NOT NULL;

ALTER TABLE booking ADD COLUMN total_price FLOAT;

UPDATE booking SET total_price = 0.0 WHERE total_price IS NULL;

ALTER TABLE booking ALTER COLUMN total_price SET NOT NULL;