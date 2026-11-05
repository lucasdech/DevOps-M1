---
marp: true

theme: gaia

class: lead

paginate: true
---

**# Backlog**

---

**# US 1**

- Ajouter pgAdmin dans un conteneur Docker

- Ajouter Redis dans un conteneur Docker

- Assurer la persistance des données PostgreSQL et Redis avec des volumes Docker

- Configurer les services dans compose.yaml

---

**# US 2**

- Ajouter les fonctionnalités suivantes à l'application :
  - Rotation d'image (90°, 180°, 270°)
  - Compléter les fonctionnalités de redimensionnement
  - Conversion de format (PNG, JPEG, WebP, etc.)
  - Génération de miniatures (thumbnails)

- Ajouter un minimum des TU (au moins 1 par feature 🫪)

---

**# US 3.1 - Workers asynchrones**

- Créer deux nouveaux services :
  - bimag-size-worker : redimensionnement, rotation, création thumbnails
  - bimag-format-worker : conversion de format

- Modifier bimag-api :
  - L'API ne traite plus directement les images
  - Elle publie des jobs dans Redis

- Les workers consomment les jobs et mettent à jour le statut des jobs en base de données

---

**# US 3.2 - Cinématique attendue**

- Le client envoie POST /images/process
- L'API crée un job et retourne immédiatement :
  - HTTP 202 Accepted
  - Un identifiant job_id
- Le worker concerné récupère le job dans Redis
- Le worker traite l'image et met à jour son statut en base
- Le client interroge GET /jobs/{job_id}
  - Lorsque le job est terminé, le client récupère l'image

---

**# US 3.3 - Architecture Docker**

- Chaque worker est exécuté dans un conteneur distinct

- Redis est exécuté dans son propre conteneur

- Les services communiquent via le réseau Docker Compose

- Utiliser un volume Docker partagé pour rendre les images originales et générées accessibles à l'API et aux workers si besoin

---

**# US 3.4 - Architecture Redis**

- Deux files Redis distinctes :
  - bimag:jobs:size → bimag-size-worker
  - bimag:jobs:format → bimag-format-worker

- Les jobs possèdent un statut :
  - pending
  - processing
  - completed
  - failed

---

**# US 4 - CI/CD Gitlab**

- Installer et configurer un GitLab Runner local

- Créer un pipeline GitLab CI fonctionnel qui doit :
  - Exécuter les tests unitaires
  - Builder les images Docker
  - Publier les images sur Docker Hub
  - Déployer l'application en local avec Docker Compose

---

⚠️ Le déploiement doit utiliser les images publiées sur Docker Hub et ni les reconstruire localement ni utiliser vos images locales

---

**# US Bonus 1**

- Ajouter Redis Insight pour visualiser les files Redis

- Ajouter des healthchecks Docker

- Ajouter des limites de CPU et de mémoire aux workers

---

**# US Bonus 2**

- Rédiger un `README.md` expliquant :
  - L'architecture globale de l'application
  - Le rôle de chaque service Docker
  - Les instructions pour installer et démarrer le projet

- Ajouter un schéma d'architecture :
  - API, Redis, PostgreSQL et workers
  - Flux de communication entre les services

---

**# Le boss final 💀 : Résilience et tolérance aux pannes**

- Implémenter un mécanisme de retry automatique :
  - Maximum 3 tentatives par job
  - Délai progressif entre les tentatives

- Ajouter une Dead Letter Queue (DLQ) Redis :
  - `bimag:jobs:failed`
  - Les jobs définitivement échoués sont transférés dans cette file

- Gérer les interruptions des workers :
  - Un job ne doit pas être perdu si un worker s'arrête brutalement
  - Les jobs bloqués en `processing` doivent pouvoir être récupérés

---

**# Le boss final 2ème phase : Chaos Engineering 🔥**

- Utiliser Redis Streams + Consumer Groups -> ⚠️réfléchir à l'architecture⚠️

- Lancer plusieurs instances de `bimag-size-worker` avec Docker Compose

- Soumettre 20 jobs simultanément

- Arrêter brutalement un worker pendant le traitement :
  - `docker kill <worker_container>`

---

- Vérifier que :
  - Les jobs ne sont pas perdus
  - Les jobs interrompus sont repris
  - Le traitement des jobs doit être idempotent : un job rejoué ne doit pas produire de résultats incohérents ni d'effets secondaires indésirables.
  - Les autres workers continuent de fonctionner
