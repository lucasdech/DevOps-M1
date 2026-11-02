# Dockerfile Multi-stage Build

# Directive pour ajouter une image existante Docker
# AS build permet de donner un nom à cette étape et de l'utiliser plus tard
FROM node:22-alpine AS build

# Directive pour changer de répertoire dans le conteneur (équivalent d'un cd dans Linux)
# Toutes les directives suivantes seront faites dans /app
WORKDIR /app 

# Directive pour copier  notre package.json dans ./ (dans le conteneur)
COPY package*.json ./

# Directive pour lancer une commande dans le conteneur
# On instal les dépendences du projet
RUN npm install

# Copie toutes les fichiers sauf les fichiers dans un .dockerignore
COPY . .

# On build l'application avec le code source
RUN npm run build


# Une fois l'application buildée, on n'a plus besoin de Node
# Nginx prend le relais pour services les scripts HTML, CSS et js
FROM nginx:alpine

# On copie le dist vers le bon repértoire ngnix
COPY --from=build /app/dist /usr/share/nginx/html
# On copie la conf nginx vers le bon repértoire
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Directive pour exposer sur le port 80 du conteneur
EXPOSE 80

# Directive pour définir les commandes qui seront lancées au démarrage du conteneur
CMD ["nginx", "-g", "daemon off;"]

# Le dernier FROM défini l'image Docker finale
# L'avantage principal de faire deux étapes est que l'image finale contient seulement Nginx et les fichiers nécessaires pour servir l'application
# Elle ne contient pas Node.js, npm ou les outils de compilation de la première étape
# Résultat : l'image est plus légère, plus simple et avec moins de composants inutiles
# À retenir pour ce cas précis : Node.js construit l'application, Nginx la sert aux users