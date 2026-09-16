CREATE TABLE client (
    id BIGINT NOT NULL AUTO_INCREMENT,
    adresse VARCHAR(255),
    email VARCHAR(255),
    mot_de_passe VARCHAR(255),
    nom VARCHAR(255),
    role ENUM('ADMIN', 'CLIENT'),
    telephone VARCHAR(255),

    PRIMARY KEY (id)
);

CREATE TABLE fournisseur (
     id BIGINT NOT NULL AUTO_INCREMENT,
     adresse VARCHAR(255),
     email VARCHAR(255),
     nom VARCHAR(255),
     telephone VARCHAR(255),

     PRIMARY KEY (id)
);

CREATE TABLE produit (
     id BIGINT NOT NULL AUTO_INCREMENT,
     description VARCHAR(255),
     nom VARCHAR(255),
     prix DECIMAL(38,2),
     fournisseur_id BIGINT NOT NULL,

     PRIMARY KEY (id),

     CONSTRAINT fk_produit_fournisseur
         FOREIGN KEY (fournisseur_id)
             REFERENCES fournisseur(id)
);

CREATE TABLE commande (
  id BIGINT NOT NULL AUTO_INCREMENT,
  date_commande DATETIME(6),
  statut ENUM(
    'ANNULEE',
    'CONFIRMEE',
    'EN_ATTENTE',
    'EN_PREPARATION',
    'LIVREE',
    'PRETE'
    ),
  total_prix DECIMAL(38,2),
  client_id BIGINT,

  PRIMARY KEY (id),

  CONSTRAINT fk_commande_client
      FOREIGN KEY (client_id)
          REFERENCES client(id)
);

CREATE TABLE ligne_commande (
    id BIGINT NOT NULL AUTO_INCREMENT,
    prix_unitaire DECIMAL(38,2),
    quantite INT,
    commande_id BIGINT,
    produit_id BIGINT,

    PRIMARY KEY (id),

    CONSTRAINT fk_ligne_commande_commande
        FOREIGN KEY (commande_id)
            REFERENCES commande(id),

    CONSTRAINT fk_ligne_commande_produit
        FOREIGN KEY (produit_id)
            REFERENCES produit(id)
);

CREATE TABLE stock (
   id BIGINT NOT NULL AUTO_INCREMENT,
   quantite INT,
   produit_id BIGINT,

   PRIMARY KEY (id),

   CONSTRAINT uk_stock_produit
       UNIQUE (produit_id),

   CONSTRAINT fk_stock_produit
       FOREIGN KEY (produit_id)
           REFERENCES produit(id)
);