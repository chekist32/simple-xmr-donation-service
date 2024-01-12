FROM node:21.5.0-alpine as builder

ENV VITE_API_BASE_URL=http://localhost:8081

WORKDIR /app/frontend/client
COPY ./package*.json ./
RUN npm install

COPY . .
RUN npm run build


FROM nginx:stable-alpine

COPY ./nginx.conf /etc/nginx/conf.d/default.conf

WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/frontend/client/dist .
ENTRYPOINT ["nginx", "-g", "daemon off;"]

