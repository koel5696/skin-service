ALTER TABLE routine_group
    ADD CONSTRAINT uk_routine_group_user_skin_result
        UNIQUE (user_id, skin_result_id);
