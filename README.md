# syntax=docker/dockerfile:1

FROM golang:1.19

# Copy CA certificates
COPY --from=scratch /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

# Set destination for COPY
WORKDIR /app

# Download Go modules
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the application code
COPY . .

# Build the Go application
RUN CGO_ENABLED=0 GOOS=linux go build -o docker-gs-ping

EXPOSE 8080

# Run the application
CMD ["./docker-gs-ping"]
