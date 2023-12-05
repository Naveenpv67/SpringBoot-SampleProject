<!-- Add JUNG library -->
<dependency>
    <groupId>net.sf.jung</groupId>
    <artifactId>jung-algorithms</artifactId>
    <version>2.1.1</version>
</dependency>
<dependency>
    <groupId>net.sf.jung</groupId>
    <artifactId>jung-api</artifactId>
    <version>2.1.1</version>
</dependency>
<dependency>
    <groupId>net.sf.jung</groupId>
    <artifactId>jung-graph-impl</artifactId>
    <version>2.1.1</version>
</dependency>
<dependency>
    <groupId>net.sf.jung</groupId>
    <artifactId>jung-visualization</artifactId>
    <version>2.1.1</version>
</dependency>

<!-- Spring Boot dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.apache.commons.collections15.Transformer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;

@SpringBootApplication
public class JsonGraphVisualizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonGraphVisualizationApplication.class, args);
    }

    @RestController
    public static class JsonGraphController {

        @PostMapping("/visualize")
        public ResponseEntity<String> visualizeGraph(@RequestBody String jsonData) {
            try {
                JsonNode[] jsonNodes = new ObjectMapper().readValue(jsonData, JsonNode[].class);
                DirectedSparseGraph<String, String> graph = new DirectedSparseGraph<>();

                for (JsonNode node : jsonNodes) {
                    String nodeLabel = node.fieldNames().next();
                    graph.addVertex(nodeLabel);

                    for (JsonNode child : node) {
                        String childLabel = child.fieldNames().next();
                        graph.addVertex(childLabel);
                        graph.addEdge(nodeLabel + "-" + childLabel, nodeLabel, childLabel);
                    }
                }

                return new ResponseEntity<>(getGraphVisualization(graph), HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error processing JSON data", HttpStatus.BAD_REQUEST);
            }
        }

        private String getGraphVisualization(DirectedSparseGraph<String, String> graph) {
            Transformer<String, Paint> vertexPaint = s -> Color.GREEN;
            VisualizationImageServer<String, String> vs =
                    new VisualizationImageServer<>(new GraphLabelTransformer(), vertexPaint, vertexPaint);
            vs.setPreferredSize(new Dimension(400, 400));

            vs.getRenderContext().setVertexLabelTransformer(new GraphLabelTransformer());
            vs.getRenderContext().setEdgeLabelTransformer(new GraphLabelTransformer());

            vs.getRenderContext().setVertexLabelTransformer(Object::toString);
            vs.getRenderContext().setEdgeLabelTransformer(Object::toString);

            vs.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

            return vs.toString();
        }

        private static class GraphLabelTransformer implements Transformer<Object, String> {
            @Override
            public String transform(Object input) {
                return input.toString();
            }
        }
    }
}

