-- ================================================================
-- 0. DATABASE & USER
-- ================================================================
CREATE USER agro_federation_user WITH PASSWORD '12345678';
CREATE DATABASE agro_federation_db OWNER agro_federation_user;
\c agro_federation_db;
GRANT ALL PRIVILEGES ON DATABASE agro_federation_db TO agro_federation_user;

-- ================================================================
-- 1. TABLES
-- ================================================================

CREATE TABLE membre (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom              VARCHAR(100)  NOT NULL,
    prenom           VARCHAR(100)  NOT NULL,
    date_naissance   DATE          NOT NULL,
    genre            VARCHAR(10)   NOT NULL CHECK (genre IN ('MASCULIN', 'FEMININ')),
    adresse          TEXT          NOT NULL,
    metier           VARCHAR(150)  NOT NULL,
    telephone        VARCHAR(20)   NOT NULL UNIQUE,
    email            VARCHAR(150)  NOT NULL UNIQUE,
    date_adhesion    DATE          NOT NULL DEFAULT CURRENT_DATE,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE federation (
    id                               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                              VARCHAR(300)   NOT NULL,
    pourcentage_cotisation_reverse   NUMERIC(5,2)   NOT NULL DEFAULT 10.00
                                         CHECK (pourcentage_cotisation_reverse BETWEEN 0 AND 100),
    created_at                       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at                       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE collectivite (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero                  VARCHAR(50)   NOT NULL UNIQUE,
    nom                     VARCHAR(200)  NOT NULL UNIQUE,
    specialite_agricole     VARCHAR(150)  NOT NULL,
    ville                   VARCHAR(100)  NOT NULL,
    date_creation           DATE          NOT NULL,
    autorisation_ouverture  BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE adhesion (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id           UUID          NOT NULL REFERENCES membre(id),
    collectivite_id     UUID          NOT NULL REFERENCES collectivite(id),
    parrain_id          UUID          REFERENCES membre(id),
    date_adhesion       DATE          NOT NULL DEFAULT CURRENT_DATE,
    date_demission      DATE,
    actif               BOOLEAN       NOT NULL DEFAULT TRUE,
    motif_demission     TEXT,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dates_adhesion CHECK (
        date_demission IS NULL OR date_demission > date_adhesion
    )
);

CREATE TABLE mandat (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    collectivite_id   UUID          NOT NULL REFERENCES collectivite(id),
    annee             INTEGER       NOT NULL,
    date_debut        DATE          NOT NULL,
    date_fin          DATE          NOT NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_mandat_collectivite_annee UNIQUE (collectivite_id, annee),
    CONSTRAINT chk_mandat_dates CHECK (date_fin > date_debut)
);

CREATE TABLE poste_mandat (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mandat_id   UUID          NOT NULL REFERENCES mandat(id),
    membre_id   UUID          NOT NULL REFERENCES membre(id),
    poste       VARCHAR(30)   NOT NULL CHECK (
                    poste IN ('PRESIDENT','PRESIDENT_ADJOINT','TRESORIER',
                              'SECRETAIRE','MEMBRE_CONFIRME','MEMBRE_JUNIOR')
                ),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_membre_mandat UNIQUE (mandat_id, membre_id)
);

-- Index partiel : un seul titulaire par poste spécifique par mandat
CREATE UNIQUE INDEX uq_poste_specifique_mandat
    ON poste_mandat (mandat_id, poste)
    WHERE poste IN ('PRESIDENT','PRESIDENT_ADJOINT','TRESORIER','SECRETAIRE');

CREATE TABLE mandat_federation (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    federation_id   UUID          NOT NULL REFERENCES federation(id),
    date_debut      DATE          NOT NULL,
    date_fin        DATE          NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_mandat_federation_duree CHECK (
        date_fin = date_debut + INTERVAL '2 years'
    )
);

CREATE TABLE poste_mandat_federation (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mandat_federation_id  UUID          NOT NULL REFERENCES mandat_federation(id),
    membre_id             UUID          NOT NULL REFERENCES membre(id),
    poste                 VARCHAR(30)   NOT NULL CHECK (
                              poste IN ('PRESIDENT','PRESIDENT_ADJOINT',
                                        'TRESORIER','SECRETAIRE')
                          ),
    created_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_poste_mandat_federation  UNIQUE (mandat_federation_id, poste),
    CONSTRAINT uq_membre_mandat_federation UNIQUE (mandat_federation_id, membre_id)
);

CREATE TABLE compte (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proprietaire_type   VARCHAR(15)   NOT NULL CHECK (
                            proprietaire_type IN ('COLLECTIVITE','FEDERATION')
                        ),
    collectivite_id     UUID          REFERENCES collectivite(id),
    federation_id       UUID          REFERENCES federation(id),
    type                VARCHAR(15)   NOT NULL CHECK (
                            type IN ('CAISSE','BANCAIRE','MOBILE_MONEY')
                        ),
    solde               NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    date_solde          DATE          NOT NULL DEFAULT CURRENT_DATE,
    devise              VARCHAR(5)    NOT NULL DEFAULT 'MGA',
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_proprietaire CHECK (
        (proprietaire_type = 'COLLECTIVITE' AND collectivite_id IS NOT NULL AND federation_id IS NULL)
        OR
        (proprietaire_type = 'FEDERATION'   AND federation_id IS NOT NULL AND collectivite_id IS NULL)
    )
);

-- Une seule caisse par entité
CREATE UNIQUE INDEX uq_caisse_collectivite
    ON compte (collectivite_id) WHERE type = 'CAISSE' AND proprietaire_type = 'COLLECTIVITE';
CREATE UNIQUE INDEX uq_caisse_federation
    ON compte (federation_id)   WHERE type = 'CAISSE' AND proprietaire_type = 'FEDERATION';

CREATE TABLE compte_bancaire (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    compte_id       UUID          NOT NULL UNIQUE REFERENCES compte(id),
    titulaire       VARCHAR(200)  NOT NULL,
    banque          VARCHAR(30)   NOT NULL CHECK (
                        banque IN ('BRED','MCB','BMOI','BOA','BGFI',
                                   'AFG','ACCES_BANQUE','BAOBAB','SIPEM')
                    ),
    numero_compte   CHAR(23)      NOT NULL UNIQUE
                        CHECK (numero_compte ~ '^\d{23}$'),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE compte_mobile_money (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    compte_id   UUID          NOT NULL UNIQUE REFERENCES compte(id),
    titulaire   VARCHAR(200)  NOT NULL,
    service     VARCHAR(20)   NOT NULL CHECK (
                    service IN ('ORANGE_MONEY','MVOLA','AIRTEL_MONEY')
                ),
    telephone   VARCHAR(20)   NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE cotisation (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    collectivite_id             UUID          NOT NULL REFERENCES collectivite(id),
    membre_id                   UUID          NOT NULL REFERENCES membre(id),
    montant                     NUMERIC(12,2) NOT NULL CHECK (montant > 0),
    date_encaissement           DATE          NOT NULL DEFAULT CURRENT_DATE,
    mode_paiement               VARCHAR(20)   NOT NULL CHECK (
                                    mode_paiement IN ('ESPECE','VIREMENT_BANCAIRE','MOBILE_MONEY')
                                ),
    type                        VARCHAR(15)   NOT NULL CHECK (
                                    type IN ('MENSUELLE','ANNUELLE','PONCTUELLE')
                                ),
    motif                       TEXT,
    montant_reverse_federation  NUMERIC(12,2),
    created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_motif_ponctuel CHECK (
        type != 'PONCTUELLE' OR motif IS NOT NULL
    )
);

CREATE TABLE activite (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    collectivite_id   UUID          REFERENCES collectivite(id),
    type              VARCHAR(25)   NOT NULL CHECK (
                          type IN ('ASSEMBLEE_GENERALE','FORMATION_JUNIORS',
                                   'EXCEPTIONNELLE','FEDERATION')
                      ),
    titre             VARCHAR(300)  NOT NULL,
    description       TEXT,
    date              TIMESTAMP     NOT NULL,
    obligatoire       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE activite_poste_vise (
    activite_id   UUID          NOT NULL REFERENCES activite(id) ON DELETE CASCADE,
    poste         VARCHAR(30)   NOT NULL CHECK (
                      poste IN ('PRESIDENT','PRESIDENT_ADJOINT','TRESORIER',
                                'SECRETAIRE','MEMBRE_CONFIRME','MEMBRE_JUNIOR')
                  ),
    PRIMARY KEY (activite_id, poste)
);

CREATE TABLE activite_collectivite_invitee (
    activite_id     UUID NOT NULL REFERENCES activite(id) ON DELETE CASCADE,
    collectivite_id UUID NOT NULL REFERENCES collectivite(id),
    PRIMARY KEY (activite_id, collectivite_id)
);

CREATE TABLE calendrier (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    collectivite_id             UUID          NOT NULL REFERENCES collectivite(id),
    annee                       INTEGER       NOT NULL,
    regle_assemblee_generale    VARCHAR(100)  NOT NULL,
    regle_formation_juniors     VARCHAR(100)  NOT NULL,
    created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_calendrier_collectivite_annee UNIQUE (collectivite_id, annee)
);

CREATE TABLE presence (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    activite_id     UUID          NOT NULL REFERENCES activite(id),
    membre_id       UUID          NOT NULL REFERENCES membre(id),
    statut          VARCHAR(10)   NOT NULL CHECK (statut IN ('PRESENT','ABSENT','EXCUSE')),
    est_visiteur    BOOLEAN       NOT NULL DEFAULT FALSE,
    motif_absence   TEXT,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_presence_activite_membre UNIQUE (activite_id, membre_id),
    CONSTRAINT chk_motif_excuse CHECK (
        statut != 'EXCUSE' OR motif_absence IS NOT NULL
    )
);

-- Droits sur toutes les tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO agro_federation_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO agro_federation_user;

-- ================================================================
-- 2. DONNÉES DE TEST
-- ================================================================

-- Fédération
INSERT INTO federation (id, nom, pourcentage_cotisation_reverse) VALUES
    ('00000000-0000-0000-0000-000000000001',
     'Fédération Nationale de Collectivités Agricoles de Madagascar', 10.00);

-- Membres (12 pour pouvoir ouvrir 1 collectivité)
INSERT INTO membre (id, nom, prenom, date_naissance, genre, adresse, metier, telephone, email, date_adhesion) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Rakoto',    'Jean',     '1985-03-12', 'MASCULIN', 'Antananarivo',  'Riziculteur',    '+261320000001', 'jean.rakoto@mail.mg',     '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000002', 'Rabe',      'Marie',    '1990-07-22', 'FEMININ',  'Antananarivo',  'Maraîchère',     '+261320000002', 'marie.rabe@mail.mg',      '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000003', 'Andry',     'Pascal',   '1988-11-05', 'MASCULIN', 'Antananarivo',  'Éleveur',        '+261320000003', 'pascal.andry@mail.mg',    '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000004', 'Rasoa',     'Claudine', '1992-04-18', 'FEMININ',  'Antananarivo',  'Apicultrice',    '+261320000004', 'claudine.rasoa@mail.mg',  '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000005', 'Randriana', 'Hery',     '1980-09-30', 'MASCULIN', 'Antananarivo',  'Agriculteur',    '+261320000005', 'hery.randriana@mail.mg',  '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000006', 'Ramiandrisoa', 'Lanto', '1995-02-14', 'FEMININ',  'Antananarivo',  'Pépiniériste',   '+261320000006', 'lanto.rami@mail.mg',      '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000007', 'Randria',   'Toky',     '1987-06-25', 'MASCULIN', 'Antananarivo',  'Pisciculteur',   '+261320000007', 'toky.randria@mail.mg',    '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000008', 'Rasolofo',  'Fanja',    '1993-08-09', 'FEMININ',  'Antananarivo',  'Maraîchère',     '+261320000008', 'fanja.rasolofo@mail.mg',  '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000009', 'Andrianivo','Solo',     '1986-12-01', 'MASCULIN', 'Antananarivo',  'Riziculteur',    '+261320000009', 'solo.andrianivo@mail.mg', '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000010', 'Rakotoson', 'Nirina',   '1991-05-17', 'FEMININ',  'Antananarivo',  'Floriculture',   '+261320000010', 'nirina.rakotoson@mail.mg','2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000011', 'Rafaralahy','Mamy',     '1983-01-28', 'MASCULIN', 'Antananarivo',  'Arboriculteur',  '+261320000011', 'mamy.rafaralahy@mail.mg', '2024-01-01'),
    ('aaaaaaaa-0000-0000-0000-000000000012', 'Raharijaona','Zo',      '1997-10-11', 'FEMININ',  'Antananarivo',  'Éleveuse',       '+261320000012', 'zo.raharijaona@mail.mg',  '2024-01-10');

-- Collectivité
INSERT INTO collectivite (id, numero, nom, specialite_agricole, ville, date_creation, autorisation_ouverture) VALUES
    ('cccccccc-0000-0000-0000-000000000001',
     'COLL-001',
     'Collectivité Rizicole Antananarivo',
     'Riziculture',
     'Antananarivo',
     '2024-02-01',
     TRUE);

-- Adhésions
INSERT INTO adhesion (membre_id, collectivite_id, parrain_id, date_adhesion, actif) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000006', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000007', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000008', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000009', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000010', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000011', 'cccccccc-0000-0000-0000-000000000001', NULL, '2024-02-01', TRUE),
    ('aaaaaaaa-0000-0000-0000-000000000012', 'cccccccc-0000-0000-0000-000000000001',
     'aaaaaaaa-0000-0000-0000-000000000001', '2024-02-10', TRUE);

-- Mandat 2025
INSERT INTO mandat (id, collectivite_id, annee, date_debut, date_fin) VALUES
    ('dddddddd-0000-0000-0000-000000000001',
     'cccccccc-0000-0000-0000-000000000001',
     2025, '2025-01-01', '2025-12-31');

INSERT INTO poste_mandat (mandat_id, membre_id, poste) VALUES
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'PRESIDENT'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'PRESIDENT_ADJOINT'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000003', 'TRESORIER'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 'SECRETAIRE'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000005', 'MEMBRE_CONFIRME'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000006', 'MEMBRE_CONFIRME'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000007', 'MEMBRE_CONFIRME'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000008', 'MEMBRE_JUNIOR'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000009', 'MEMBRE_JUNIOR'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000010', 'MEMBRE_JUNIOR'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000011', 'MEMBRE_CONFIRME'),
    ('dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000012', 'MEMBRE_JUNIOR');

-- Mandat fédération 2024-2026
INSERT INTO mandat_federation (id, federation_id, date_debut, date_fin) VALUES
    ('eeeeeeee-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     '2024-01-01', '2026-01-01');

INSERT INTO poste_mandat_federation (mandat_federation_id, membre_id, poste) VALUES
    ('eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'PRESIDENT'),
    ('eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'PRESIDENT_ADJOINT'),
    ('eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000003', 'TRESORIER'),
    ('eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 'SECRETAIRE');

-- Comptes de la collectivité
INSERT INTO compte (id, proprietaire_type, collectivite_id, type, solde, date_solde) VALUES
    ('ffffffff-0000-0000-0000-000000000001', 'COLLECTIVITE', 'cccccccc-0000-0000-0000-000000000001', 'CAISSE',      500000.00, '2025-04-01'),
    ('ffffffff-0000-0000-0000-000000000002', 'COLLECTIVITE', 'cccccccc-0000-0000-0000-000000000001', 'BANCAIRE',   2500000.00, '2025-04-01'),
    ('ffffffff-0000-0000-0000-000000000003', 'COLLECTIVITE', 'cccccccc-0000-0000-0000-000000000001', 'MOBILE_MONEY', 750000.00, '2025-04-01');

INSERT INTO compte_bancaire (compte_id, titulaire, banque, numero_compte) VALUES
    ('ffffffff-0000-0000-0000-000000000002',
     'Collectivité Rizicole Antananarivo',
     'BOA',
     '00005000011234567890100');

INSERT INTO compte_mobile_money (compte_id, titulaire, service, telephone) VALUES
    ('ffffffff-0000-0000-0000-000000000003',
     'Rakoto Jean',
     'MVOLA',
     '+261340000003');

-- Compte de la fédération
INSERT INTO compte (id, proprietaire_type, federation_id, type, solde, date_solde) VALUES
    ('ffffffff-0000-0000-0000-000000000010', 'FEDERATION', '00000000-0000-0000-0000-000000000001', 'CAISSE', 1000000.00, '2025-04-01');

-- Cotisations
INSERT INTO cotisation (collectivite_id, membre_id, montant, date_encaissement, mode_paiement, type, montant_reverse_federation) VALUES
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 10000, '2025-01-05', 'MOBILE_MONEY',    'MENSUELLE', 1000),
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 10000, '2025-01-06', 'ESPECE',          'MENSUELLE', 1000),
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000003', 10000, '2025-01-07', 'VIREMENT_BANCAIRE','MENSUELLE', 1000),
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 10000, '2025-01-08', 'MOBILE_MONEY',    'MENSUELLE', 1000),
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000005', 10000, '2025-02-05', 'ESPECE',          'MENSUELLE', 1000),
    ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 50000, '2025-03-01', 'VIREMENT_BANCAIRE','PONCTUELLE', NULL);

-- Erreur intentionnelle : la cotisation ponctuelle doit avoir un motif
UPDATE cotisation SET motif = 'Financement formation terrain' WHERE type = 'PONCTUELLE';

-- Calendrier 2025
INSERT INTO calendrier (collectivite_id, annee, regle_assemblee_generale, regle_formation_juniors) VALUES
    ('cccccccc-0000-0000-0000-000000000001', 2025,
     '2ème dimanche du mois',
     '4ème samedi du mois');

-- Activités
INSERT INTO activite (id, collectivite_id, type, titre, date, obligatoire) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001',
     'cccccccc-0000-0000-0000-000000000001',
     'ASSEMBLEE_GENERALE',
     'Assemblée générale - Janvier 2025',
     '2025-01-12 09:00:00',
     TRUE),
    ('bbbbbbbb-0000-0000-0000-000000000002',
     'cccccccc-0000-0000-0000-000000000001',
     'FORMATION_JUNIORS',
     'Formation juniors - Janvier 2025',
     '2025-01-25 08:00:00',
     FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000003',
     'cccccccc-0000-0000-0000-000000000001',
     'EXCEPTIONNELLE',
     'Formation sur les nouvelles techniques de riziculture',
     '2025-02-15 09:00:00',
     FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000004',
     NULL,
     'FEDERATION',
     'Assemblée Générale Annuelle de la Fédération 2025',
     '2025-04-05 09:00:00',
     TRUE);

-- Poste visé pour la formation juniors
INSERT INTO activite_poste_vise (activite_id, poste) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000002', 'MEMBRE_JUNIOR');

-- Collectivités invitées à l'AG fédération
INSERT INTO activite_collectivite_invitee (activite_id, collectivite_id) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000001');

-- Présences AG Janvier
INSERT INTO presence (activite_id, membre_id, statut, est_visiteur) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000003', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 'ABSENT',  FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000005', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000006', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000007', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000008', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000009', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000010', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000011', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000012', 'EXCUSE',  FALSE);

UPDATE presence SET motif_absence = 'Déplacement professionnel'
WHERE activite_id = 'bbbbbbbb-0000-0000-0000-000000000001'
  AND membre_id   = 'aaaaaaaa-0000-0000-0000-000000000012';

-- Présences formation juniors
INSERT INTO presence (activite_id, membre_id, statut, est_visiteur) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000008', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000009', 'PRESENT', FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000010', 'ABSENT',  FALSE),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000012', 'PRESENT', FALSE);
