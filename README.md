# syntax=docker/dockerfile:1

FROM golang:1.19 AS builder

# Set destination for COPY
WORKDIR /app

# Download Go modules
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the application code
COPY . .

# Build the Go application
RUN CGO_ENABLED=0 GOOS=linux go build -o docker-gs-ping

FROM gcr.io/distroless/base-debian10

WORKDIR /app

# Copy the binary from the builder stage
COPY --from=builder /app/docker-gs-ping .

EXPOSE 8080

CMD ["./docker-gs-ping"]
