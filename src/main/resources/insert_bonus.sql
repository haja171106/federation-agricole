-- ================================================================
-- SCRIPT D'INSERTION DES DONNÉES BONUS (activités et présences)
-- À EXÉCUTER APRÈS insert2.sql
-- ================================================================

-- Désactiver temporairement les vérifications de clés étrangères
SET session_replication_role = replica;

-- ================================================================
-- 1. AJOUT DES TYPES D'ACTIVITÉ MANQUANTS
-- ================================================================
ALTER TABLE activity DROP CONSTRAINT IF EXISTS activity_type_check;
ALTER TABLE activity ADD CONSTRAINT activity_type_check
    CHECK (type IN ('GENERAL_MEETING', 'JUNIOR_TRAINING', 'EXCEPTIONAL',
                    'FEDERATION', 'MEETING', 'TRAINING', 'PUNCTUAL'));

-- ================================================================
-- 2. SUPPRESSION DES DONNÉES EXISTANTES
-- ================================================================
DELETE FROM presence WHERE activity_id IN (
    'act-1', 'act-2', 'act-3', 'act-4', 'act-5', 'act-6', 'act-7',
    'act-1-avril', 'act-3-avril', 'act-6-avril'
);

DELETE FROM activity_target_position WHERE activity_id IN (
    'act-1', 'act-2', 'act-3', 'act-4', 'act-5', 'act-6', 'act-7',
    'act-1-avril', 'act-3-avril', 'act-6-avril'
);

DELETE FROM activity WHERE id IN (
    'act-1', 'act-2', 'act-3', 'act-4', 'act-5', 'act-6', 'act-7',
    'act-1-avril', 'act-3-avril', 'act-6-avril'
);

-- Réactiver les vérifications
SET session_replication_role = default;

-- ================================================================
-- 3. ACTIVITÉS DE LA COLLECTIVITÉ 1
-- ================================================================
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    ('act-1', 'col-1', 'MEETING', 'AG1', '2026-03-07 00:00:00', TRUE),
    ('act-2', 'col-1', 'TRAINING', 'Formation de base', '2026-03-08 00:00:00', TRUE)
ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
    type = EXCLUDED.type,
    title = EXCLUDED.title,
    date = EXCLUDED.date,
    mandatory = EXCLUDED.mandatory;

-- ================================================================
-- 4. ACTIVITÉS DE LA COLLECTIVITÉ 2
-- ================================================================
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    ('act-3', 'col-2', 'MEETING', 'AG2', '2026-03-08 00:00:00', TRUE),
    ('act-4', 'col-2', 'TRAINING', 'Formation de base', '2026-03-15 00:00:00', TRUE),
    ('act-5', 'col-2', 'PUNCTUAL', 'Perfectionnement', '2026-04-30 00:00:00', FALSE)
ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
    type = EXCLUDED.type,
    title = EXCLUDED.title,
    date = EXCLUDED.date,
    mandatory = EXCLUDED.mandatory;

-- ================================================================
-- 5. ACTIVITÉS DE LA COLLECTIVITÉ 3
-- ================================================================
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    ('act-6', 'col-3', 'MEETING', 'AG3', '2026-03-06 00:00:00', TRUE),
    ('act-7', 'col-3', 'TRAINING', 'Formation de base', '2026-03-25 00:00:00', TRUE)
ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
    type = EXCLUDED.type,
    title = EXCLUDED.title,
    date = EXCLUDED.date,
    mandatory = EXCLUDED.mandatory;

-- ================================================================
-- 6. ACTIVITÉS POUR AVRIL 2026 (activités récurrentes)
-- ================================================================
INSERT INTO activity (id, collectivity_id, type, title, date, mandatory) VALUES
    ('act-1-avril', 'col-1', 'MEETING', 'AG1', '2026-04-04 00:00:00', TRUE),
    ('act-3-avril', 'col-2', 'MEETING', 'AG2', '2026-04-05 00:00:00', TRUE),
    ('act-6-avril', 'col-3', 'MEETING', 'AG3', '2026-04-03 00:00:00', TRUE)
ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
    type = EXCLUDED.type,
    title = EXCLUDED.title,
    date = EXCLUDED.date,
    mandatory = EXCLUDED.mandatory;

-- ================================================================
-- 7. ACTIVITY_TARGET_POSITIONS
-- ================================================================

-- Pour act-1 (AG1) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-1', 'PRESIDENT'), ('act-1', 'VICE_PRESIDENT'), ('act-1', 'TREASURER'),
    ('act-1', 'SECRETARY'), ('act-1', 'CONFIRMED_MEMBER'), ('act-1', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-2 (Formation de base) - seulement juniors
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-2', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-3 (AG2) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-3', 'PRESIDENT'), ('act-3', 'VICE_PRESIDENT'), ('act-3', 'TREASURER'),
    ('act-3', 'SECRETARY'), ('act-3', 'CONFIRMED_MEMBER'), ('act-3', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-4 (Formation de base col2) - seulement juniors
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-4', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-5 (Perfectionnement) - seulement seniors (CONFIRMED_MEMBER)
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-5', 'CONFIRMED_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-6 (AG3) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-6', 'PRESIDENT'), ('act-6', 'VICE_PRESIDENT'), ('act-6', 'TREASURER'),
    ('act-6', 'SECRETARY'), ('act-6', 'CONFIRMED_MEMBER'), ('act-6', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-7 (Formation de base col3) - seulement juniors
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-7', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-1-avril (AG1 avril) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-1-avril', 'PRESIDENT'), ('act-1-avril', 'VICE_PRESIDENT'), ('act-1-avril', 'TREASURER'),
    ('act-1-avril', 'SECRETARY'), ('act-1-avril', 'CONFIRMED_MEMBER'), ('act-1-avril', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-3-avril (AG2 avril) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-3-avril', 'PRESIDENT'), ('act-3-avril', 'VICE_PRESIDENT'), ('act-3-avril', 'TREASURER'),
    ('act-3-avril', 'SECRETARY'), ('act-3-avril', 'CONFIRMED_MEMBER'), ('act-3-avril', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- Pour act-6-avril (AG3 avril) - tous les postes
INSERT INTO activity_target_position (activity_id, position) VALUES
    ('act-6-avril', 'PRESIDENT'), ('act-6-avril', 'VICE_PRESIDENT'), ('act-6-avril', 'TREASURER'),
    ('act-6-avril', 'SECRETARY'), ('act-6-avril', 'CONFIRMED_MEMBER'), ('act-6-avril', 'JUNIOR_MEMBER')
ON CONFLICT (activity_id, position) DO NOTHING;

-- ================================================================
-- 8. PRÉSENCES COLLECTIVITÉ 1 - MARS 2026
-- ================================================================

-- Tableau 24 : AG1 - Mars 2026 (act-1 le 07/03/2026)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act1_mar_C1M1', 'act-1', 'C1-M1', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M2', 'act-1', 'C1-M2', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M3', 'act-1', 'C1-M3', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M4', 'act-1', 'C1-M4', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M5', 'act-1', 'C1-M5', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M6', 'act-1', 'C1-M6', 'PRESENT', FALSE, NULL),
    ('pres_act1_mar_C1M7', 'act-1', 'C1-M7', 'ABSENT', FALSE, NULL),
    ('pres_act1_mar_C1M8', 'act-1', 'C1-M8', 'ABSENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 9. PRÉSENCES COLLECTIVITÉ 1 - AVRIL 2026
-- ================================================================

-- Tableau 25 : AG1 - Avril 2026 (act-1-avril le 04/04/2026)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act1_avr_C1M1', 'act-1-avril', 'C1-M1', 'PRESENT', FALSE, NULL),
    ('pres_act1_avr_C1M2', 'act-1-avril', 'C1-M2', 'PRESENT', FALSE, NULL),
    ('pres_act1_avr_C1M3', 'act-1-avril', 'C1-M3', 'ABSENT', FALSE, NULL),
    ('pres_act1_avr_C1M4', 'act-1-avril', 'C1-M4', 'ABSENT', FALSE, NULL),
    ('pres_act1_avr_C1M5', 'act-1-avril', 'C1-M5', 'PRESENT', FALSE, NULL),
    ('pres_act1_avr_C1M6', 'act-1-avril', 'C1-M6', 'PRESENT', FALSE, NULL),
    ('pres_act1_avr_C1M7', 'act-1-avril', 'C1-M7', 'PRESENT', FALSE, NULL),
    ('pres_act1_avr_C1M8', 'act-1-avril', 'C1-M8', 'PRESENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 10. PRÉSENCES COLLECTIVITÉ 2 - MARS 2026
-- ================================================================

-- Tableau 26 : AG2 - Mars 2026 (act-3)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act3_mar_C1M1', 'act-3', 'C1-M1', 'PRESENT', FALSE, NULL),
    ('pres_act3_mar_C1M2', 'act-3', 'C1-M2', 'PRESENT', FALSE, NULL),
    ('pres_act3_mar_C1M3', 'act-3', 'C1-M3', 'ABSENT', FALSE, NULL),
    ('pres_act3_mar_C1M4', 'act-3', 'C1-M4', 'ABSENT', FALSE, NULL),
    ('pres_act3_mar_C1M5', 'act-3', 'C1-M5', 'PRESENT', FALSE, NULL),
    ('pres_act3_mar_C1M6', 'act-3', 'C1-M6', 'PRESENT', FALSE, NULL),
    ('pres_act3_mar_C1M7', 'act-3', 'C1-M7', 'PRESENT', FALSE, NULL),
    ('pres_act3_mar_C1M8', 'act-3', 'C1-M8', 'PRESENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 11. PRÉSENCES COLLECTIVITÉ 2 - AVRIL 2026
-- ================================================================

-- Tableau 27 : AG2 - Avril 2026 (act-3-avril)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act3_avr_C1M1', 'act-3-avril', 'C1-M1', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M2', 'act-3-avril', 'C1-M2', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M3', 'act-3-avril', 'C1-M3', 'ABSENT', FALSE, NULL),
    ('pres_act3_avr_C1M4', 'act-3-avril', 'C1-M4', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M5', 'act-3-avril', 'C1-M5', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M6', 'act-3-avril', 'C1-M6', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M7', 'act-3-avril', 'C1-M7', 'PRESENT', FALSE, NULL),
    ('pres_act3_avr_C1M8', 'act-3-avril', 'C1-M8', 'ABSENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 12. PRÉSENCES COLLECTIVITÉ 2 - PERFECTIONNEMENT (30/04/2026)
-- ================================================================

-- Tableau 28 : Perfectionnement (act-5)
-- Note : "Non-défini" = ABSENT par défaut
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act5_C1M1', 'act-5', 'C1-M1', 'PRESENT', FALSE, NULL),
    ('pres_act5_C1M2', 'act-5', 'C1-M2', 'PRESENT', FALSE, NULL),
    ('pres_act5_C1M3', 'act-5', 'C1-M3', 'PRESENT', FALSE, NULL),
    ('pres_act5_C1M4', 'act-5', 'C1-M4', 'ABSENT', FALSE, NULL),
    ('pres_act5_C1M5', 'act-5', 'C1-M5', 'ABSENT', FALSE, NULL),
    ('pres_act5_C1M6', 'act-5', 'C1-M6', 'ABSENT', FALSE, NULL),
    ('pres_act5_C1M7', 'act-5', 'C1-M7', 'ABSENT', FALSE, NULL),
    ('pres_act5_C1M8', 'act-5', 'C1-M8', 'ABSENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 13. PRÉSENCES COLLECTIVITÉ 3 - MARS 2026
-- ================================================================

-- Tableau 29 : AG3 - Mars 2026 (act-6)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act6_mar_C3M1', 'act-6', 'C3-M1', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M2', 'act-6', 'C3-M2', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M3', 'act-6', 'C3-M3', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M4', 'act-6', 'C3-M4', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M5', 'act-6', 'C3-M5', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M6', 'act-6', 'C3-M6', 'PRESENT', FALSE, NULL),
    ('pres_act6_mar_C3M7', 'act-6', 'C3-M7', 'ABSENT', FALSE, NULL),
    ('pres_act6_mar_C3M8', 'act-6', 'C3-M8', 'ABSENT', FALSE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 14. PRÉSENCES COLLECTIVITÉ 3 - AVRIL 2026
-- ================================================================

-- Tableau 30 : AG3 - Avril 2026 (act-6-avril)
INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason) VALUES
    ('pres_act6_avr_C3M1', 'act-6-avril', 'C3-M1', 'PRESENT', FALSE, NULL),
    ('pres_act6_avr_C3M2', 'act-6-avril', 'C3-M2', 'PRESENT', FALSE, NULL),
    ('pres_act6_avr_C3M3', 'act-6-avril', 'C3-M3', 'ABSENT', FALSE, NULL),
    ('pres_act6_avr_C3M4', 'act-6-avril', 'C3-M4', 'ABSENT', FALSE, NULL),
    ('pres_act6_avr_C3M5', 'act-6-avril', 'C3-M5', 'PRESENT', FALSE, NULL),
    ('pres_act6_avr_C3M6', 'act-6-avril', 'C3-M6', 'PRESENT', FALSE, NULL),
    ('pres_act6_avr_C3M7', 'act-6-avril', 'C3-M7', 'ABSENT', FALSE, NULL),
    ('pres_act6_avr_C3M8', 'act-6-avril', 'C3-M8', 'PRESENT', FALSE, NULL),
    -- Invité de la collectivité 1
    ('pres_act6_avr_C1M1', 'act-6-avril', 'C1-M1', 'PRESENT', TRUE, NULL)
ON CONFLICT (activity_id, member_id) DO UPDATE SET
    status = EXCLUDED.status,
    is_visitor = EXCLUDED.is_visitor,
    absence_reason = EXCLUDED.absence_reason;

-- ================================================================
-- 15. MISE À JOUR DES CALENDARS
-- ================================================================

DELETE FROM calendar WHERE collectivity_id IN ('col-1', 'col-2', 'col-3');

INSERT INTO calendar (id, collectivity_id, year, general_meeting_rule, junior_training_rule) VALUES
    ('cal_col1', 'col-1', 2026, '1st Saturday of each month', '2nd Sunday of each month'),
    ('cal_col2', 'col-2', 2026, '1st Sunday of each month', '3rd Sunday of each month'),
    ('cal_col3', 'col-3', 2026, '1st Friday of each month', '4th Wednesday of each month')
ON CONFLICT (id) DO UPDATE SET
    general_meeting_rule = EXCLUDED.general_meeting_rule,
    junior_training_rule = EXCLUDED.junior_training_rule;

-- ================================================================
-- 16. VÉRIFICATION FINALE
-- ================================================================

DO $$
DECLARE
    v_activity_count INTEGER;
    v_presence_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_activity_count FROM activity WHERE id IN (
        'act-1', 'act-2', 'act-3', 'act-4', 'act-5', 'act-6', 'act-7',
        'act-1-avril', 'act-3-avril', 'act-6-avril'
    );

    SELECT COUNT(*) INTO v_presence_count FROM presence WHERE activity_id IN (
        'act-1', 'act-2', 'act-3', 'act-4', 'act-5', 'act-6', 'act-7',
        'act-1-avril', 'act-3-avril', 'act-6-avril'
    );

    RAISE NOTICE '============================================';
    RAISE NOTICE 'RÉSULTAT DE L INSERTION DES DONNÉES BONUS';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Activités insérées : %', v_activity_count;
    RAISE NOTICE 'Présences insérées : %', v_presence_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Détail des activités par collectivité:';
    RAISE NOTICE '  Collectivité 1 (col-1) : 3 activités (act-1, act-2, act-1-avril)';
    RAISE NOTICE '  Collectivité 2 (col-2) : 4 activités (act-3, act-4, act-5, act-3-avril)';
    RAISE NOTICE '  Collectivité 3 (col-3) : 3 activités (act-6, act-7, act-6-avril)';
    RAISE NOTICE '';
    RAISE NOTICE '✅ Insertion terminée avec succès !';
END $$;

-- ================================================================
-- FIN DU SCRIPT
-- ================================================================
