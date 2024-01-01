# Step 1: Use the official GoLang image as the base platform
FROM golang:1.20

# Step 2: Copy the pre-built executable file into the container
COPY ./goservice /goservice

# Step 3: Specify how to run the service
ENTRYPOINT ["/goservice"]

