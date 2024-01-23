FROM node:21.5.0-alpine as builder

ARG API_BASE_URL

ENV VITE_API_BASE_URL=$API_BASE_URL

WORKDIR /app/frontend
COPY ./package*.json ./
RUN npm install

COPY ./shared ./shared

WORKDIR /app/frontend/admin-panel
COPY ./admin-panel/package*.json ./
RUN npm install

COPY ./admin-panel .
RUN npm run build


FROM nginx:stable-alpine

COPY ./admin-panel/nginx.conf /etc/nginx/conf.d/default.conf

WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/frontend/admin-panel/dist .

ENTRYPOINT ["nginx", "-g", "daemon off;"]

