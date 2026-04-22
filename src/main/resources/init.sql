-- ================================================================
-- 0. DROP TABLES (for clean re-execution)
-- ================================================================

-- DROP TABLES (reverse order of dependencies)
DROP TABLE IF EXISTS presence CASCADE;
DROP TABLE IF EXISTS activite_collectivite_invitee CASCADE;
DROP TABLE IF EXISTS activite_poste_vise CASCADE;
DROP TABLE IF EXISTS activite CASCADE;
DROP TABLE IF EXISTS calendrier CASCADE;
DROP TABLE IF EXISTS collectivity_transaction CASCADE;
DROP TABLE IF EXISTS member_payment CASCADE;
DROP TABLE IF EXISTS membership_fee CASCADE;
DROP TABLE IF EXISTS cotisation CASCADE;
DROP TABLE IF EXISTS compte_mobile_money CASCADE;
DROP TABLE IF EXISTS compte_bancaire CASCADE;
DROP TABLE IF EXISTS compte CASCADE;
DROP TABLE IF EXISTS poste_mandat_federation CASCADE;
DROP TABLE IF EXISTS mandat_federation CASCADE;
DROP TABLE IF EXISTS poste_mandat CASCADE;
DROP TABLE IF EXISTS mandat CASCADE;
DROP TABLE IF EXISTS adhesion CASCADE;
DROP TABLE IF EXISTS collectivite CASCADE;
DROP TABLE IF EXISTS federation CASCADE;
DROP TABLE IF EXISTS member CASCADE;

-- ================================================================
-- 1. DATABASE & USER
-- ================================================================
DROP DATABASE IF EXISTS agro_federation_db;
DROP USER IF EXISTS agro_federation_user;

CREATE USER agro_federation_user WITH PASSWORD '12345678';
CREATE DATABASE agro_federation_db OWNER agro_federation_user;
\c agro_federation_db;
GRANT ALL PRIVILEGES ON DATABASE agro_federation_db TO agro_federation_user;

-- ================================================================
-- 2. TABLES (using INT instead of UUID)
-- ================================================================

CREATE TABLE member (
    id               SERIAL PRIMARY KEY,
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
    id                                 SERIAL PRIMARY KEY,
    name                               VARCHAR(300)   NOT NULL,
    reverse_contribution_percentage    NUMERIC(5,2)   NOT NULL DEFAULT 10.00
                                         CHECK (reverse_contribution_percentage BETWEEN 0 AND 100),
    created_at                         TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at                         TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE collectivity (
    id                      SERIAL PRIMARY KEY,
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
    id                  SERIAL PRIMARY KEY,
    member_id           INT           NOT NULL REFERENCES member(id),
    collectivity_id     INT           NOT NULL REFERENCES collectivity(id),
    sponsor_id          INT           REFERENCES member(id),
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
    id                SERIAL PRIMARY KEY,
    collectivity_id   INT           NOT NULL REFERENCES collectivity(id),
    year              INTEGER       NOT NULL,
    start_date        DATE          NOT NULL,
    end_date          DATE          NOT NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_mandate_collectivity_year UNIQUE (collectivity_id, year),
    CONSTRAINT chk_mandate_dates CHECK (end_date > start_date)
);

CREATE TABLE mandate_position (
    id          SERIAL PRIMARY KEY,
    mandate_id  INT           NOT NULL REFERENCES mandate(id),
    member_id   INT           NOT NULL REFERENCES member(id),
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
    id              SERIAL PRIMARY KEY,
    federation_id   INT           NOT NULL REFERENCES federation(id),
    start_date      DATE          NOT NULL,
    end_date        DATE          NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_federation_mandate_duration CHECK (
        end_date = start_date + INTERVAL '2 years'
    )
);

CREATE TABLE federation_mandate_position (
    id                    SERIAL PRIMARY KEY,
    federation_mandate_id INT           NOT NULL REFERENCES federation_mandate(id),
    member_id             INT           NOT NULL REFERENCES member(id),
    position              VARCHAR(30)   NOT NULL CHECK (
                              position IN ('PRESIDENT','VICE_PRESIDENT',
                                           'TREASURER','SECRETARY')
                          ),
    created_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_position_federation_mandate UNIQUE (federation_mandate_id, position),
    CONSTRAINT uq_member_federation_mandate UNIQUE (federation_mandate_id, member_id)
);

CREATE TABLE account (
    id                  SERIAL PRIMARY KEY,
    owner_type          VARCHAR(15)   NOT NULL CHECK (
                            owner_type IN ('COLLECTIVITY','FEDERATION')
                        ),
    collectivity_id     INT           REFERENCES collectivity(id),
    federation_id       INT           REFERENCES federation(id),
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
    id              SERIAL PRIMARY KEY,
    account_id      INT           NOT NULL UNIQUE REFERENCES account(id),
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
    id          SERIAL PRIMARY KEY,
    account_id  INT           NOT NULL UNIQUE REFERENCES account(id),
    holder      VARCHAR(200)  NOT NULL,
    service     VARCHAR(20)   NOT NULL CHECK (
                    service IN ('ORANGE_MONEY','MVOLA','AIRTEL_MONEY')
                ),
    phone       VARCHAR(20)   NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE contribution (
    id                          SERIAL PRIMARY KEY,
    collectivity_id             INT           NOT NULL REFERENCES collectivity(id),
    member_id                   INT           NOT NULL REFERENCES member(id),
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
    id               SERIAL PRIMARY KEY,
    collectivity_id  INT           NOT NULL REFERENCES collectivity(id),
    eligible_from    DATE          NOT NULL,
    frequency        VARCHAR(20)   NOT NULL,
    amount           NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    label            VARCHAR(200),
    status           VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE member_payment (
    id                 SERIAL PRIMARY KEY,
    member_id          INT           NOT NULL REFERENCES member(id),
    membership_fee_id  INT           NOT NULL REFERENCES membership_fee(id),
    account_credited_id INT          NOT NULL REFERENCES account(id),
    amount             NUMERIC(12,2) NOT NULL,
    payment_mode       VARCHAR(20)   NOT NULL,
    creation_date      DATE          NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE collectivity_transaction (
    id                  SERIAL PRIMARY KEY,
    collectivity_id     INT           NOT NULL REFERENCES collectivity(id),
    member_debited_id   INT           NOT NULL REFERENCES member(id),
    account_credited_id INT           NOT NULL REFERENCES account(id),
    amount              NUMERIC(12,2) NOT NULL,
    payment_mode        VARCHAR(20)   NOT NULL,
    creation_date       DATE          NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE activity (
    id                SERIAL PRIMARY KEY,
    collectivity_id   INT           REFERENCES collectivity(id),
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
    activity_id   INT           NOT NULL REFERENCES activity(id) ON DELETE CASCADE,
    position      VARCHAR(30)   NOT NULL CHECK (
                      position IN ('PRESIDENT','VICE_PRESIDENT','TREASURER',
                                   'SECRETARY','CONFIRMED_MEMBER','JUNIOR_MEMBER')
                  ),
    PRIMARY KEY (activity_id, position)
);

CREATE TABLE activity_invited_collectivity (
    activity_id     INT NOT NULL REFERENCES activity(id) ON DELETE CASCADE,
    collectivity_id INT NOT NULL REFERENCES collectivity(id),
    PRIMARY KEY (activity_id, collectivity_id)
);

CREATE TABLE calendar (
    id                          SERIAL PRIMARY KEY,
    collectivity_id             INT           NOT NULL REFERENCES collectivity(id),
    year                        INTEGER       NOT NULL,
    general_meeting_rule        VARCHAR(100)  NOT NULL,
    junior_training_rule        VARCHAR(100)  NOT NULL,
    created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_calendar_collectivity_year UNIQUE (collectivity_id, year)
);

CREATE TABLE presence (
    id              SERIAL PRIMARY KEY,
    activity_id     INT           NOT NULL REFERENCES activity(id),
    member_id       INT           NOT NULL REFERENCES member(id),
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
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO agro_federation_user;

-- ================================================================
-- 3. TEST DATA (SPECIFIC INSERTS)
-- ================================================================

-- Federation
INSERT INTO federation (id, name, reverse_contribution_percentage) VALUES
    (1, 'National Federation of Agricultural Collectivities of Madagascar', 10.00);

-- Members (12 to open 1 collectivity)
INSERT INTO member (id, last_name, first_name, birth_date, gender, address, profession, phone, email, membership_date) VALUES
    (1, 'Rakoto', 'Jean', '1985-03-12', 'MASCULINE', 'Antananarivo', 'Rice Farmer', '+261320000001', 'jean.rakoto@mail.mg', '2024-01-01'),
    (2, 'Rabe', 'Marie', '1990-07-22', 'FEMININE', 'Antananarivo', 'Market Gardener', '+261320000002', 'marie.rabe@mail.mg', '2024-01-01'),
    (3, 'Andry', 'Pascal', '1988-11-05', 'MASCULINE', 'Antananarivo', 'Livestock Farmer', '+261320000003', 'pascal.andry@mail.mg', '2024-01-01'),
    (4, 'Rasoa', 'Claudine', '1992-04-18', 'FEMININE', 'Antananarivo', 'Beekeeper', '+261320000004', 'claudine.rasoa@mail.mg', '2024-01-01'),
    (5, 'Randriana', 'Hery', '1980-09-30', 'MASCULINE', 'Antananarivo', 'Agriculturist', '+261320000005', 'hery.randriana@mail.mg', '2024-01-01'),
    (6, 'Ramiandrisoa', 'Lanto', '1995-02-14', 'FEMININE', 'Antananarivo', 'Nursery Grower', '+261320000006', 'lanto.rami@mail.mg', '2024-01-01'),
    (7, 'Randria', 'Toky', '1987-06-25', 'MASCULINE', 'Antananarivo', 'Fish Farmer', '+261320000007', 'toky.randria@mail.mg', '2024-01-01'),
    (8, 'Rasolofo', 'Fanja', '1993-08-09', 'FEMININE', 'Antananarivo', 'Market Gardener', '+261320000008', 'fanja.rasolofo@mail.mg', '2024-01-01'),
    (9, 'Andrianivo', 'Solo', '1986-12-01', 'MASCULINE', 'Antananarivo', 'Rice Farmer', '+261320000009', 'solo.andrianivo@mail.mg', '2024-01-01'),
    (10, 'Rakotoson', 'Nirina', '1991-05-17', 'FEMININE', 'Antananarivo', 'Floriculturist', '+261320000010', 'nirina.rakotoson@mail.mg', '2024-01-01'),
    (11, 'Rafaralahy', 'Mamy', '1983-01-28', 'MASCULINE', 'Antananarivo', 'Arboriculturist', '+261320000011', 'mamy.rafaralahy@mail.mg', '2024-01-01'),
    (12, 'Raharijaona', 'Zo', '1997-10-11', 'FEMININE', 'Antananarivo', 'Livestock Farmer', '+261320000012', 'zo.raharijaona@mail.mg', '2024-01-10');

-- Collectivity
INSERT INTO collectivity (id, number, name, agricultural_specialty, city, creation_date, opening_authorization) VALUES
    (1, 'COLL-001', 'Rice Collectivity of Antananarivo', 'Rice Cultivation', 'Antananarivo', '2024-02-01', TRUE);

-- Adhesions
INSERT INTO adhesion (member_id, collectivity_id, sponsor_id, adhesion_date, active) VALUES
    (1, 1, NULL, '2024-02-01', TRUE),
    (2, 1, NULL, '2024-02-01', TRUE),
    (3, 1, NULL, '2024-02-01', TRUE),
    (4, 1, NULL, '2024-02-01', TRUE),
    (5, 1, NULL, '2024-02-01', TRUE),
    (6, 1, NULL, '2024-02-01', TRUE),
    (7, 1, NULL, '2024-02-01', TRUE),
    (8, 1, NULL, '2024-02-01', TRUE),
    (9, 1, NULL, '2024-02-01', TRUE),
    (10, 1, NULL, '2024-02-01', TRUE),
    (11, 1, NULL, '2024-02-01', TRUE),
    (12, 1, 1, '2024-02-10', TRUE);

-- Mandate 2025
INSERT INTO mandate (id, collectivity_id, year, start_date, end_date) VALUES
    (1, 1, 2025, '2025-01-01', '2025-12-31');

INSERT INTO mandate_position (mandate_id, member_id, position) VALUES
    (1, 1, 'PRESIDENT'),
    (1, 2, 'VICE_PRESIDENT'),
    (1, 3, 'TREASURER'),
    (1, 4, 'SECRETARY'),
    (1, 5, 'CONFIRMED_MEMBER'),
    (1, 6, 'CONFIRMED_MEMBER'),
    (1, 7, 'CONFIRMED_MEMBER'),
    (1, 8, 'JUNIOR_MEMBER'),
    (1, 9, 'JUNIOR_MEMBER'),
    (1, 10, 'JUNIOR_MEMBER'),
    (1, 11, 'CONFIRMED_MEMBER'),
    (1, 12, 'JUNIOR_MEMBER');

-- Federation mandate 2024-2026
INSERT INTO federation_mandate (id, federation_id, start_date, end_date) VALUES
    (1, 1, '2024-01-01', '2026-01-01');

INSERT INTO federation_mandate_position (federation_mandate_id, member_id, position) VALUES
    (1, 1, 'PRESIDENT'),
    (1, 2, 'VICE_PRESIDENT'),
    (1, 3, 'TREASURER'),
    (1, 4, 'SECRETARY');

-- Collectivity accounts
INSERT INTO account (id, owner_type, collectivity_id, type, balance, balance_date) VALUES
    (1, 'COLLECTIVITY', 1, 'CASH', 500000.00, '2025-04-01'),
    (2, 'COLLECTIVITY', 1, 'BANK', 2500000.00, '2025-04-01'),
    (3, 'COLLECTIVITY', 1, 'MOBILE_MONEY', 750000.00, '2025-04-01');

INSERT INTO bank_account (account_id, holder, bank_name, account_number) VALUES
    (2, 'Rice Collectivity of Antananarivo', 'BOA', '00005000011234567890100');

INSERT INTO mobile_money_account (account_id, holder, service, phone) VALUES
    (3, 'Jean Rakoto', 'MVOLA', '+261340000003');

-- Federation account
INSERT INTO account (id, owner_type, federation_id, type, balance, balance_date) VALUES
    (4, 'FEDERATION', 1, 'CASH', 1000000.00, '2025-04-01');

-- Contributions
INSERT INTO contribution (collectivity_id, member_id, amount, collection_date, payment_mode, type, federation_reverse_amount, reason) VALUES
    (1, 1, 10000, '2025-01-05', 'MOBILE_MONEY', 'MONTHLY', 1000, NULL),
    (1, 2, 10000, '2025-01-06', 'CASH', 'MONTHLY', 1000, NULL),
    (1, 3, 10000, '2025-01-07', 'BANK_TRANSFER', 'MONTHLY', 1000, NULL),
    (1, 4, 10000, '2025-01-08', 'MOBILE_MONEY', 'MONTHLY', 1000, NULL),
    (1, 5, 10000, '2025-02-05', 'CASH', 'MONTHLY', 1000, NULL),
    (1, 1, 50000, '2025-03-01', 'BANK_TRANSFER', 'ONE_TIME', NULL, 'Field training funding');

-- Membership Fees (new)
INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, status) VALUES
    (1, 1, '2024-01-01', 'MONTHLY', 10000.00, 'Standard monthly fee', 'ACTIVE'),
    (2, 1, '2024-01-01', 'YEARLY', 100000.00, 'Annual fee', 'ACTIVE');

-- Member Payments (new)
INSERT INTO member_payment (member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date) VALUES
    (1, 1, 1, 10000.00, 'MOBILE_MONEY', '2025-01-05'),
    (2, 1, 1, 10000.00, 'CASH', '2025-01-06'),
    (3, 1, 1, 10000.00, 'BANK_TRANSFER', '2025-01-07');

-- Collectivity Transactions (new)
INSERT INTO collectivity_transaction (collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date) VALUES
    (1, 1, 4, 1000.00, 'BANK_TRANSFER', '2025-01-05'),
    (1, 2, 4, 1000.00, 'CASH', '2025-01-06'),
    (1, 3, 4, 1000.00, 'BANK_TRANSFER', '2025-01-07');

-- Calendar 2025
INSERT INTO calendar (collectivity_id, year, general_meeting_rule, junior_training_rule) VALUES
    (1, 2025, '2nd Sunday of the month', '4th Saturday of the month');

-- Activities
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    (1, 1, 'GENERAL_MEETING', 'General Assembly - January 2025', '2025-01-12 09:00:00', TRUE),
    (2, 1, 'JUNIOR_TRAINING', 'Junior Training - January 2025', '2025-01-25 08:00:00', FALSE),
    (3, 1, 'EXCEPTIONAL', 'Training on new rice cultivation techniques', '2025-02-15 09:00:00', FALSE),
    (4, NULL, 'FEDERATION', 'Annual General Assembly of the Federation 2025', '2025-04-05 09:00:00', TRUE);

-- Target position for junior training
INSERT INTO activity_target_position (activity_id, position) VALUES
    (2, 'JUNIOR_MEMBER');

-- Collectivities invited to federation AG
INSERT INTO activity_invited_collectivity (activity_id, collectivity_id) VALUES
    (4, 1);

-- Presence at January General Assembly
INSERT INTO presence (activity_id, member_id, status, is_visitor, absence_reason) VALUES
    (1, 1, 'PRESENT', FALSE, NULL),
    (1, 2, 'PRESENT', FALSE, NULL),
    (1, 3, 'PRESENT', FALSE, NULL),
    (1, 4, 'ABSENT', FALSE, NULL),
    (1, 5, 'PRESENT', FALSE, NULL),
    (1, 6, 'PRESENT', FALSE, NULL),
    (1, 7, 'PRESENT', FALSE, NULL),
    (1, 8, 'PRESENT', FALSE, NULL),
    (1, 9, 'PRESENT', FALSE, NULL),
    (1, 10, 'PRESENT', FALSE, NULL),
    (1, 11, 'PRESENT', FALSE, NULL),
    (1, 12, 'EXCUSED', FALSE, 'Business trip');

-- Presence at junior training
INSERT INTO presence (activity_id, member_id, status, is_visitor, absence_reason) VALUES
    (2, 8, 'PRESENT', FALSE, NULL),
    (2, 9, 'PRESENT', FALSE, NULL),
    (2, 10, 'ABSENT', FALSE, NULL),
    (2, 12, 'PRESENT', FALSE, NULL);

-- ================================================================
-- 4. SEQUENCE RESET
-- ================================================================

-- Reset sequences for next IDs
SELECT setval('member_id_seq', (SELECT MAX(id) FROM member));
SELECT setval('federation_id_seq', (SELECT MAX(id) FROM federation));
SELECT setval('collectivity_id_seq', (SELECT MAX(id) FROM collectivity));
SELECT setval('adhesion_id_seq', (SELECT MAX(id) FROM adhesion));
SELECT setval('mandate_id_seq', (SELECT MAX(id) FROM mandate));
SELECT setval('mandate_position_id_seq', (SELECT MAX(id) FROM mandate_position));
SELECT setval('federation_mandate_id_seq', (SELECT MAX(id) FROM federation_mandate));
SELECT setval('federation_mandate_position_id_seq', (SELECT MAX(id) FROM federation_mandate_position));
SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('bank_account_id_seq', (SELECT MAX(id) FROM bank_account));
SELECT setval('mobile_money_account_id_seq', (SELECT MAX(id) FROM mobile_money_account));
SELECT setval('contribution_id_seq', (SELECT MAX(id) FROM contribution));
SELECT setval('membership_fee_id_seq', (SELECT MAX(id) FROM membership_fee));
SELECT setval('member_payment_id_seq', (SELECT MAX(id) FROM member_payment));
SELECT setval('collectivity_transaction_id_seq', (SELECT MAX(id) FROM collectivity_transaction));
SELECT setval('activity_id_seq', (SELECT MAX(id) FROM activity));
SELECT setval('calendar_id_seq', (SELECT MAX(id) FROM calendar));
SELECT setval('presence_id_seq', (SELECT MAX(id) FROM presence));
