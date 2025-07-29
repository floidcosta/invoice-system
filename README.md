# Invoice system using docker compose, spring boot & mongoDB 

## Build the system

```bash
docker compose build --no-cache
```
## Run the System
We can easily run the whole with only a single command:
```bash
docker compose up -d
```

## api to test

http://localhost:8080/api/invoices

## swagger
http://localhost:8080/swagger-ui/index.html


## Run the sytem in detacted mode
The services can be run on the background with command:
```bash
docker compose up -d
```

## Stop the System
Stopping all the running containers is also simple with a single command:
```bash
docker compose down
```

If you need to stop and remove all containers, networks, and all images used by any service in <em>docker-compose.yml</em> file, use the command:
```bash
docker compose down --rmi all
```

If you indent to remove and still there are references

```bash
docker ps
docker rm -f <image id> 
```