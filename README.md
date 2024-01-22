location / {
    set $platformId $http_platformId;
    error_log "Hello World";

    if ($platformId = "web") {
        proxy_pass http://localhost:7778;
    } else {
        proxy_pass http://localhost:7777;
    }
}
