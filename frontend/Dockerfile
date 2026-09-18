# --- Build ---
FROM node:24-alpine AS build
WORKDIR /app

# Couche dépendances séparée du code source : ne se réinstalle que si package*.json change.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration production

# --- Serve ---
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
