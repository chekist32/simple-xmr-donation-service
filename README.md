# Simple monero donation service 

Selfhosted monero donation system written with [Spring Boot](https://spring.io/projects/spring-boot), Java (backend) and [React](https://react.dev/), JavaScript (frontend).

- Provides an admin page where user can make some minor customizations.
- Provides a notification functionality via donation link, that can be used with OBS browser source.


## Installation


### Docker

In the root folder you can find ```docker-compose.yaml``` file which is populated with example configuration.
```yml
version: '3'
services:
  
  postgres-db:
    image: postgres:16-alpine
    restart: always
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_DB=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "54321:5432"
    volumes:
        # Here you should paste a path (directory) where 
        # postgres-db container will persist its data.
      - /var/docker_data/simple_monero_donation_service/postgres:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

  monero-rpc:
    image: sethsimmons/simple-monero-wallet-rpc:latest
    restart: unless-stopped
    ports:
      - 38083:38083
    volumes:
        # Here you should paste a path (directory) where the view-only monero wallet is located.
      - /var/docker_data/simple_monero_donation_service/monero/wallets/test1_wallet:/home/monero/wallet
    command: 
        # In production should be set to "mainnet"
      - "--stagenet" 
      - "--daemon-address=stagenet.community.rino.io:38081"
      - "--trusted-daemon" 
      - "--rpc-bind-port=38083" 
        # Credentials for accessing the monero wallet rpc (optional) 
      - "--rpc-login=user:pass" 
      - "--wallet-dir=/home/monero/wallet"

  backend:
    build:
      context: ./backend
    env_file:
        # Here you can paste a path to the .env file for backend container.
        # Example can be found under backend/env.example
      - ./backend/.env.dev
    ports:
      - 8081:8080
    depends_on:
      - monero-rpc
      - postgres-db

  frontend-client:
    build:
      context: ./frontend
      dockerfile: client/Dockerfile
      args:
          # A url mapped to the backend container
        - API_BASE_URL=http://localhost:8081
    ports:
      - "81:80"
    depends_on:
      - backend

  frontend-admin:
    build:
      context: ./frontend
      dockerfile: admin-panel/Dockerfile
      args:
          # A url mapped to the backend container
        - API_BASE_URL=http://localhost:8081
    ports:
      - "82:80"
    depends_on:
      - backend
```
This configuration can already be used in test purposes. Steps you need to complete in order to be able to run:
1. Create ```/var/docker_data/simple_monero_donation_service/postgres``` directory.
2. Create a monero view_only stagenet (for production setups mainnet) wallet and put it inside ```/var/docker_data/simple_monero_donation_service/monero/wallets/test1_wallet``` directory.
3. On the base of ```backend/.env.example``` 
``` shell
# The name of your wallet file
MONERO_WALLET_PATH=test1 
# A password for wallet private keys unlocking (optional)
MONERO_WALLET_PASSWORD=  
# A monero wallet rpc host:port 
MONERO_RPC_SERVER_URL=http://monero-rpc:38083
# A username for accessing the monero walet rpc (optional) 
MONERO_RPC_SERVER_USERNAME=user 
# A password for accessing the monero walet rpc  (optional)  
MONERO_RPC_SERVER_PASSWORD=pass


POSTGRES_DB_HOST=postgres-db:5432
POSTGRES_DB_NAME=monero_donation_service_db
POSTGRES_DB_USERNAME=postgres
POSTGRES_DB_PASSWORD=postgres


SMTP_HOST= # your smtp host (for example: smtp.gmail.com)
SMTP_PORT=  # your smtp port (for example: 587)
SMTP_USERNAME= # your smtp username (for example: example@gmail.com)
SMTP_PASSWORD= # your smtp password (optional)

# A url mapped to the frontend-admin container (default http://localhost:82)
ADMIN_PANEL_UI_URL=http://localhost:82  

# allowed origins in format [ https://example1.com,https://example2.com ]
# (basically the urls mapped to frontend-admin and frontend-client containers)
ALLOWED_ORIGINS=http://localhost:81,http://localhost:82  
```
create ```.env.dev``` file and put it inside ```backend``` directory.

4. Run 
```bash 
    docker-compose up
``` 

## Usage 
Here is the preview video of usage. 

https://github.com/chekist32/simple-xmr-donation-service/assets/41333847/2d6ae027-1d35-4cb7-a09d-1b2c45fa783a


## Known Issues
1. Lack of edit functionality for profile avatar on admin page.
2. Lack of time zone conversion on admin page.


## Further Development
1. Add ability to donate in other cryptocurrencies (at least in BTC and LTC).
