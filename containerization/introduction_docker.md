---
marp: true
theme: gaia
class: lead
paginate: true
---

![width:150px](./assets/logo_docker.png)

# Conteneurisation

#### Avec Docker

###### Cours d'introduction

---

### C'est quoi la conteneurisation ?

---

La conteneurisation est le processus grâce auquel une application peut être empaquetée avec toutes ses dépendances afin de pouvoir être exécutée sur différentes machines

---

### C'est quoi Docker ?

---

La techno qui nous permet de conteneuriser !

---

#### Un peu d'histoire

---

- Crée en 2013 par Solomon Hykes -> franco-américain

- Dévient populaire en 2014

- Apparution de Kubernetes en 2015

- Standard su marché en 2020

---

A retenir : Docker n'a pas invinté la conteneurisation (2000 et avant), il l'a juste facilité et rendu très populaire !

---

## Le DevOps

---

- Les devs d'un côté

- Les ops d'un autre

---

Mais pas ensemble ! 🤬

---

![width:800px](./assets/devops_schema.png)

---

##### Infrastructure as code

---

On revient sur Docker...

---

#### Les problèmes

---

###### Problème 1

- Harmoniser les environnements de developpement : installations, config, commandes dans le terminal, etc

---

- Pas besoin d'installer des logiciels sur notre PC : le conteneur est un environnement isolé où habite le logiciel.

On lance le service avec une commande 🪄

\*et c'est toujours la même !

---

Nous pouvons même avoir plusieurs service avec des versions differentes !

---

###### Problème 2

- Faciliter le déploiement des applications

---

Avant, installer des applications c'était l'infer 🔥🔥🔥

instrctions, conf, versions, bdd, app, scripts, dépendences, livrables, etc.

---

Et aussi : la communication entre devs et ops 💀

---

Dans un container on à tout dedans !

Il suffi mantenant d'apprendre Docker

Plus besoin de configurer le serveur ! (plus ou moins)

---

### Docker : un ourtil de virtualisation ?

ça dépend !

---

##### VM's vs Docker

Différences & avantages

---

Mais avant, il faut commprendre comment fonctionne Docker

---

Si je n'ai pas besion d'installer mes outils sur mon PC, comment Docker est-il capable de les lancer ?

---

![width:800px](./assets/os_layers_arch_simple.png)

---

Quel parti est virtualisée ?

---

Une VM virtualise tout !

---

![width:700px](./assets/vm_arch.png)

---

Docker virtualise la couche Applicative

- Plus ou moins : contrairement à une VM, un conteneur ne possède pas son propre kernel. Les conteneurs Linux partagent le kernel Linux de leur hôte. Sur Windows avec Docker Desktop, cet hôte Linux est généralement fourni par une VM WSL 2.

- En résumé : Docker isole les processus et les ressources en partageant le kernel de l'hôte

---

- Un conteneur contient la couche Application d'un OS (Linux)

- Il contient aussi des applications

- Il utiliser le Kernel du host !

---

![width:700px](./assets/docker_arch.png)

---

- Avantages
  - ➕ Espace
  - ➕ Vitesse

</br>

- Inconveniants
  - ➖ Compatibilité

---

➖ Compatibilité ?

---

La majorité des services connus ont des images Docker basées sur Linux

Docker a été construit pour Linux

🥶

---

Docker Desktop
(2016)

---

![width:800px](./assets/docker_on_windows_arch.png)

---

Pourquoi Docker ?

---

Vers une architecture de microservices 🧩

---

~~Applis monolitiques~~

---

Docker nous simplifier la vie !

---

### Docker

##### Images et contereurs

---

C'est quoi une image ?

---

Un paquet avec tout

- Artifact de notre application
  - Code source compilé
  - Tous les services et dépendences nécessaires.

- Environnement
  - Dossiers, configuration, variables d'environnement, etc.

---

C'est quoi un conteneur ?

---

L'espace où l'application s'execute avec toutes ses dependences, services, conf, etc.

---

Pour résumer

###### Image : le template qui permet de créer un conteneur

###### Conteneur : l'instance d'une image Docker

-> Il possède un cycle de vie : créé, démarré, arrêté, supprimé

---

Nous pouvons instancier notre image à l'infinie !

---

Mais, comment obtenir une image ? 🤔

---

Deux cas :

- Créer nos propres images et les publier sur un registry

- Utiliser une image existante d'un registry comme Docker Hub
  - Images officielles Docker 📑

---

Versionning d'images -> image tags

-> Best practice : définir une vérsion, (pas 'latest')

---

Nous pouvons télécharger nos images Docker

---

## Docker network

---

Comment accèder à mon conteneur ?

Comment parlent entre eux les conteneurs ?

---

##### Côté Docker

Sur le résseau Docker virtuel, chaque conteneur possède :

- Une addresse IP privé sur le résseau Docker

- Un ou plusieurs ports sur lesquels les applications du conteneur écoutent

---

##### Côté host

Port binding : permet de lier le port d'un conteneur à un port du host

Uniquement un conteneur par port du host !

---

Best practice

- Utiliser le port standart d'un service

---

## Comment créer une image Docker ?

---

Déclarer une définition de comment on construit une image de notre application

---

#### Le Dockerfile

---

- Il contient les instructions qui permettent à Docker de créer une image de notre application

- Il posède sa propre syntaxe

---

#### Layering des images

---

![width:900px](./assets/docker_image_layering.png)

---

### Docker Volumes 💾

Les données écrites dans un conteneur peuvent disparaître à tout moment ! 💀

---

- Stocker les données en dehors du système de fichiers du conteneur
- Conserver les données même si le conteneur est supprimé
- Réutiliser les données avec un nouveau conteneur

---

C'est l'heure des TP's !

---

## Docker compose

---

Complexification applicative

---

Baucoup de conteneurs

---

![width:700px](./assets/head_pain.jpg)

---

Docker Compose

Un outil Docker qui permet de définir, grouper et de gérer plusieurs conteneurs

---

-> retour aux TP pour l'exemple pratique !

---

## CI/CD

#### Avec Gitlab

---

GitLab est une plateforme qui regroupe plusieurs outils DevOps

---

- Gestion du code source avec Git
- Collaboration entre développeurs
- Automatisation :
  - Production des images de notre application : CI
  - Déploiement de nos images : CD

---

### GitLab Pipelines

Outil qui permet l'automatisation des taches

- Une pipeline contient :
  - **Jobs** : tâches à exécuter
  - **Stages** : étapes regroupant les jobs
  - **Runners** : exécutent les jobs

---

**`.gitlab-ci.yml`**

---

Il y a d'autres alternatives, comme Jenkins

---

### Soyez curieux 🤓
