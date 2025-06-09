# nook

After instlaling Buffalo, run these commands to get a development database going: 
```
docker pull postgres
docker run --name postgres -e POSTGRES_PASSWORD=pstgrspw -p 5432:5432 -d postgres
```
