DO
$$
    BEGIN
        IF EXISTS
            (SELECT 1 from pg_roles where rolname = 'postgres')
        THEN
            GRANT USAGE ON SCHEMA flyway_history_schema TO postgres;
            GRANT USAGE ON SCHEMA kaka TO postgres;
        END IF;
    END
$$;

