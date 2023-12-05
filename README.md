import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JsonGraphVisualization {

    public static void main(String[] args) {
        String jsonData = "[{\"Pk\":1,\"CustId\":11,\"custName\":\"abc\"}," +
                          "{\"Pk\":10,\"AccNum\":123,\"Acctype\":\"CASA\",\"CustId\":11}," +
                          "{\"Pk\":13,\"CardNum\":1234,\"AccNum\":123,\"CustId\":11}]";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode[] jsonNodes = objectMapper.readValue(jsonData, JsonNode[].class);

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

            visualizeGraph(graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void visualizeGraph(DirectedSparseGraph<String, String> graph) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        VisualizationImageServer<String, String> vs =
                new VisualizationImageServer<>((String s) -> s, (s) -> Color.GREEN, (s) -> Color.GREEN);
        vs.setPreferredSize(new Dimension(400, 400));

        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);

        vs.getRenderContext().setVertexLabelTransformer((String s) -> s);
        vs.getRenderContext().setEdgeLabelTransformer((String s) -> s);

        vs.getRenderContext().setVertexLabelTransformer(Object::toString);
        vs.getRenderContext().setEdgeLabelTransformer(Object::toString);

        vs.getRenderContext().setVertexFillPaintTransformer((String s) -> Color.GREEN);
    }
}
