# 💊 PharmacieAPI

API REST de gestion d'une pharmacie développée avec **Java 21** et **Spring Boot 4.1**.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.4-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)
![CI](https://github.com/kamaraseydi/pharmacie-api/actions/workflows/ci.yml/badge.svg)

---

## 🚀 Démarrage rapide

```bash
git clone https://github.com/kamaraseydi/pharmacie-api.git
cd pharmacie-api

cp .env.example .env          # Renseigner JWT_SECRET
docker compose up
```

L'API démarre sur **http://localhost:8080**  
La documentation Swagger est disponible sur **http://localhost:8080/swagger-ui.html**

---

## 📌 Présentation

**PharmacieAPI** est une API REST permettant de gérer les principales ressources d'une pharmacie :

- 👤 **Clients** — inscription, authentification, gestion du profil
- 📦 **Produits** — catalogue rattaché aux fournisseurs
- 🏭 **Fournisseurs** — gestion des partenaires
- 📊 **Stocks** — suivi des quantités avec contrôle de concurrence
- 🛒 **Commandes** — cycle de vie complet avec machine à états
- 🔐 **Authentification** — JWT via Spring Security

---

## 🛠️ Technologies

| Couche | Technologie |
|--------|-------------|
| Backend | Java 21, Spring Boot 4.1 |
| Sécurité | Spring Security, JWT (oauth2-resource-server) |
| Base de données | MySQL 8.4, Spring Data JPA, Flyway |
| Tests | JUnit 5, Mockito, Testcontainers |
| Infra | Docker, Docker Compose, GitHub Actions |
| Documentation | Swagger / OpenAPI |

---

## 🏗️ Architecture

```
HTTP Request
     │
     ▼
┌───────────┐
│ Controller│  ← validation des entrées, réponses HTTP
└─────┬─────┘
      │
      ▼
┌───────────┐
│  Service  │  ← logique métier, règles, transactions
└─────┬─────┘
      │
      ▼
┌───────────┐
│ Repository│  ← accès base de données (JPA)
└─────┬─────┘
      │
      ▼
  ┌───────┐
  │ MySQL │
  └───────┘
```

Les entités ne sortent jamais directement de l'API — des **DTO** et **Mappers** assurent la séparation entre le modèle de données et les contrats d'API.

---

## ⚙️ Prérequis

**Avec Docker (recommandé)**
- Docker Desktop

**Sans Docker**
- Java 21
- MySQL 8.4
- Maven (ou utiliser `./mvnw`)

---

## 🌍 Variables d'environnement

Copier `.env.example` en `.env` et renseigner les valeurs :

```env
SPRING_PROFILES_ACTIVE=dev

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/pharmacie
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=

JWT_SECRET=change-me-with-a-long-random-secret-key
```

> ⚠️ Le fichier `.env` est ignoré par Git. Ne jamais committer de secrets.

---

## 🐳 Docker

```bash
# Lancer l'API + MySQL
docker compose up

# En arrière-plan
docker compose up -d

# Arrêter et supprimer les conteneurs
docker compose down
```

L'image MySQL utilise un **volume nommé** : les données sont conservées entre les redémarrages.

Un **healthcheck** garantit que MySQL est prêt avant le démarrage de l'API.

---

## 💻 Développement local (sans Docker)

```bash
# Exporter le profil
export SPRING_PROFILES_ACTIVE=dev

# Lancer l'application
./mvnw spring-boot:run
```

---

## 🗄️ Migrations Flyway

Le schéma est versionné avec Flyway :

```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_date_naissance_client.sql
└── ...
```

Flyway s'exécute automatiquement au démarrage et applique les migrations manquantes.

> ⚠️ Un fichier de migration déjà appliqué ne doit jamais être modifié. Tout changement passe par un nouveau fichier.

---

## 🧪 Tests

```bash
# Tous les tests
./mvnw clean test

# Build sans tests
./mvnw clean package -DskipTests
```

Le projet contient :

- **Tests unitaires** — services testés avec Mockito (logique métier, machine à états, contrôle d'accès)
- **Tests de sécurité** — vérification des règles d'autorisation par rôle
- **Tests des contrôleurs** — couche HTTP
- **Test d'intégration** — `ajouterCommande` avec un vrai MySQL via Testcontainers

---

## 📖 Documentation API

Swagger est disponible en développement :

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

> Swagger est désactivé en production.

### Endpoints principaux

| Méthode | Endpoint | Rôle |
|---------|----------|------|
| `POST` | `/auth/register` | Inscription client |
| `POST` | `/auth/login` | Connexion → JWT |
| `GET` | `/produits` | Liste des produits |
| `POST` | `/commandes` | Créer une commande |
| `GET` | `/commandes` | Mes commandes (CLIENT) / toutes (ADMIN) |
| `PATCH` | `/commandes/{id}/statut` | Modifier le statut (ADMIN) |
| `DELETE` | `/commandes/{id}` | Annuler une commande |
| `GET` | `/stocks` | État des stocks (ADMIN) |

> La documentation complète avec les requêtes et réponses est disponible sur Swagger.

---

## 🔐 Authentification

```
POST /auth/login
Content-Type: application/json

{
  "email": "client@example.com",
  "motDePasse": "password"
}
```

Utiliser le token retourné dans les requêtes suivantes :

```
Authorization: Bearer <token>
```

---

## 🔄 CI/CD

Le pipeline GitHub Actions s'exécute à chaque Pull Request vers `main` :

```
Checkout
   │
   ▼
Setup Java 21
   │
   ▼
Tests (avec Testcontainers)
   │
   ▼
Build
```

---

## 🌿 Branches

```
main
 ├── feature/authentication
 ├── feature/commande
 ├── fix/deadlock-stock
 └── docs/readme
```

Chaque modification passe par une Pull Request avant d'être fusionnée dans `main`.

---

## 👨‍💻 Auteur

**Seydi Kamara** — Étudiant en Génie Logiciel (L2), ISI Dakar  
Projet réalisé dans le cadre de l'apprentissage du développement backend Java/Spring Boot.

[![GitHub](https://img.shields.io/badge/GitHub-kamaraseydi-181717?logo=github)](https://github.com/kamaraseydi)