ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode respStatNode = rootNode
                .path("S:Envelope")
                .path("S:Body")
                .path("ns11:doDematLandingPageResponse")
                .path("return")
                .path("respstat");
