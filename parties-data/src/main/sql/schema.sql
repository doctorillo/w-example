CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

CREATE OR REPLACE FUNCTION public.notify_event()
    RETURNS trigger
    LANGUAGE plpgsql
AS
$function$
DECLARE
    table_n      TEXT;
    table_id     uuid;
    ops          TEXT;
    notification json;
BEGIN
    table_n = lower(TG_TABLE_NAME);
    IF TG_OP = 'DELETE' THEN
        table_id = OLD.id;
        ops = TG_OP;
        notification = json_build_object('table', table_n, 'id', table_id, 'action', ops, 'updated', OLD.updated);
    ELSEIF TG_OP = 'TRUNCATE' THEN
        table_id = OLD.id;
        ops = TG_OP;
        notification = json_build_object('table', table_n, 'id', table_id, 'action', ops, 'updated', ('now'::text)::timestamp
            without time zone);
    ELSEIF TG_OP = 'UPDATE' AND NEW.mark_as_delete = TRUE THEN
        table_id = NEW.id;
        ops = 'DELETE';
        notification = json_build_object('table', table_n, 'id', table_id, 'action', ops, 'updated', NEW.updated);
    ELSE
        table_id = NEW.id;
        ops = TG_OP;
        notification = json_build_object('table', table_n, 'id', table_id, 'action', ops, 'updated', NEW.updated);
    END IF;
    PERFORM pg_notify('events', notification::text);
    RETURN NULL;
END;
$function$
    COST 100
    CALLED ON NULL INPUT
    SECURITY INVOKER
    VOLATILE;

ALTER FUNCTION "public"."notify_event" () OWNER TO "doctor";

CREATE OR REPLACE FUNCTION "public"."update_timestamp"()
    RETURNS "trigger"
AS
$BODY$
BEGIN
    NEW.updated = now();
    RETURN NEW;
END;
$BODY$
    LANGUAGE plpgsql
    COST 100
    CALLED ON NULL INPUT
    SECURITY INVOKER
    VOLATILE;

ALTER FUNCTION "public"."update_timestamp" () OWNER TO "doctor";

--- TABLES ---

--- APPS ---

--- SOLVERS ---

CREATE TABLE "public"."solvers"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    data_id          uuid      NOT NULL,
    "email"          TEXT      NOT NULL,
    "password_hash"  TEXT      NOT NULL,
    "preferred_lang" INT       NOT NULL DEFAULT 0,
    "no_login"       bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "solvers_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."solvers"
    OWNER TO "doctor";

CREATE INDEX "solvers_person_idx" ON "public"."solvers"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "solvers_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."solvers"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "solvers_notify_event" ON "public"."solvers" IS NULL;

CREATE TRIGGER "solvers_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."solvers"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "solvers_update_timestamp" ON "public"."solvers" IS NULL;

CREATE TRIGGER "solvers_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."solvers"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "solvers_truncate_notify" ON "public"."solvers" IS NULL;

--- SOLVER SYNCS ---

CREATE TABLE "public"."solver_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    "data_id"        uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "solver_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "solver_syncs_solver_fkey" FOREIGN KEY (data_id) REFERENCES "public"."solvers" ("id") ON DELETE CASCADE,
    CONSTRAINT "solver_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."solver_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "solver_syncs_solver_ref_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

CREATE UNIQUE INDEX "solver_syncs_source_delete_false_uidx" ON "public"."solver_syncs"
    USING btree ("data_id", "source", "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "solver_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."solver_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "solver_syncs_notify_event" ON "public"."solver_syncs" IS NULL;

CREATE TRIGGER "solver_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."solver_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "solver_syncs_update_timestamp" ON "public"."solver_syncs" IS NULL;

CREATE TRIGGER "solver_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."solver_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "solver_syncs_truncate_notify" ON "public"."solver_syncs" IS NULL;

ALTER TABLE "public"."solver_syncs"
    OWNER TO "doctor";

--- APPS ---

CREATE TABLE "public"."apps"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "ident"          INT       NOT NULL,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "apps_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "apps_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."apps" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "apps_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."apps"
    OWNER TO "doctor";

CREATE TRIGGER "apps_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."apps"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "apps_notify_event" ON "public"."apps" IS NULL;

CREATE TRIGGER "apps_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."apps"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "apps_update_timestamp" ON "public"."apps" IS NULL;

CREATE TRIGGER "apps_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."apps"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "apps_truncate_notify" ON "public"."apps" IS NULL;

--- APP CONTEXTS ---

CREATE TABLE "public"."app_contexts"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    data_id          uuid      NOT NULL,
    value_id         int4      NOT NULL,
    "code"           TEXT      NOT NULL,
    CONSTRAINT "app_contexts_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_context_app_fkey" FOREIGN KEY (data_id) REFERENCES "public"."apps" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_contexts_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."app_contexts" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_contexts_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."app_contexts"
    OWNER TO "doctor";

CREATE UNIQUE INDEX "app_contexts_data_value_delete_false_uidx" ON "public"."app_contexts"
    USING btree (data_id, value_id, "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "app_contexts_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."app_contexts"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "app_contexts_notify_event" ON "public"."app_contexts" IS NULL;

CREATE TRIGGER "app_contexts_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."app_contexts"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "app_contexts_update_timestamp" ON "public"."app_contexts" IS NULL;

CREATE TRIGGER "app_contexts_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."app_contexts"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "app_contexts_truncate_notify" ON "public"."app_contexts" IS NULL;

--- APP LANGS ---

CREATE TABLE "public"."app_langs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    data_id          uuid      NOT NULL,
    "lang_id"        int       NOT NULL,
    "active"         BOOLEAN   NOT NULL DEFAULT TRUE,
    CONSTRAINT "app_langs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_langs_app_fkey" FOREIGN KEY (data_id) REFERENCES "public"."apps" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_langs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."app_langs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "app_langs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."app_langs"
    OWNER TO "doctor";

CREATE TRIGGER "app_langs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."app_langs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "app_langs_notify_event" ON "public"."app_langs" IS NULL;

CREATE TRIGGER "app_langs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."app_langs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "app_langs_update_timestamp" ON "public"."app_langs" IS NULL;

CREATE TRIGGER "app_langs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."app_langs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "app_langs_truncate_notify" ON "public"."app_langs" IS NULL;

--- PARTIES ---

CREATE TABLE "public"."parties"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "parties_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "parties_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "parties_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."parties"
    OWNER TO "doctor";

CREATE TRIGGER "parties_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."parties"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "parties_notify_event" ON "public"."parties" IS NULL;

CREATE TRIGGER "parties_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."parties"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "parties_update_timestamp" ON "public"."parties" IS NULL;

CREATE TRIGGER "parties_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."parties"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "parties_truncate_notify" ON "public"."parties" IS NULL;

--- PARTY SYNCS ---

CREATE TABLE "public"."party_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    "data_id"        uuid      NOT NULL,
    "source_id"      int4      NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "party_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "party_syncs_party_fkey" FOREIGN KEY ("data_id") REFERENCES "public"."parties" ("id") ON DELETE CASCADE,
    CONSTRAINT "party_syncs_parent_fkey" FOREIGN KEY ("parent_id") REFERENCES "public"."party_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "party_syncs_solver_fkey" FOREIGN KEY ("solver_id") REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."party_syncs"
    OWNER TO "doctor";

CREATE TRIGGER "party_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."party_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "party_syncs_notify_event" ON "public"."party_syncs" IS NULL;

CREATE TRIGGER "party_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."party_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "party_syncs_update_timestamp" ON "public"."party_syncs" IS NULL;

CREATE TRIGGER "party_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."party_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "party_syncs_truncate_notify" ON "public"."party_syncs" IS NULL;

--- COMPANIES ---

CREATE TABLE "public"."companies"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "data_id"        uuid      NOT NULL,
    "code"           TEXT      NOT NULL,
    "name"           TEXT      NOT NULL,
    CONSTRAINT "companies_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "companies_parties_fkey" FOREIGN KEY (data_id) REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "companies_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."companies" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "companies_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."companies"
    OWNER TO "doctor";

CREATE INDEX "companies_party_idx" ON "public"."companies"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "companies_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."companies"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "companies_notify_event" ON "public"."companies" IS NULL;

CREATE TRIGGER "companies_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."companies"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "companies_update_timestamp" ON "public"."companies" IS NULL;

CREATE TRIGGER "companies_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."companies"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "companies_truncate_notify" ON "public"."companies" IS NULL;

--- PERSONS ---

CREATE TABLE "public"."persons"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    data_id          uuid      NOT NULL,
    "first_name"     TEXT      NOT NULL,
    "last_name"      TEXT      NOT NULL,
    CONSTRAINT "persons_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "persons_parties_fkey" FOREIGN KEY (data_id) REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "persons_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."persons" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "persons_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."persons"
    OWNER TO "doctor";

CREATE INDEX "persons_party_idx" ON "public"."persons"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "persons_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."persons"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "persons_notify_event" ON "public"."persons" IS NULL;

CREATE TRIGGER "persons_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."persons"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "persons_update_timestamp" ON "public"."persons" IS NULL;

CREATE TRIGGER "persons_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."persons"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "persons_truncate_notify" ON "public"."persons" IS NULL;

--- PROVIDERS ---

CREATE TABLE "public"."providers"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "context_id"     uuid      NOT NULL,
    "party_id"       uuid      NOT NULL,
    "is_virtual"     bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "providers_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "providers_context_fkey" FOREIGN KEY ("context_id") REFERENCES "public"."app_contexts" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "providers_parties_fkey" FOREIGN KEY ("party_id") REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "providers_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."providers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "providers_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."providers"
    OWNER TO "doctor";

CREATE INDEX "providers_context_idx" ON "public"."providers"
    USING btree ("context_id" ASC NULLS LAST);

CREATE INDEX "providers_party_idx" ON "public"."providers"
    USING btree ("party_id" ASC NULLS LAST);

CREATE UNIQUE INDEX "providers_ctx_delete_false_uidx" ON "public"."providers"
    USING btree ("context_id", "party_id", "mark_as_delete") WHERE (mark_as_delete = false);


CREATE TRIGGER "providers_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."providers"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "providers_notify_event" ON "public"."providers" IS NULL;

CREATE TRIGGER "providers_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."providers"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "providers_update_timestamp" ON "public"."providers" IS NULL;

CREATE TRIGGER "providers_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."providers"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "providers_truncate_notify" ON "public"."providers" IS NULL;

-- PROVIDER SUPPLIER GROUPS ---

CREATE TABLE "public"."provider_supplier_groups"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    data_id          uuid      NOT NULL,
    "code"           TEXT,
    "notes"          TEXT,
    CONSTRAINT "provider_supplier_groups_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_groups_providers_fkey" FOREIGN KEY (data_id) REFERENCES "public"."providers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_groups_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."provider_supplier_groups" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_groups_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."provider_supplier_groups"
    OWNER TO "doctor";

CREATE INDEX "provider_supplier_groups_provider_idx" ON "public"."provider_supplier_groups"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "provider_supplier_groups_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."provider_supplier_groups"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_supplier_groups_notify_event" ON "public"."provider_supplier_groups" IS NULL;

CREATE TRIGGER "provider_supplier_groups_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."provider_supplier_groups"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "provider_supplier_groups_update_timestamp" ON "public"."provider_supplier_groups" IS NULL;

CREATE TRIGGER "provider_supplier_groups_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."provider_supplier_groups"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_supplier_groups_truncate_notify" ON "public"."provider_supplier_groups" IS NULL;

--- PROVIDER SUPPLIER MEMBERS ---

CREATE TABLE "public"."provider_supplier_members"
(
    "id"                uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"         uuid,
    "solver_id"         uuid,
    "created"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete"    bool      NOT NULL DEFAULT FALSE,
    "group_id"          uuid      NOT NULL,
    "supplier_id"       uuid      NOT NULL,
    "dates_system_life" daterange NOT NULL DEFAULT '[2010-01-01 00:00, 2030-01-01 00:00)'::daterange,
    CONSTRAINT "provider_supplier_members_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_members_group_fkey" FOREIGN KEY ("group_id") REFERENCES "public"."provider_supplier_groups" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_suppliers_supplier_fkey" FOREIGN KEY ("supplier_id") REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_members_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."provider_supplier_members" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_supplier_members_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."provider_supplier_members"
    OWNER TO "doctor";

CREATE INDEX "provider_supplier_members_supplier_idx" ON "public"."provider_supplier_members"
    USING btree ("supplier_id" ASC NULLS LAST);

CREATE TRIGGER "provider_supplier_members_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."provider_supplier_members"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_supplier_members_notify_event" ON "public"."provider_supplier_members" IS NULL;

CREATE TRIGGER "provider_supplier_members_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."provider_supplier_members"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "provider_supplier_members_update_timestamp" ON "public"."provider_supplier_members" IS NULL;

CREATE TRIGGER "provider_supplier_members_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."provider_supplier_members"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_supplier_members_truncate_notify" ON "public"."provider_supplier_members" IS NULL;

--- PROVIDER CUSTOMER GROUPS ---

CREATE TABLE "public"."provider_customer_groups"
(
    "id"              uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"       uuid,
    "solver_id"       uuid,
    "created"         timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"         timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete"  bool      NOT NULL DEFAULT FALSE,
    data_id           uuid      NOT NULL,
    "code"            TEXT,
    "notes"           TEXT,
    "mark_as_default" bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "provider_customer_groups_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_groups_providers_fkey" FOREIGN KEY (data_id) REFERENCES "public"."providers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_groups_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."provider_customer_groups" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_groups_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."provider_customer_groups"
    OWNER TO "doctor";

CREATE TRIGGER "provider_customer_groups_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."provider_customer_groups"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_groups_notify_event" ON "public"."provider_customer_groups" IS NULL;

CREATE TRIGGER "provider_customer_groups_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."provider_customer_groups"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "provider_customer_groups_update_timestamp" ON "public"."provider_customer_groups" IS NULL;

CREATE TRIGGER "provider_customer_groups_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."provider_customer_groups"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_groups_truncate_notify" ON "public"."provider_customer_groups" IS NULL;

--- PROVIDER CUSTOMER GROUP SYNCS ---

CREATE TABLE "public"."provider_customer_group_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    "data_id"        uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "provider_customer_group_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_group_syncs_group_fkey" FOREIGN KEY (data_id) REFERENCES "public"."provider_customer_groups" ("id") ON DELETE CASCADE,
    CONSTRAINT "provider_customer_group_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."provider_customer_group_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_group_syncs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."provider_customer_group_syncs"
    OWNER TO "doctor";

CREATE UNIQUE INDEX "provider_customer_group_syncs_source_delete_false_uidx" ON "public"."provider_customer_group_syncs"
    USING btree ("source", "mark_as_delete", "data_id") WHERE (mark_as_delete = false);

CREATE TRIGGER "provider_customer_group_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."provider_customer_group_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_group_syncs_notify_event" ON "public"."provider_customer_group_syncs" IS NULL;

CREATE TRIGGER "provider_customer_group_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."provider_customer_group_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "provider_customer_group_syncs_update_timestamp" ON "public"."provider_customer_group_syncs" IS NULL;

CREATE TRIGGER "provider_customer_group_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."provider_customer_group_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_group_syncs_truncate_notify" ON "public"."provider_customer_group_syncs" IS NULL;

--- PROVIDER CUSTOMER MEMBERS ---

CREATE TABLE "public"."provider_customer_members"
(
    "id"                uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"         uuid,
    "solver_id"         uuid,
    "created"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete"    bool      NOT NULL DEFAULT FALSE,
    "group_id"          uuid      NOT NULL,
    "customer_id"       uuid      NOT NULL,
    "dates_system_life" daterange NOT NULL DEFAULT '[2010-01-01 00:00, 2030-01-01 00:00)'::daterange,
    CONSTRAINT "provider_customer_members_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_members_group_fkey" FOREIGN KEY ("group_id") REFERENCES "public"."provider_customer_groups" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_members_customer_fkey" FOREIGN KEY ("customer_id") REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_members_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."provider_customer_members" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "provider_customer_members_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."provider_customer_members"
    OWNER TO "doctor";

CREATE INDEX "provider_customer_members_group_idx" ON "public"."provider_customer_members"
    USING btree ("group_id" ASC NULLS LAST);

CREATE INDEX "provider_customer_members_customer_idx" ON "public"."provider_customer_members"
    USING btree ("customer_id" ASC NULLS LAST);

CREATE TRIGGER "provider_customer_members_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."provider_customer_members"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_members_notify_event" ON "public"."provider_customer_members" IS NULL;

CREATE TRIGGER "provider_customer_members_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."provider_customer_members"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "provider_customer_members_update_timestamp" ON "public"."provider_customer_members" IS NULL;

CREATE TRIGGER "provider_customer_members_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."provider_customer_members"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "provider_customer_members_truncate_notify" ON "public"."provider_customer_members" IS NULL;

-- USERS ---

CREATE TABLE "public"."users"
(
    "id"                uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"         uuid,
    "solver_id"         uuid,
    "created"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"           timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete"    bool      NOT NULL DEFAULT FALSE,
    "solver_ref_id"     uuid      NOT NULL,
    "app_id"            uuid      NOT NULL,
    "business_party_id" uuid      NOT NULL,
    "roles"             INT[]     NOT NULL DEFAULT ARRAY [] ::INT[],
    CONSTRAINT "users_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "users_app_fkey" FOREIGN KEY ("app_id") REFERENCES "public"."apps" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "users_business_party_fkey" FOREIGN KEY ("business_party_id") REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "users_solver_fkey" FOREIGN KEY ("solver_id") REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "users_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."users" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "users_solver_ref_fkey" FOREIGN KEY (solver_ref_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."users"
    OWNER TO "doctor";

CREATE INDEX "users_solver_idx" ON "public"."users"
    USING btree ("solver_id" ASC NULLS LAST);

CREATE INDEX "users_app_idx" ON "public"."users"
    USING btree ("app_id" ASC NULLS LAST);

CREATE INDEX "users_business_party_idx" ON "public"."users"
    USING btree ("business_party_id" ASC NULLS LAST);

CREATE TRIGGER "users_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."users"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "users_notify_event" ON "public"."users" IS NULL;

CREATE TRIGGER "users_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."users"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "users_update_timestamp" ON "public"."users" IS NULL;

CREATE TRIGGER "users_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."users"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "users_truncate_notify" ON "public"."users" IS NULL;

--- COUNTRIES ---

CREATE TABLE "public"."countries"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "iso_code_2"     TEXT[2],
    "iso_code_3"     TEXT[3],
    "name"           TEXT      NOT NULL,
    CONSTRAINT "countries_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "countries_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."countries" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "countries_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."countries"
    OWNER TO "doctor";

CREATE TRIGGER "countries_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."countries"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "countries_notify_event" ON "public"."countries" IS NULL;

CREATE TRIGGER "countries_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."countries"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "countries_update_timestamp" ON "public"."countries" IS NULL;

CREATE TRIGGER "countries_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."countries"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "countries_truncate_notify" ON "public"."countries" IS NULL;

--- COUNTRY LABELS ---

CREATE TABLE "public"."country_labels"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "lang_id"        INT       NOT NULL,
    "label"          TEXT      NOT NULL,
    CONSTRAINT "country_labels_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "country_labels_countries_fkey" FOREIGN KEY (data_id) REFERENCES "public"."countries" ("id") ON DELETE CASCADE,
    CONSTRAINT "country_labels_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."country_labels" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "country_labels_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."country_labels"
    OWNER TO "doctor";

CREATE INDEX "country_labels_countries_idx" ON "public"."country_labels"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "country_labels_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."country_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "country_labels_notify_event" ON "public"."country_labels" IS NULL;

CREATE TRIGGER "country_labels_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."country_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "country_labels_update_timestamp" ON "public"."country_labels" IS NULL;

CREATE TRIGGER "country_labels_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."country_labels"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "country_labels_truncate_notify" ON "public"."country_labels" IS NULL;

--- COUNTRY SYNCS ---

CREATE TABLE "public"."country_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "country_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "country_syncs_countries_fkey" FOREIGN KEY (data_id) REFERENCES "public"."countries" ("id") ON DELETE CASCADE,
    CONSTRAINT "country_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."country_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "country_syncs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."country_syncs"
    OWNER TO "doctor";

CREATE UNIQUE INDEX "country_syncs_countries_idx" ON "public"."country_syncs"
    USING btree (data_id ASC NULLS LAST);

CREATE UNIQUE INDEX "country_syncs_source_delete_false_uidx" ON "public"."country_syncs"
    USING btree ("source", "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "country_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."country_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "country_syncs_notify_event" ON "public"."country_syncs" IS NULL;

CREATE TRIGGER "country_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."country_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "country_syncs_update_timestamp" ON "public"."country_syncs" IS NULL;

CREATE TRIGGER "country_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."country_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "country_syncs_truncate_notify" ON "public"."country_syncs" IS NULL;

--- REGIONS ---

CREATE TABLE "public"."regions"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "country_id"     uuid      NOT NULL,
    "iso_code"       TEXT,
    "name"           TEXT      NOT NULL,
    CONSTRAINT "regions_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "regions_country_fkey" FOREIGN KEY ("country_id") REFERENCES "public"."countries" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "regions_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."regions" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "regions_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."regions"
    OWNER TO "doctor";

CREATE INDEX "regions_country_idx" ON "public"."regions"
    USING btree ("country_id" ASC NULLS LAST);

CREATE TRIGGER "regions_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."regions"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "regions_notify_event" ON "public"."regions" IS NULL;

CREATE TRIGGER "regions_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."regions"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "regions_update_timestamp" ON "public"."regions" IS NULL;

CREATE TRIGGER "regions_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."regions"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "regions_truncate_notify" ON "public"."regions" IS NULL;

--- REGION LABELS ---

CREATE TABLE "public"."region_labels"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "lang_id"        INT       NOT NULL,
    "label"          TEXT      NOT NULL,
    CONSTRAINT "region_labels_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "region_labels_region_fkey" FOREIGN KEY (data_id) REFERENCES "public"."regions" ("id") ON DELETE CASCADE,
    CONSTRAINT "region_labels_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."region_labels" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "region_labels_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."region_labels"
    OWNER TO "doctor";

CREATE INDEX "region_labels_region_idx" ON "public"."region_labels"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "region_labels_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."region_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "region_labels_notify_event" ON "public"."region_labels" IS NULL;

CREATE TRIGGER "region_labels_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."region_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "region_labels_update_timestamp" ON "public"."region_labels" IS NULL;

CREATE TRIGGER "region_labels_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."region_labels"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "region_labels_truncate_notify" ON "public"."region_labels" IS NULL;

--- REGION SYNCS ---

CREATE TABLE "public"."region_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "region_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "region_syncs_region_fkey" FOREIGN KEY (data_id) REFERENCES "public"."regions" ("id") ON DELETE CASCADE,
    CONSTRAINT "region_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."region_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "region_syncs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."region_syncs"
    OWNER TO "doctor";

CREATE UNIQUE INDEX "region_syncs_region_idx" ON "public"."region_syncs"
    USING btree (data_id ASC NULLS LAST);

CREATE UNIQUE INDEX "region_syncs_source_delete_false_uidx" ON "public"."region_syncs"
    USING btree ("source", "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "region_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."region_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "region_syncs_notify_event" ON "public"."region_syncs" IS NULL;

CREATE TRIGGER "region_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."region_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "region_syncs_update_timestamp" ON "public"."region_syncs" IS NULL;

CREATE TRIGGER "region_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."region_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "region_syncs_truncate_notify" ON "public"."region_syncs" IS NULL;

--- CITIES ---

CREATE TABLE "public"."cities"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "region_id"      uuid,
    "iso_code"       TEXT,
    "name"           TEXT      NOT NULL,
    "geo_location"   point,
    "has_districts"  bool      NOT NULL DEFAULT FALSE,
    CONSTRAINT "cities_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "cities_region_fkey" FOREIGN KEY ("region_id") REFERENCES "public"."regions" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "cities_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."cities" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "cities_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."cities"
    OWNER TO "doctor";

CREATE INDEX "cities_region_idx" ON "public"."cities"
    USING btree ("region_id" ASC NULLS LAST);

CREATE TRIGGER "cities_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."cities"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "cities_notify_event" ON "public"."cities" IS NULL;

CREATE TRIGGER "cities_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."cities"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "cities_update_timestamp" ON "public"."cities" IS NULL;

CREATE TRIGGER "cities_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."cities"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "cities_truncate_notify" ON "public"."cities" IS NULL;

--- CITY LABELS ---

CREATE TABLE "public"."city_labels"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "lang_id"        INT       NOT NULL,
    "label"          TEXT      NOT NULL,
    CONSTRAINT "city_labels_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_labels_city_fkey" FOREIGN KEY (data_id) REFERENCES "public"."cities" ("id") ON DELETE CASCADE,
    CONSTRAINT "city_labels_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_labels" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_labels_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_labels"
    OWNER TO "doctor";

CREATE INDEX "city_labels_city_idx" ON "public"."city_labels"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "city_labels_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_labels_notify_event" ON "public"."city_labels" IS NULL;

CREATE TRIGGER "city_labels_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_labels_update_timestamp" ON "public"."city_labels" IS NULL;

CREATE TRIGGER "city_labels_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_labels"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_labels_truncate_notify" ON "public"."city_labels" IS NULL;

--- CITY SYNCS ---

CREATE TABLE "public"."city_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "city_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_syncs_city_fkey" FOREIGN KEY (data_id) REFERENCES "public"."cities" ("id") ON DELETE CASCADE,
    CONSTRAINT "city_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_syncs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_syncs"
    OWNER TO "doctor";

CREATE UNIQUE INDEX "city_syncs_city_idx" ON "public"."city_syncs"
    USING btree (data_id ASC NULLS LAST);

CREATE UNIQUE INDEX "city_syncs_source_delete_false_uidx" ON "public"."city_syncs"
    USING btree ("source", "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "city_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_syncs_notify_event" ON "public"."city_syncs" IS NULL;

CREATE TRIGGER "city_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_syncs_update_timestamp" ON "public"."city_syncs" IS NULL;

CREATE TRIGGER "city_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_syncs_truncate_notify" ON "public"."city_syncs" IS NULL;

--- CITY DISTRICTS ---

CREATE TABLE "public"."city_districts"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "city_id"        uuid      NOT NULL,
    "name"           TEXT      NOT NULL,
    CONSTRAINT "city_districts_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_districts_city_fkey" FOREIGN KEY ("city_id") REFERENCES "public"."cities" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_districts_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_districts" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_districts_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_districts"
    OWNER TO "doctor";

CREATE INDEX "city_districts_city_idx" ON "public"."city_districts"
    USING btree ("city_id" ASC NULLS LAST);

CREATE TRIGGER "city_districts_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_districts"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_districts_notify_event" ON "public"."city_districts" IS NULL;

CREATE TRIGGER "city_districts_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_districts"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_districts_update_timestamp" ON "public"."city_districts" IS NULL;

CREATE TRIGGER "city_districts_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_districts"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_districts_truncate_notify" ON "public"."city_districts" IS NULL;

--- CITY DISTRICT LABELS ---

CREATE TABLE "public"."city_district_labels"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    data_id          uuid      NOT NULL,
    "lang_id"        INT       NOT NULL,
    "label"          TEXT      NOT NULL,
    CONSTRAINT "city_district_labels_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_district_labels_city_districts_fkey" FOREIGN KEY (data_id) REFERENCES "public"."city_districts" ("id") ON DELETE CASCADE,
    CONSTRAINT "city_district_labels_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_district_labels" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_district_labels_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_district_labels"
    OWNER TO "doctor";

CREATE INDEX "city_district_labels_city_district_idx" ON "public"."city_district_labels"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "city_district_labels_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_district_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_district_labels_notify_event" ON "public"."city_district_labels" IS NULL;

CREATE TRIGGER "city_district_labels_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_district_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_district_labels_update_timestamp" ON "public"."city_district_labels" IS NULL;

CREATE TRIGGER "city_district_labels_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_district_labels"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_district_labels_truncate_notify" ON "public"."city_district_labels" IS NULL;

--- CITY POINTS ---

CREATE TABLE "public"."city_points"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "city_id"        uuid      NOT NULL,
    "type_id"        INT       NOT NULL,
    "name"           TEXT      NOT NULL,
    "geo_location"   point     NOT NULL,
    CONSTRAINT "city_points_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_points_city_fkey" FOREIGN KEY ("city_id") REFERENCES "public"."cities" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_points_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_points" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_points_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_points"
    OWNER TO "doctor";

CREATE INDEX "city_points_city_idx" ON "public"."city_points"
    USING btree ("city_id" ASC NULLS LAST);

CREATE TRIGGER "city_points_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_points"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_points_notify_event" ON "public"."city_points" IS NULL;

CREATE TRIGGER "city_points_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_points"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_points_update_timestamp" ON "public"."city_points" IS NULL;

CREATE TRIGGER "city_points_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_points"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_points_truncate_notify" ON "public"."city_points" IS NULL;

--- CITY POINT SYNCS ---

CREATE TABLE "public"."city_point_syncs"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    "data_id"        uuid      NOT NULL,
    "source_id"      INT       NOT NULL,
    "source"         jsonb     NOT NULL,
    "active"         bool      NOT NULL DEFAULT TRUE,
    CONSTRAINT "city_point_syncs_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_point_syncs_points_fkey" FOREIGN KEY (data_id) REFERENCES "public"."city_points" ("id") ON DELETE CASCADE,
    CONSTRAINT "city_point_syncs_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_point_syncs" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_point_syncs_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

CREATE UNIQUE INDEX "city_point_syncs_source_delete_false_uidx" ON "public"."city_point_syncs"
    USING btree (data_id, "mark_as_delete") WHERE (mark_as_delete = false);

CREATE TRIGGER "city_point_syncs_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_point_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_point_syncs_notify_event" ON "public"."city_point_syncs" IS NULL;

CREATE TRIGGER "city_point_syncs_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_point_syncs"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_point_syncs_update_timestamp" ON "public"."city_point_syncs" IS NULL;

CREATE TRIGGER "city_point_syncs_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_point_syncs"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_point_syncs_truncate_notify" ON "public"."city_point_syncs" IS NULL;

ALTER TABLE "public"."city_point_syncs"
    OWNER TO "doctor";

--- CITY POINT LABELS ---

CREATE TABLE "public"."city_point_labels"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT false,
    "data_id"        uuid      NOT NULL,
    "lang_id"        INT       NOT NULL,
    "label"          TEXT      NOT NULL,
    CONSTRAINT "city_point_labels_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_point_labels_point_fkey" FOREIGN KEY (data_id) REFERENCES "public"."city_points" ("id") ON DELETE CASCADE,
    CONSTRAINT "city_point_labels_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."city_point_labels" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "city_point_labels_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."city_point_labels"
    OWNER TO "doctor";

CREATE INDEX "city_point_labels_city_idx" ON "public"."city_point_labels"
    USING btree (data_id ASC NULLS LAST);

CREATE TRIGGER "city_point_labels_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."city_point_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_point_labels_notify_event" ON "public"."city_point_labels" IS NULL;

CREATE TRIGGER "city_point_labels_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."city_point_labels"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "city_point_labels_update_timestamp" ON "public"."city_point_labels" IS NULL;

CREATE TRIGGER "city_point_labels_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."city_point_labels"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "city_point_labels_truncate_notify" ON "public"."city_point_labels" IS NULL;

--- PARTY ADDRESS ---

CREATE TABLE "public"."addresses"
(
    "id"             uuid      NOT NULL DEFAULT uuid_generate_v4(),
    "parent_id"      uuid,
    "solver_id"      uuid,
    "created"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "updated"        timestamp NOT NULL DEFAULT ('now'::text) ::timestamp without time zone,
    "mark_as_delete" bool      NOT NULL DEFAULT FALSE,
    "city_id"        uuid      NOT NULL,
    "district_id"    uuid,
    "party_id"       uuid      NOT NULL,
    "address"        INT       NOT NULL,
    "street_name"    TEXT      NOT NULL,
    "internal"       TEXT,
    "location"       TEXT,
    "zip"            TEXT,
    "geo_location"   point,
    CONSTRAINT "addresses_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "addresses_city_fkey" FOREIGN KEY ("city_id") REFERENCES "public"."cities" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "addresses_district_fkey" FOREIGN KEY ("district_id") REFERENCES "public"."city_districts" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "addresses_party_fkey" FOREIGN KEY ("party_id") REFERENCES "public"."parties" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "addresses_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."addresses" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT "addresses_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE
);

ALTER TABLE "public"."addresses"
    OWNER TO "doctor";

CREATE INDEX "addresses_city_idx" ON "public"."addresses"
    USING btree ("city_id" ASC NULLS LAST);

CREATE INDEX "addresses_city_district_idx" ON "public"."addresses"
    USING btree ("district_id" ASC NULLS LAST);

CREATE INDEX "addresses_party_idx" ON "public"."addresses"
    USING btree ("party_id" ASC NULLS LAST);

CREATE TRIGGER "addresses_notify_event"
    AFTER DELETE
        OR INSERT
        OR
        UPDATE
    ON "public"."addresses"
    FOR EACH ROW
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "addresses_notify_event" ON "public"."addresses" IS NULL;

CREATE TRIGGER "addresses_update_timestamp"
    BEFORE
        UPDATE
    ON "public"."addresses"
    FOR EACH ROW
EXECUTE PROCEDURE "update_timestamp"();

COMMENT ON TRIGGER "addresses_update_timestamp" ON "public"."addresses" IS NULL;

CREATE TRIGGER "addresses_truncate_notify"
    BEFORE
        TRUNCATE
    ON "public"."addresses"
    FOR EACH STATEMENT
EXECUTE PROCEDURE "notify_event"();

COMMENT ON TRIGGER "addresses_truncate_notify" ON "public"."addresses" IS NULL;

ALTER TABLE solvers
    ADD CONSTRAINT "solvers_person_fkey" FOREIGN KEY (data_id) REFERENCES "public"."persons" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE NOT VALID;

ALTER TABLE solvers
    ADD CONSTRAINT "solvers_parent_fkey" FOREIGN KEY (parent_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE NOT VALID;

ALTER TABLE solvers
    ADD CONSTRAINT "solvers_solver_fkey" FOREIGN KEY (solver_id) REFERENCES "public"."solvers" ("id") ON
        UPDATE
        NO ACTION ON DELETE CASCADE NOT DEFERRABLE INITIALLY IMMEDIATE NOT VALID;