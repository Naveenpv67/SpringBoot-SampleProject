podman run --name my_postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -v ~/postgres_data:/var/lib/postgresql/data -d postgres:latest
