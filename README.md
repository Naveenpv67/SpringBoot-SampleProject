package com.example.demo.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String json = "{\r\n"
        		+ "    \"S:Envelope\": {\r\n"
        		+ "        \"xmlns:S\": \"http://schemas.xmlsoap.org/soap/envelope/\",\r\n"
        		+ "        \"S:Body\": {\r\n"
        		+ "            \"ns11:doDematLandingPageResponse\": {\r\n"
        		+ "                \"xmlns:ns10\": \"http://dto.common.domain.framework.fc.ofss.com\",\r\n"
        		+ "                \"return\": {\r\n"
        		+ "                    \"msghdr\": {\r\n"
        		+ "                        \"resptmstmp\": 20240531074814,\r\n"
        		+ "                        \"msgtp\": \"DBACTLSTRP\",\r\n"
        		+ "                        \"respapp\": \"DEBOS\"\r\n"
        		+ "                    },\r\n"
        		+ "                    \"readtis\": {\r\n"
        		+ "                        \"regrefno\": 3415605,\r\n"
        		+ "                        \"custid\": 50000045\r\n"
        		+ "                    },\r\n"
        		+ "                    \"respstat\": {\r\n"
        		+ "                        \"respmsg\": \"Duplicate request reference number 3415605\",\r\n"
        		+ "                        \"respcode\": 0\r\n"
        		+ "                    },\r\n"
        		+ "                    \"debosdata\": {\r\n"
        		+ "                        \"acctlist\": {\r\n"
        		+ "                            \"acctdil\": {\r\n"
        		+ "                                \"mfund\": \"\"\r\n"
        		+ "                            }\r\n"
        		+ "                        }\r\n"
        		+ "                    }\r\n"
        		+ "                }\r\n"
        		+ "            }\r\n"
        		+ "        }\r\n"
        		+ "    }\r\n"
        		+ "}";

        try {
            Object result = parseResponse(json);
            System.out.println(result);
        } catch (IOException e) {
            System.err.println("Invalid JSON payload");
        }
    }

    public static Object parseResponse(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode respStatNode = rootNode
                .path("S:Envelope")
                .path("S:Body")
                .path("ns11:doDematLandingPageResponse")
                .path("return")
                .path("respstat");

        int respcode = respStatNode.path("respcode").asInt();
        String respmsg = respStatNode.path("respmsg").asText();

        if (respcode != 0) {
            return "Error: " + respmsg;
        } else {
            JsonNode debosDataNode = rootNode
                    .path("S:Envelope")
                    .path("S:Body")
                    .path("ns11:doDematLandingPageResponse")
                    .path("return")
                    .path("debosdata");
            return debosDataNode;
        }
    }
}
