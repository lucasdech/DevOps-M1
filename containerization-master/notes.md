## Commandes Docker

`docker pull <image-name:image-tag>` -> pull une image depuis Dockerhub

`docker ps` -> lister nos conteneur actifs

`docker ps -a` -> lister tous les conteneur

`docker images` -> lister nos images

`docker stop <container-name-or-id>` -> arrêter un container

`docker stop $(docker ps -q)` -> arrêter tous les conteneurs actifs

`docker build -t <app-name> .` -> builder une image

`docker run -d --name <container-name> <image-name>` -> lancer un conteneur à partir d'une image (conseillé)

`docker run <image-name>` -> commande de base pour lancer un conteneur (si l'image n'est pas dispo localement, Docker la télécharge automatiquement)

`docker logs <container-id>` -> voir les logs d'un conteneur

`docker network create <network-name>` -> créer un résseau Docker

`docker network ls` -> lister les networks Docker

`docker run -d -p <host-port>:<docker-port> <image-name>` -> run une image avec un port binding

`docker start <container-id>` -> relancer un conteneur déjà crée

`docker rmi -f $(docker images -aq)` -> supprimer toutes les images docker

`docker exec -it <container-id> /bin/bash` -> inspecter conteneur (exemple pour bdd : docker-db-demo psql
-U angel -d images)

### Docker compose

`docker compose up -d` -> créer et lancer un docker compose en mode 'detached'

`docker compose down` -> arreter et détrure les conteneurs Docker avec compose

`docker compose stop` -> arreter les services Docker avec compose

`docker compose start` -> lancer à nouveau les conteneurs compose

`docker compose restart` -> redemmarer les services compose

`docker compose logs` -> afficher les logs des conteneurs compose

`docker compose ps` -> lister les coneneurs compsoe

### Dockerhub

`docker tag <image_name>:<image_version> YOUR_DOCKER_USERNAME/<image_name>:<image_version>` -> tagguer une image

`docker login` se connecter à Dockerhub

`docker push YOUR_DOCKER_USERNAME/<image_name>:<image_version>` -> push une image

`docker pull YOUR_DOCKER_USERNAME/<image_name>:<image_version>` -> pull une image

# Runner

### Installation (Windows)

`New-Item -ItemType Directory -Force -Path C:\GitLab-Runner`

`Set-Location C:\GitLab-Runner`

`cd C:\GitLab-Runner`

`$url = "https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-windows-amd64.exe"`

`Invoke-WebRequest -Uri $url -OutFile .\gitlab-runner.exe`

`Get-Item .\gitlab-runner.exe | Select-Object Name, Length`

`.\gitlab-runner.exe --version`

### Registrer son runner

`.\gitlab-runner.exe register`

`.\gitlab-runner.exe list`

`.\gitlab-runner.exe verify`

`.\gitlab-runner.exe run`

### Autre

`$env:VAR_NAME = "var_value"` -> ajouter variable d'env au terminal Windows

## Liens

Repo supports cours : https://gitlab.com/AngelRafaelGO/containerization

Documentation docker : https://docs.docker.com/

Racourci documentation Docker compose : https://docs.docker.com/compose/

Docker cheat sheet : https://www.docker.com/resources/cli-cheat-sheet/

Téléchargement Docker Desktop : https://docs.docker.com/desktop/

Dockerhub : https://hub.docker.com/

Doc GitLab CI : https://docs.gitlab.com/ci/

Gitlab runner : https://docs.gitlab.com/runner/install/windows/

Documentation Redis : https://redis.io/docs/latest/

Documentation Lettuce : https://redis.github.io/lettuce/

## Redis

#### <span style="color: #FF10F0;">Lists</span> : structure native de Redis

- https://redis.io/docs/latest/develop/data-types/lists/

#### <span style="color: #FF10F0;">Lists avec acquittement</span> : mécanisme de fiabilité à construire soi-même avec les commandes Redis

- https://redis.io/docs/latest/commands/blmove/

#### <span style="color: #FF10F0;">Streams</span> : structure native de Redis avec des fonctionnalités de gestion des messages et d'acquittement via les Consumer Groups

Très complèxe 🥵

- https://redis.io/docs/latest/develop/data-types/streams/

## Liens pour les curieux

C'est quoi Docker (explication technique) : https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-a-container/

Site containered : https://containerd.io/

C'est quoi un hyperviseur : https://www.redhat.com/en/topics/virtualization/what-is-KVM

C'est quoi la virtualisation : https://developers.redhat.com/topics/virtualization
