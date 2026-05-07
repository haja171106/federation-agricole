DROP TABLE IF EXISTS presence CASCADE;
DROP TABLE IF EXISTS activity_invited_collectivity CASCADE;
DROP TABLE IF EXISTS activity_target_position CASCADE;
DROP TABLE IF EXISTS activity CASCADE;
DROP TABLE IF EXISTS collectivity_transaction CASCADE;
DROP TABLE IF EXISTS member_payment CASCADE;
DROP TABLE IF EXISTS membership_fee CASCADE;
DROP TABLE IF EXISTS contribution CASCADE;
DROP TABLE IF EXISTS mobile_money_account CASCADE;
DROP TABLE IF EXISTS bank_account CASCADE;
DROP TABLE IF EXISTS account CASCADE;
DROP TABLE IF EXISTS federation_mandate_position CASCADE;
DROP TABLE IF EXISTS federation_mandate CASCADE;
DROP TABLE IF EXISTS mandate_position CASCADE;
DROP TABLE IF EXISTS mandate CASCADE;
DROP TABLE IF EXISTS adhesion CASCADE;
DROP TABLE IF EXISTS calendar CASCADE;
DROP TABLE IF EXISTS collectivity CASCADE;
DROP TABLE IF EXISTS federation CASCADE;
DROP TABLE IF EXISTS member CASCADE;

DROP DATABASE IF EXISTS agro_federation_db;
DROP USER IF EXISTS agro_federation_user;

CREATE USER agro_federation_user WITH PASSWORD '12345678';
CREATE DATABASE agro_federation_db OWNER agro_federation_user;
\c agro_federation_db;
GRANT ALL PRIVILEGES ON DATABASE agro_federation_db TO agro_federation_user;

-- ================================================================
-- 2. TABLES (using VARCHAR for IDs instead of INT)
-- ================================================================

CREATE TABLE member (
    id               VARCHAR(50)   PRIMARY KEY,
    last_name        VARCHAR(100)  NOT NULL,
    first_name       VARCHAR(100)  NOT NULL,
    birth_date       DATE          NOT NULL,
    gender           VARCHAR(10)   NOT NULL CHECK (gender IN ('MASCULINE', 'FEMININE')),
    address          TEXT          NOT NULL,
    profession       VARCHAR(150)  NOT NULL,
    phone            VARCHAR(20)   NOT NULL UNIQUE,
    email            VARCHAR(150)  NOT NULL UNIQUE,
    membership_date  DATE          NOT NULL DEFAULT CURRENT_DATE,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE federation (
    id                                 VARCHAR(50)   PRIMARY KEY,
    name                               VARCHAR(300)   NOT NULL,
    reverse_contribution_percentage    NUMERIC(5,2)   NOT NULL DEFAULT 10.00
                                         CHECK (reverse_contribution_percentage BETWEEN 0 AND 100),
    created_at                         TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at                         TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE collectivity (
    id                      VARCHAR(50)   PRIMARY KEY,
    number                  VARCHAR(50)   NOT NULL UNIQUE,
    name                    VARCHAR(200)  NOT NULL UNIQUE,
    agricultural_specialty  VARCHAR(150)  NOT NULL,
    city                    VARCHAR(100)  NOT NULL,
    creation_date           DATE          NOT NULL,
    opening_authorization   BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE adhesion (
    id                  VARCHAR(50)   PRIMARY KEY,
    member_id           VARCHAR(50)   NOT NULL REFERENCES member(id),
    collectivity_id     VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    sponsor_id          VARCHAR(50)   REFERENCES member(id),
    adhesion_date       DATE          NOT NULL DEFAULT CURRENT_DATE,
    resignation_date    DATE,
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    resignation_reason  TEXT,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dates_adhesion CHECK (
        resignation_date IS NULL OR resignation_date > adhesion_date
    )
);

CREATE TABLE mandate (
    id                VARCHAR(50)   PRIMARY KEY,
    collectivity_id   VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    year              INTEGER       NOT NULL,
    start_date        DATE          NOT NULL,
    end_date          DATE          NOT NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_mandate_collectivity_year UNIQUE (collectivity_id, year),
    CONSTRAINT chk_mandate_dates CHECK (end_date > start_date)
);

CREATE TABLE mandate_position (
    id          VARCHAR(50)   PRIMARY KEY,
    mandate_id  VARCHAR(50)   NOT NULL REFERENCES mandate(id),
    member_id   VARCHAR(50)   NOT NULL REFERENCES member(id),
    position    VARCHAR(30)   NOT NULL CHECK (
                    position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER',
                                 'SECRETARY','CONFIRMED_MEMBER','JUNIOR_MEMBER')
                ),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_member_mandate UNIQUE (mandate_id, member_id)
);

-- Partial index: only one holder per specific position per mandate
CREATE UNIQUE INDEX uq_specific_position_mandate
    ON mandate_position (mandate_id, position)
    WHERE position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY');

CREATE TABLE federation_mandate (
    id              VARCHAR(50)   PRIMARY KEY,
    federation_id   VARCHAR(50)   NOT NULL REFERENCES federation(id),
    start_date      DATE          NOT NULL,
    end_date        DATE          NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_federation_mandate_duration CHECK (
        end_date = start_date + INTERVAL '2 years'
    )
);

CREATE TABLE federation_mandate_position (
    id                    VARCHAR(50)   PRIMARY KEY,
    federation_mandate_id VARCHAR(50)   NOT NULL REFERENCES federation_mandate(id),
    member_id             VARCHAR(50)   NOT NULL REFERENCES member(id),
    position              VARCHAR(30)   NOT NULL CHECK (
                              position IN ('PRESIDENT','VICE_PRESIDENT',
                                           'TREASURER','SECRETARY')
                          ),
    created_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_position_federation_mandate UNIQUE (federation_mandate_id, position),
    CONSTRAINT uq_member_federation_mandate UNIQUE (federation_mandate_id, member_id)
);

CREATE TABLE account (
    id                  VARCHAR(50)   PRIMARY KEY,
    owner_type          VARCHAR(15)   NOT NULL CHECK (
                            owner_type IN ('COLLECTIVITY','FEDERATION')
                        ),
    collectivity_id     VARCHAR(50)   REFERENCES collectivity(id),
    federation_id       VARCHAR(50)   REFERENCES federation(id),
    type                VARCHAR(15)   NOT NULL CHECK (
                            type IN ('CASH','BANK','MOBILE_MONEY')
                        ),
    balance             NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    balance_date        DATE          NOT NULL DEFAULT CURRENT_DATE,
    currency            VARCHAR(5)    NOT NULL DEFAULT 'MGA',
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_owner CHECK (
        (owner_type = 'COLLECTIVITY' AND collectivity_id IS NOT NULL AND federation_id IS NULL)
        OR
        (owner_type = 'FEDERATION'   AND federation_id IS NOT NULL AND collectivity_id IS NULL)
    )
);

-- Only one cash account per entity
CREATE UNIQUE INDEX uq_cash_collectivity
    ON account (collectivity_id) WHERE type = 'CASH' AND owner_type = 'COLLECTIVITY';
CREATE UNIQUE INDEX uq_cash_federation
    ON account (federation_id)   WHERE type = 'CASH' AND owner_type = 'FEDERATION';

CREATE TABLE bank_account (
    id              VARCHAR(50)   PRIMARY KEY,
    account_id      VARCHAR(50)   NOT NULL UNIQUE REFERENCES account(id),
    holder          VARCHAR(200)  NOT NULL,
    bank_name       VARCHAR(30)   NOT NULL CHECK (
                        bank_name IN ('BRED','MCB','BMOI','BOA','BGFI',
                                      'AFG','ACCES_BANQUE','BAOBAB','SIPEM')
                    ),
    account_number  CHAR(23)      NOT NULL UNIQUE
                        CHECK (account_number ~ '^\d{23}$'),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE mobile_money_account (
    id          VARCHAR(50)   PRIMARY KEY,
    account_id  VARCHAR(50)   NOT NULL UNIQUE REFERENCES account(id),
    holder      VARCHAR(200)  NOT NULL,
    service     VARCHAR(20)   NOT NULL CHECK (
                    service IN ('ORANGE_MONEY','MVOLA','AIRTEL_MONEY')
                ),
    phone       VARCHAR(20)   NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE contribution (
    id                          VARCHAR(50)   PRIMARY KEY,
    collectivity_id             VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    member_id                   VARCHAR(50)   NOT NULL REFERENCES member(id),
    amount                      NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    collection_date             DATE          NOT NULL DEFAULT CURRENT_DATE,
    payment_mode                VARCHAR(20)   NOT NULL CHECK (
                                    payment_mode IN ('CASH','BANK_TRANSFER','MOBILE_MONEY')
                                ),
    type                        VARCHAR(15)   NOT NULL CHECK (
                                    type IN ('MONTHLY','YEARLY','ONE_TIME')
                                ),
    reason                      TEXT,
    federation_reverse_amount   NUMERIC(12,2),
    created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_reason_one_time CHECK (
        type != 'ONE_TIME' OR reason IS NOT NULL
    )
);

-- NEW MISSING TABLES
CREATE TABLE membership_fee (
    id               VARCHAR(50)   PRIMARY KEY,
    collectivity_id  VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    eligible_from    DATE          NOT NULL,
    frequency        VARCHAR(20)   NOT NULL,
    amount           NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    label            VARCHAR(200),
    status           VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE member_payment (
    id                 VARCHAR(50)   PRIMARY KEY,
    member_id          VARCHAR(50)   NOT NULL REFERENCES member(id),
    membership_fee_id  VARCHAR(50)   NOT NULL REFERENCES membership_fee(id),
    account_credited_id VARCHAR(50)  NOT NULL REFERENCES account(id),
    amount             NUMERIC(12,2) NOT NULL,
    payment_mode       VARCHAR(20)   NOT NULL,
    creation_date      DATE          NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE collectivity_transaction (
    id                  VARCHAR(50)   PRIMARY KEY,
    collectivity_id     VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    member_debited_id   VARCHAR(50)   NOT NULL REFERENCES member(id),
    account_credited_id VARCHAR(50)   NOT NULL REFERENCES account(id),
    amount              NUMERIC(12,2) NOT NULL,
    payment_mode        VARCHAR(20)   NOT NULL,
    creation_date       DATE          NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE activity (
    id                VARCHAR(50)   PRIMARY KEY,
    collectivity_id   VARCHAR(50)   REFERENCES collectivity(id),
    type              VARCHAR(25)   NOT NULL CHECK (
                          type IN ('GENERAL_MEETING','JUNIOR_TRAINING',
                                   'EXCEPTIONAL','FEDERATION')
                      ),
    title             VARCHAR(300)  NOT NULL,
    description       TEXT,
    date              TIMESTAMP     NOT NULL,
    mandatory         BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE activity_target_position (
    activity_id   VARCHAR(50)   NOT NULL REFERENCES activity(id) ON DELETE CASCADE,
    position      VARCHAR(30)   NOT NULL CHECK (
                      position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER',
                                   'SECRETARY','CONFIRMED_MEMBER','JUNIOR_MEMBER')
                  ),
    PRIMARY KEY (activity_id, position)
);

CREATE TABLE activity_invited_collectivity (
    activity_id     VARCHAR(50) NOT NULL REFERENCES activity(id) ON DELETE CASCADE,
    collectivity_id VARCHAR(50) NOT NULL REFERENCES collectivity(id),
    PRIMARY KEY (activity_id, collectivity_id)
);

CREATE TABLE calendar (
    id                          VARCHAR(50)   PRIMARY KEY,
    collectivity_id             VARCHAR(50)   NOT NULL REFERENCES collectivity(id),
    year                        INTEGER       NOT NULL,
    general_meeting_rule        VARCHAR(100)  NOT NULL,
    junior_training_rule        VARCHAR(100)  NOT NULL,
    created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_calendar_collectivity_year UNIQUE (collectivity_id, year)
);

CREATE TABLE presence (
    id              VARCHAR(50)   PRIMARY KEY,
    activity_id     VARCHAR(50)   NOT NULL REFERENCES activity(id),
    member_id       VARCHAR(50)   NOT NULL REFERENCES member(id),
    status          VARCHAR(10)   NOT NULL CHECK (status IN ('PRESENT','ABSENT','EXCUSED')),
    is_visitor      BOOLEAN       NOT NULL DEFAULT FALSE,
    absence_reason  TEXT,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_presence_activity_member UNIQUE (activity_id, member_id),
    CONSTRAINT chk_reason_excused CHECK (
        status != 'EXCUSED' OR absence_reason IS NOT NULL
    )
);

-- Grant privileges on all tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO agro_federation_user;



-- ================================================================
-- MIGRATION SCRIPT
-- Aligns init.sql with API requirements
-- Uses VARCHAR for IDs (not UUID)
-- English version
-- ================================================================

-- 1. Update member table: change gender values to MALE/FEMALE
ALTER TABLE member DROP CONSTRAINT IF EXISTS member_gender_check;
ALTER TABLE member ADD CONSTRAINT member_gender_check
    CHECK (gender IN ('MALE', 'FEMALE'));

-- 2. Update mandate_position: align position values with API
ALTER TABLE mandate_position DROP CONSTRAINT IF EXISTS mandate_position_position_check;
ALTER TABLE mandate_position ADD CONSTRAINT mandate_position_position_check
    CHECK (position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY','SENIOR','JUNIOR'));

DROP INDEX IF EXISTS uq_specific_position_mandate;
CREATE UNIQUE INDEX uq_specific_position_mandate
    ON mandate_position (mandate_id, position)
    WHERE position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER','SECRETARY');

-- 3. Remove sponsor_id from adhesion (replaced by adhesion_referent)
ALTER TABLE adhesion DROP COLUMN IF EXISTS sponsor_id;

-- 4. NEW TABLE: adhesion_referent (supports multiple referents per adhesion)
CREATE TABLE IF NOT EXISTS adhesion_referent (
    id           VARCHAR(50)   PRIMARY KEY,
    adhesion_id  VARCHAR(50)   NOT NULL REFERENCES adhesion(id) ON DELETE CASCADE,
    referent_id  VARCHAR(50)   NOT NULL REFERENCES member(id),
    relation     VARCHAR(100),
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_adhesion_referent UNIQUE (adhesion_id, referent_id)
);

-- 5. Update existing membership_fee table (add missing columns if needed)
-- First check if table exists, then alter
DO $$
BEGIN
    -- Add statut column if not exists
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'membership_fee' AND column_name = 'statut') THEN
        ALTER TABLE membership_fee ADD COLUMN statut VARCHAR(10) NOT NULL DEFAULT 'ACTIVE';
        ALTER TABLE membership_fee ADD CONSTRAINT membership_fee_statut_check 
            CHECK (statut IN ('ACTIVE','INACTIVE'));
    END IF;
    
    -- Add label column if not exists
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'membership_fee' AND column_name = 'label') THEN
        ALTER TABLE membership_fee ADD COLUMN label VARCHAR(200);
    END IF;
    
    -- Modify frequency check constraint
    ALTER TABLE membership_fee DROP CONSTRAINT IF EXISTS membership_fee_frequency_check;
    ALTER TABLE membership_fee ADD CONSTRAINT membership_fee_frequency_check
        CHECK (frequency IN ('WEEKLY','MONTHLY','YEARLY','ONE_TIME','ANNUALLY','PUNCTUALLY'));
        
    -- Modify status column if exists (rename from status to statut)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'membership_fee' AND column_name = 'status') THEN
        ALTER TABLE membership_fee RENAME COLUMN status TO statut;
    END IF;
END $$;

-- 6. Update member_payment table (align payment_mode values)
ALTER TABLE member_payment DROP CONSTRAINT IF EXISTS member_payment_payment_mode_check;
ALTER TABLE member_payment ADD CONSTRAINT member_payment_payment_mode_check
    CHECK (payment_mode IN ('CASH','MOBILE_BANKING','BANK_TRANSFER','MOBILE_MONEY'));

-- 7. Update collectivity_transaction table (align payment_mode values)
ALTER TABLE collectivity_transaction DROP CONSTRAINT IF EXISTS collectivity_transaction_payment_mode_check;
ALTER TABLE collectivity_transaction ADD CONSTRAINT collectivity_transaction_payment_mode_check
    CHECK (payment_mode IN ('CASH','MOBILE_BANKING','BANK_TRANSFER','MOBILE_MONEY'));

-- 8. Grant privileges
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO agro_federation_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO agro_federation_user;
