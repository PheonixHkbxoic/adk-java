package io.github.pheonixhkbxoic.adk.uml;

import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.node.End;
import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.core.node.Group;
import io.github.pheonixhkbxoic.adk.core.node.Start;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractGroupNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 17:36
 * @desc plantuml generator to generate plantuml string and png
 * preference: {@see https://plantuml.com/zh/activity-diagram-beta}[preference]
 * and online editor: {@see https://www.plantuml.com/plantuml/uml}[online editor]
 */
public class PlantUmlGenerator {

    /**
     * geneate plantuml string of graph
     *
     * @param graph graph
     * @return plantuml string
     */
    public String generate(Graph graph) {
        StringBuilder uml = new StringBuilder();
        uml.append("@startuml\n" +
                "start\n");
        uml.append(this.buildUmlNode(graph, -1, -1, false, false));
        uml.append("stop\n");
        uml.append("@enduml");
        return uml.toString();
    }

    private StringBuilder buildUmlNode(Node node, int indent, int indentStartNode, boolean inSwitch, boolean appendEnd) {
        indent++;
        StringBuilder uml = new StringBuilder();
        if (node instanceof Graph) {
            uml.append(reapeat(indent)).append(String.format("partition **%s** {", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Graph) node).getStart(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(reapeat(indent)).append("}").append("\n");
        } else if (node instanceof AbstractGroupNode) {
            uml.append(reapeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Group) node).getEntry(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(reapeat(indent)).append("end group").append("\n");
        } else if (node instanceof AbstractBranchesNode) {
            uml.append(reapeat(indent)).append(String.format("switch( %s? )", node.getName())).append("\n");
            indent++;
            List<Edge> edgeList = ((AbstractBranchesNode) node).getEdgeList();
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                uml.append(reapeat(indent)).append(String.format("case ( %s )", edge.getName())).append("\n");
                boolean appendEndFlag = i == edgeList.size() - 1;
                uml.append(this.buildUmlNode(edge.getNode(), indent, indentStartNode, true, appendEndFlag));
            }

            // move ": end;" in switch to after "endswitch;"
            String endInSwitch = ": end;\n";
            int endInSwitchIndex = uml.lastIndexOf(endInSwitch);
            int endInSwitchIndexEnd = endInSwitchIndex + endInSwitch.length();
            if (endInSwitchIndex > -1) {
                while (endInSwitchIndex > 0) {
                    if (uml.charAt(endInSwitchIndex - 1) == ' ') {
                        endInSwitchIndex--;
                    } else {
                        break;
                    }
                }
                uml = uml.replace(endInSwitchIndex, endInSwitchIndexEnd, "");
            }
            indent--;
            uml.append(reapeat(indent)).append("endswitch").append("\n");
            if (endInSwitchIndex > -1) {
                uml.append(reapeat(indent)).append(endInSwitch);
            }
        } else if (node instanceof AbstractChainNode) {
            if (node instanceof Start) {
                indentStartNode = indent;
                uml.append(reapeat(indentStartNode)).append(String.format(": %s;", node.getName())).append("\n");
            } else if (node instanceof End) {
                if (!inSwitch || appendEnd) {
                    uml.append(reapeat(indentStartNode)).append(String.format(": %s;", node.getName())).append("\n");
                }
            } else {
                uml.append(reapeat(indent)).append(String.format(": %s;", node.getName())).append("\n");
            }
            Edge edge = ((AbstractChainNode) node).getEdge();
            if (edge != null) {
                uml.append(this.buildUmlNode(edge.getNode(), indent, indentStartNode, inSwitch, appendEnd));
            }
        }
        return uml;
    }

    private String reapeat(int indent) {
        return "  ".repeat(indent);
    }

    public void generatePng(Graph graph, OutputStream outputStream) throws IOException {
        String source = this.generate(graph);
        System.out.println(source);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream);
    }

    public ByteArrayOutputStream generatePng(Graph graph) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generatePng(graph, os);
        return os;
    }
}
