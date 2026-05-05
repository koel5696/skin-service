UPDATE product
SET product_usage_time = 'AM'
WHERE category = 'SUN_CARE';

ALTER TABLE product
    MODIFY COLUMN product_usage_time VARCHAR(10) NOT NULL;

ALTER TABLE product
    ADD CONSTRAINT chk_product_sun_care_usage_time
        CHECK (category <> 'SUN_CARE' OR product_usage_time = 'AM');
