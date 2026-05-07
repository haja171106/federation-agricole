-- ================================================================
-- SUPPRESSION DES DONNÉES EXISTANTES (avec CASCADE)
-- ================================================================

-- Désactiver temporairement les vérifications de clés étrangères
SET session_replication_role = replica;

-- Supprimer les données dans l'ordre inverse des dépendances
DELETE FROM collectivity_transaction;
DELETE FROM member_payment;
DELETE FROM mandate_position;
DELETE FROM mandate;
DELETE FROM adhesion_referent;
DELETE FROM adhesion;
DELETE FROM presence;
DELETE FROM activity_invited_collectivity;
DELETE FROM activity_target_position;
DELETE FROM activity;
DELETE FROM calendar;
DELETE FROM contribution;
DELETE FROM mobile_money_account;
DELETE FROM bank_account;
DELETE FROM account;
DELETE FROM membership_fee;
DELETE FROM federation_mandate_position;
DELETE FROM federation_mandate;
DELETE FROM mandate_position;
DELETE FROM mandate;
DELETE FROM adhesion;
DELETE FROM member;
DELETE FROM collectivity;
DELETE FROM federation;

-- Réactiver les vérifications
SET session_replication_role = default;

-- ================================================================
-- RESET DES SÉQUENCES (si vous utilisez des séquences pour les IDs)
-- ================================================================

SELECT setval('member_id_seq', 1, false);
SELECT setval('collectivity_id_seq', 1, false);
SELECT setval('federation_id_seq', 1, false);
SELECT setval('adhesion_id_seq', 1, false);
SELECT setval('mandate_id_seq', 1, false);
SELECT setval('mandate_position_id_seq', 1, false);
SELECT setval('account_id_seq', 1, false);
SELECT setval('membership_fee_id_seq', 1, false);
SELECT setval('activity_id_seq', 1, false);
SELECT setval('presence_id_seq', 1, false);

-- ================================================================
-- INSERTION DES DONNÉES
-- ================================================================

-- 1. FEDERATION
INSERT INTO federation (id, name, reverse_contribution_percentage) VALUES
    ('federation_1', 'National Federation of Agricultural Collectivities of Madagascar', 10.00);

-- 2. COLLECTIVITIES
INSERT INTO collectivity (id, number, name, city, agricultural_specialty, creation_date, opening_authorization) VALUES
    ('col-1', '1', 'Mpanorina', 'Ambatondrazaka', 'Riziculture', '2024-01-01', TRUE),
    ('col-2', '2', 'Dobo voalahany', 'Ambatondrazaka', 'Pisciculture', '2024-01-01', TRUE),
    ('col-3', '3', 'Tantely mamy', 'Brickaville', 'Apiculture', '2024-01-01', TRUE);

-- 3. MEMBERS (Collectivité 1)
INSERT INTO member (id, last_name, first_name, birth_date, gender, address, profession, phone, email, membership_date) VALUES
    ('C1-M1', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'MALE', 'Lot II V M Ambato.', 'Riziculteur', '0341234561', 'member1@fed-agri.mg', '2024-01-01'),
    ('C1-M2', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'MALE', 'Lot II F Ambato.', 'Agriculteur', '0341234562', 'member2@fed-agri.mg', '2024-01-01'),
    ('C1-M3', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'MALE', 'Lot II J Ambato.', 'Collecteur', '0341234563', 'member3@fed-agri.mg', '2024-01-01'),
    ('C1-M4', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.', 'Distributeur', '0341234564', 'member4@fed-agri.mg', '2024-01-01'),
    ('C1-M5', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato.', 'Riziculteur', '0341234565', 'member5@fed-agri.mg', '2024-01-01'),
    ('C1-M6', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.', 'Riziculteur', '0341234566', 'member6@fed-agri.mg', '2024-01-01'),
    ('C1-M7', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato.', 'Riziculteur', '0341234567', 'member7@fed-agri.mg', '2024-01-01'),
    ('C1-M8', 'Nom membre 8', 'Prénom membre 8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato.', 'Riziculteur', '0341234568', 'member8@fed-agri.mg', '2024-01-01');

-- MEMBERS (Collectivité 2)
INSERT INTO member (id, last_name, first_name, birth_date, gender, address, profession, phone, email, membership_date) VALUES
    ('C2-M1', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'MALE', 'Lot II V M Ambato.', 'Riziculteur', '0341234571', 'member1.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M2', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'MALE', 'Lot II F Ambato.', 'Agriculteur', '0341234572', 'member2.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M3', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'MALE', 'Lot II J Ambato.', 'Collecteur', '0341234573', 'member3.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M4', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.', 'Distributeur', '0341234574', 'member4.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M5', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato.', 'Riziculteur', '0341234575', 'member5.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M6', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.', 'Riziculteur', '0341234576', 'member6.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M7', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato.', 'Riziculteur', '0341234577', 'member7.col2@fed-agri.mg', '2024-01-01'),
    ('C2-M8', 'Nom membre 8', 'Prénom membre 8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato.', 'Riziculteur', '0341234578', 'member8.col2@fed-agri.mg', '2024-01-01');

-- MEMBERS (Collectivité 3)
INSERT INTO member (id, last_name, first_name, birth_date, gender, address, profession, phone, email, membership_date) VALUES
    ('C3-M1', 'Nom membre 9', 'Prénom membre 9', '1988-02-01', 'MALE', 'Lot 33 J Antsirabe', 'Apiculteur', '0341234581', 'member9@fed-agri.mg', '2024-01-01'),
    ('C3-M2', 'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'MALE', 'Lot 2 J Antsirabe', 'Agriculteur', '0341234582', 'member10@fed-agri.mg', '2024-01-01'),
    ('C3-M3', 'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'MALE', 'Lot 8 KM Antsirabe', 'Collecteur', '0341234583', 'member11@fed-agri.mg', '2024-01-01'),
    ('C3-M4', 'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0341234584', 'member12@fed-agri.mg', '2024-01-01'),
    ('C3-M5', 'Nom membre 13', 'Prénom membre 13', '1999-08-11', 'MALE', 'Lot UV 80 Antsirabe', 'Apiculteur', '0341234585', 'member13@fed-agri.mg', '2024-01-01'),
    ('C3-M6', 'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe', 'Apiculteur', '0341234586', 'member14@fed-agri.mg', '2024-01-01'),
    ('C3-M7', 'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'MALE', 'Lot UV 7 Antsirabe', 'Apiculteur', '0341234587', 'member15@fed-agri.mg', '2024-01-01'),
    ('C3-M8', 'Nom membre 16', 'Prénom membre 16', '1975-08-02', 'MALE', 'Lot UV 8 Antsirabe', 'Apiculteur', '0341234588', 'member16@fed-agri.mg', '2024-01-01');

-- 4. MEMBERSHIP_FEES
INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, statut) VALUES
    ('cot-1', 'col-1', '2026-01-01', 'ANNUALLY', 100000.00, 'Cotisation annuelle', 'ACTIVE'),
    ('cot-2', 'col-2', '2026-01-01', 'ANNUALLY', 100000.00, 'Cotisation annuelle', 'ACTIVE'),
    ('cot-3', 'col-3', '2026-01-01', 'ANNUALLY', 50000.00, 'Cotisation annuelle', 'ACTIVE');

-- 5. ACCOUNTS
INSERT INTO account (id, owner_type, collectivity_id, type, balance, balance_date, currency) VALUES
    ('C1-A-CASH', 'COLLECTIVITY', 'col-1', 'CASH', 0.00, '2026-01-01', 'MGA'),
    ('C1-A-MOBILE-1', 'COLLECTIVITY', 'col-1', 'MOBILE_MONEY', 0.00, '2026-01-01', 'MGA'),
    ('C2-A-CASH', 'COLLECTIVITY', 'col-2', 'CASH', 0.00, '2026-01-01', 'MGA'),
    ('C2-A-MOBILE-1', 'COLLECTIVITY', 'col-2', 'MOBILE_MONEY', 0.00, '2026-01-01', 'MGA'),
    ('C3-A-CASH', 'COLLECTIVITY', 'col-3', 'CASH', 0.00, '2026-01-01', 'MGA');

-- 6. MOBILE MONEY ACCOUNTS
INSERT INTO mobile_money_account (id, account_id, holder, service, phone) VALUES
    ('mm_C1', 'C1-A-MOBILE-1', 'Mpanorina', 'ORANGE_MONEY', '0370489612'),
    ('mm_C2', 'C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612');

-- 7. ADHESIONS
INSERT INTO adhesion (id, member_id, collectivity_id, adhesion_date, active) VALUES
    ('adh_C1_M1', 'C1-M1', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M2', 'C1-M2', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M3', 'C1-M3', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M4', 'C1-M4', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M5', 'C1-M5', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M6', 'C1-M6', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M7', 'C1-M7', 'col-1', '2024-01-01', TRUE),
    ('adh_C1_M8', 'C1-M8', 'col-1', '2024-01-01', TRUE),
    ('adh_C2_M1', 'C2-M1', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M2', 'C2-M2', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M3', 'C2-M3', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M4', 'C2-M4', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M5', 'C2-M5', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M6', 'C2-M6', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M7', 'C2-M7', 'col-2', '2024-01-01', TRUE),
    ('adh_C2_M8', 'C2-M8', 'col-2', '2024-01-01', TRUE),
    ('adh_C3_M1', 'C3-M1', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M2', 'C3-M2', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M3', 'C3-M3', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M4', 'C3-M4', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M5', 'C3-M5', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M6', 'C3-M6', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M7', 'C3-M7', 'col-3', '2024-01-01', TRUE),
    ('adh_C3_M8', 'C3-M8', 'col-3', '2024-01-01', TRUE);

-- 8. ADHESION_REFERENT
INSERT INTO adhesion_referent (id, adhesion_id, referent_id, relation) VALUES
    ('ref_C1_M3_1', 'adh_C1_M3', 'C1-M1', 'famille'),
    ('ref_C1_M3_2', 'adh_C1_M3', 'C1-M2', 'collegue'),
    ('ref_C1_M4_1', 'adh_C1_M4', 'C1-M1', 'famille'),
    ('ref_C1_M4_2', 'adh_C1_M4', 'C1-M2', 'collegue'),
    ('ref_C1_M5_1', 'adh_C1_M5', 'C1-M1', 'famille'),
    ('ref_C1_M5_2', 'adh_C1_M5', 'C1-M2', 'collegue'),
    ('ref_C1_M6_1', 'adh_C1_M6', 'C1-M1', 'famille'),
    ('ref_C1_M6_2', 'adh_C1_M6', 'C1-M2', 'collegue'),
    ('ref_C1_M7_1', 'adh_C1_M7', 'C1-M1', 'famille'),
    ('ref_C1_M7_2', 'adh_C1_M7', 'C1-M2', 'collegue'),
    ('ref_C1_M8_1', 'adh_C1_M8', 'C1-M6', 'collegue'),
    ('ref_C1_M8_2', 'adh_C1_M8', 'C1-M7', 'collegue'),
    ('ref_C2_M3_1', 'adh_C2_M3', 'C2-M1', 'famille'),
    ('ref_C2_M3_2', 'adh_C2_M3', 'C2-M2', 'collegue'),
    ('ref_C2_M4_1', 'adh_C2_M4', 'C2-M1', 'famille'),
    ('ref_C2_M4_2', 'adh_C2_M4', 'C2-M2', 'collegue'),
    ('ref_C2_M5_1', 'adh_C2_M5', 'C2-M1', 'famille'),
    ('ref_C2_M5_2', 'adh_C2_M5', 'C2-M2', 'collegue'),
    ('ref_C2_M6_1', 'adh_C2_M6', 'C2-M1', 'famille'),
    ('ref_C2_M6_2', 'adh_C2_M6', 'C2-M2', 'collegue'),
    ('ref_C2_M7_1', 'adh_C2_M7', 'C2-M1', 'famille'),
    ('ref_C2_M7_2', 'adh_C2_M7', 'C2-M2', 'collegue'),
    ('ref_C2_M8_1', 'adh_C2_M8', 'C2-M6', 'collegue'),
    ('ref_C2_M8_2', 'adh_C2_M8', 'C2-M7', 'collegue'),
    ('ref_C3_M3_1', 'adh_C3_M3', 'C3-M1', 'famille'),
    ('ref_C3_M3_2', 'adh_C3_M3', 'C3-M2', 'collegue'),
    ('ref_C3_M4_1', 'adh_C3_M4', 'C3-M1', 'famille'),
    ('ref_C3_M4_2', 'adh_C3_M4', 'C3-M2', 'collegue'),
    ('ref_C3_M5_1', 'adh_C3_M5', 'C3-M1', 'famille'),
    ('ref_C3_M5_2', 'adh_C3_M5', 'C3-M2', 'collegue'),
    ('ref_C3_M6_1', 'adh_C3_M6', 'C3-M1', 'famille'),
    ('ref_C3_M6_2', 'adh_C3_M6', 'C3-M2', 'collegue'),
    ('ref_C3_M7_1', 'adh_C3_M7', 'C3-M1', 'famille'),
    ('ref_C3_M7_2', 'adh_C3_M7', 'C3-M2', 'collegue'),
    ('ref_C3_M8_1', 'adh_C3_M8', 'C3-M1', 'famille'),
    ('ref_C3_M8_2', 'adh_C3_M8', 'C3-M2', 'collegue');

-- 9. MANDATE
INSERT INTO mandate (id, collectivity_id, year, start_date, end_date) VALUES
    ('mandate_col1_2025', 'col-1', 2025, '2025-01-01', '2025-12-31'),
    ('mandate_col2_2025', 'col-2', 2025, '2025-01-01', '2025-12-31'),
    ('mandate_col3_2025', 'col-3', 2025, '2025-01-01', '2025-12-31');

-- 10. MANDATE_POSITION
INSERT INTO mandate_position (id, mandate_id, member_id, position) VALUES
    ('mp_C1_M1', 'mandate_col1_2025', 'C1-M1', 'PRESIDENT'),
    ('mp_C1_M2', 'mandate_col1_2025', 'C1-M2', 'VICE_PRESIDENT'),
    ('mp_C1_M3', 'mandate_col1_2025', 'C1-M3', 'SECRETARY'),
    ('mp_C1_M4', 'mandate_col1_2025', 'C1-M4', 'TREASURER'),
    ('mp_C1_M5', 'mandate_col1_2025', 'C1-M5', 'SENIOR'),
    ('mp_C1_M6', 'mandate_col1_2025', 'C1-M6', 'SENIOR'),
    ('mp_C1_M7', 'mandate_col1_2025', 'C1-M7', 'SENIOR'),
    ('mp_C1_M8', 'mandate_col1_2025', 'C1-M8', 'SENIOR'),
    ('mp_C2_M5', 'mandate_col2_2025', 'C2-M5', 'PRESIDENT'),
    ('mp_C2_M6', 'mandate_col2_2025', 'C2-M6', 'VICE_PRESIDENT'),
    ('mp_C2_M7', 'mandate_col2_2025', 'C2-M7', 'SECRETARY'),
    ('mp_C2_M8', 'mandate_col2_2025', 'C2-M8', 'TREASURER'),
    ('mp_C2_M1', 'mandate_col2_2025', 'C2-M1', 'JUNIOR'),
    ('mp_C2_M2', 'mandate_col2_2025', 'C2-M2', 'JUNIOR'),
    ('mp_C2_M3', 'mandate_col2_2025', 'C2-M3', 'JUNIOR'),
    ('mp_C2_M4', 'mandate_col2_2025', 'C2-M4', 'JUNIOR'),
    ('mp_C3_M1', 'mandate_col3_2025', 'C3-M1', 'PRESIDENT'),
    ('mp_C3_M2', 'mandate_col3_2025', 'C3-M2', 'VICE_PRESIDENT'),
    ('mp_C3_M3', 'mandate_col3_2025', 'C3-M3', 'SECRETARY'),
    ('mp_C3_M4', 'mandate_col3_2025', 'C3-M4', 'TREASURER'),
    ('mp_C3_M5', 'mandate_col3_2025', 'C3-M5', 'SENIOR'),
    ('mp_C3_M6', 'mandate_col3_2025', 'C3-M6', 'SENIOR'),
    ('mp_C3_M7', 'mandate_col3_2025', 'C3-M7', 'SENIOR'),
    ('mp_C3_M8', 'mandate_col3_2025', 'C3-M8', 'SENIOR');

-- 11. MEMBER_PAYMENT
INSERT INTO member_payment (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date) VALUES
    ('pay_C1_M1', 'C1-M1', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M2', 'C1-M2', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M3', 'C1-M3', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M4', 'C1-M4', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M5', 'C1-M5', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M6', 'C1-M6', 'cot-1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M7', 'C1-M7', 'cot-1', 'C1-A-CASH', 60000.00, 'CASH', '2026-01-01'),
    ('pay_C1_M8', 'C1-M8', 'cot-1', 'C1-A-CASH', 90000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M1', 'C2-M1', 'cot-2', 'C2-A-CASH', 60000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M2', 'C2-M2', 'cot-2', 'C2-A-CASH', 90000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M3', 'C2-M3', 'cot-2', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M4', 'C2-M4', 'cot-2', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M5', 'C2-M5', 'cot-2', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M6', 'C2-M6', 'cot-2', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('pay_C2_M7', 'C2-M7', 'cot-2', 'C2-A-MOBILE-1', 40000.00, 'MOBILE_MONEY', '2026-01-01'),
    ('pay_C2_M8', 'C2-M8', 'cot-2', 'C2-A-MOBILE-1', 60000.00, 'MOBILE_MONEY', '2026-01-01');

-- 12. COLLECTIVITY_TRANSACTION
INSERT INTO collectivity_transaction (id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date) VALUES
    ('trans_C1_M1', 'col-1', 'C1-M1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M2', 'col-1', 'C1-M2', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M3', 'col-1', 'C1-M3', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M4', 'col-1', 'C1-M4', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M5', 'col-1', 'C1-M5', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M6', 'col-1', 'C1-M6', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M7', 'col-1', 'C1-M7', 'C1-A-CASH', 60000.00, 'CASH', '2026-01-01'),
    ('trans_C1_M8', 'col-1', 'C1-M8', 'C1-A-CASH', 90000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M1', 'col-2', 'C2-M1', 'C2-A-CASH', 60000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M2', 'col-2', 'C2-M2', 'C2-A-CASH', 90000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M3', 'col-2', 'C2-M3', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M4', 'col-2', 'C2-M4', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M5', 'col-2', 'C2-M5', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M6', 'col-2', 'C2-M6', 'C2-A-CASH', 100000.00, 'CASH', '2026-01-01'),
    ('trans_C2_M7', 'col-2', 'C2-M7', 'C2-A-MOBILE-1', 40000.00, 'MOBILE_MONEY', '2026-01-01'),
    ('trans_C2_M8', 'col-2', 'C2-M8', 'C2-A-MOBILE-1', 60000.00, 'MOBILE_MONEY', '2026-01-01');

-- 13. FEDERATION_MANDATE
INSERT INTO federation_mandate (id, federation_id, start_date, end_date) VALUES
    ('fed_mandate_1', 'federation_1', '2024-01-01', '2026-01-01');

-- 14. FEDERATION_MANDATE_POSITION
INSERT INTO federation_mandate_position (id, federation_mandate_id, member_id, position) VALUES
    ('fed_pos_1', 'fed_mandate_1', 'C1-M1', 'PRESIDENT'),
    ('fed_pos_2', 'fed_mandate_1', 'C1-M2', 'VICE_PRESIDENT'),
    ('fed_pos_3', 'fed_mandate_1', 'C1-M3', 'TREASURER'),
    ('fed_pos_4', 'fed_mandate_1', 'C1-M4', 'SECRETARY');

-- 15. ACTIVITIES (si nécessaire pour les bonus)
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    ('act_col1_ga_jan', 'col-1', 'GENERAL_MEETING', 'Assemblée générale - Janvier 2025', '2025-01-12 09:00:00', TRUE),
    ('act_col1_jt_jan', 'col-1', 'JUNIOR_TRAINING', 'Formation junior - Janvier 2025', '2025-01-25 08:00:00', TRUE),
    ('act_col2_ga_jan', 'col-2', 'GENERAL_MEETING', 'Assemblée générale - Janvier 2025', '2025-01-12 09:00:00', TRUE),
    ('act_col3_ga_jan', 'col-3', 'GENERAL_MEETING', 'Assemblée générale - Janvier 2025', '2025-01-12 09:00:00', TRUE);

-- 16. PRESENCE (si nécessaire pour les bonus)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor) VALUES
    ('pres_act1_m1', 'act_col1_ga_jan', 'C1-M1', 'PRESENT', FALSE),
    ('pres_act1_m2', 'act_col1_ga_jan', 'C1-M2', 'PRESENT', FALSE),
    ('pres_act1_m3', 'act_col1_ga_jan', 'C1-M3', 'PRESENT', FALSE),
    ('pres_act1_m4', 'act_col1_ga_jan', 'C1-M4', 'PRESENT', FALSE),
    ('pres_act1_m5', 'act_col1_ga_jan', 'C1-M5', 'PRESENT', FALSE),
    ('pres_act1_m6', 'act_col1_ga_jan', 'C1-M6', 'PRESENT', FALSE),
    ('pres_act1_m7', 'act_col1_ga_jan', 'C1-M7', 'PRESENT', FALSE),
    ('pres_act1_m8', 'act_col1_ga_jan', 'C1-M8', 'ABSENT', FALSE);
    
    
    
    
    
    
-- Vérifier le nombre de membres
SELECT COUNT(*) FROM member;  -- Doit retourner 24

-- Vérifier les collectivités
SELECT COUNT(*) FROM collectivity;  -- Doit retourner 3

-- Vérifier les adhésions
SELECT COUNT(*) FROM adhesion;  -- Doit retourner 24

-- Vérifier les paiements
SELECT COUNT(*) FROM member_payment;  -- Doit retourner 16

-- Voir les collectivités avec leurs membres
SELECT c.id, c.name, COUNT(a.member_id) as nb_members
FROM collectivity c
LEFT JOIN adhesion a ON a.collectivity_id = c.id AND a.active = TRUE
GROUP BY c.id;
