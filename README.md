Aerospike Database Backup and Restore in Docker
Backup Steps:
Create Backup Directory:

bash
Copy code
docker exec -it <containerId> /bin/bash
mkdir backup
Perform Backup:

bash
Copy code
asbackup -n <namespace> -d backup
This creates a namespace.asb file in the backup folder.

Copy Backup to Local Machine:

bash
Copy code
docker cp <containerId>:/backup ~/Desktop
Restore Steps:
Create Restore Directory Inside Container:

bash
Copy code
docker exec -it <containerId> /bin/bash
mkdir bckupFile
Copy Backup File from Local Machine to Docker Container:

bash
Copy code
docker cp ~/Desktop/backup/namespace.asb <containerId>:/bckupFile
Navigate to Docker Container:

bash
Copy code
docker exec -it <containerId> /bin/bash
Perform Restore:

bash
Copy code
asrestore -n <namespace> -d bckupFile
Replace <namespace> with the actual namespace name.


Note: Ensure that the Aerospike container is running and replace placeholders such as <containerId> and <namespace> with your specific values.




Aerospike Database Backup and Restore in Podman
Backup Steps:
Create Backup Directory:

bash
Copy code
podman exec -it <containerId> /bin/bash
mkdir backup
Perform Backup:

bash
Copy code
asbackup -n <namespace> -d backup
This creates a namespace.asb file in the backup folder.

Copy Backup to Local Machine:

bash
Copy code
podman cp <containerId>:/backup ~/Desktop
Restore Steps:
Create Restore Directory Inside Container:

bash
Copy code
podman exec -it <containerId> /bin/bash
mkdir bckupFile
Copy Backup File from Local Machine to Podman Container:

bash
Copy code
podman cp ~/Desktop/backup/namespace.asb <containerId>:/bckupFile
Navigate to Podman Container:

bash
Copy code
podman exec -it <containerId> /bin/bash
Perform Restore:

bash
Copy code
asrestore -n <namespace> -d bckupFile
Replace <namespace> with the actual namespace name.

Note: Ensure that the Aerospike container managed by Podman is running, and replace placeholders such as <containerId> and <namespace> with your specific values.


