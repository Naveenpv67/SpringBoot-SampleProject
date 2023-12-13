# syntax=docker/dockerfile:1

FROM golang:1.19-alpine

WORKDIR /app

COPY . .

RUN go build -o docker-gs-ping

EXPOSE 8080

CMD ["./docker-gs-ping"]
