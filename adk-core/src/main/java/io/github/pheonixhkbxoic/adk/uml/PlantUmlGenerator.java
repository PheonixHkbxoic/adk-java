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
import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import io.github.pheonixhkbxoic.adk.runtime.RouterContext;
import lombok.Data;
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
@Data
public class PlantUmlGenerator {
    private String INDENT_STR = "  ";
    private String activeLabelBgStyle = "#green";
    private String activeLabelStyle = "<color:red><b>";
    private String activeArrowStyle = "-[#red]->";

    /**
     * generate plantuml string of graph
     *
     * @param graph graph
     * @return plantuml string
     */
    public String generate(Graph graph) {
        return "@startuml\n" +
                "start\n" +
                this.buildUmlNode(graph, -1, -1, false, false) +
                "stop\n" +
                "@enduml";
    }

    private StringBuilder buildUmlNode(Node node, int indent, int indentStartNode, boolean inSwitch, boolean appendEnd) {
        indent++;
        StringBuilder uml = new StringBuilder();
        if (node instanceof Graph) {
            uml.append(repeat(indent)).append(String.format("partition **%s** {", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Graph) node).getStart(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(repeat(indent)).append("}").append("\n");
        } else if (node instanceof AbstractGroupNode) {
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Group) node).getEntry(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(repeat(indent)).append("end group").append("\n");
        } else if (node instanceof AbstractBranchesNode) {
            uml.append(repeat(indent)).append(String.format("switch( %s? )", node.getName())).append("\n");
            indent++;
            List<Edge> edgeList = ((AbstractBranchesNode) node).getEdgeList();
            for (int i = 0; i < edgeList.size(); i++) {
                Edge edge = edgeList.get(i);
                uml.append(repeat(indent)).append(String.format("case ( %s )", edge.getName())).append("\n");
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
                uml.replace(endInSwitchIndex, endInSwitchIndexEnd, "");
            }
            indent--;
            uml.append(repeat(indent)).append("endswitch").append("\n");
            if (endInSwitchIndex > -1) {
                uml.append(repeat(indent)).append(endInSwitch);
            }
        } else if (node instanceof AbstractChainNode) {
            if (node instanceof Start) {
                indentStartNode = indent;
                uml.append(repeat(indentStartNode)).append(String.format(": %s;", node.getName())).append("\n");
            } else if (node instanceof End) {
                if (!inSwitch || appendEnd) {
                    uml.append(repeat(indentStartNode)).append(String.format(": %s;", node.getName())).append("\n");
                }
            } else {
                uml.append(repeat(indent)).append(String.format(": %s;", node.getName())).append("\n");
            }
            Edge edge = ((AbstractChainNode) node).getEdge();
            if (edge != null) {
                indent--;
                uml.append(this.buildUmlNode(edge.getNode(), indent, indentStartNode, inSwitch, appendEnd));
            }
        }
        return uml;
    }

    /**
     * generate plantuml string of graph
     *
     * @param graph graph
     * @return plantuml string
     */
    public String generate(List<AdkContext> contextList, Graph graph) {
        return "@startuml\n" +
                "start\n" +
                this.buildUmlNode(contextList, -1, graph, -1, -1, false, false) +
                "stop\n" +
                "@enduml";
    }

    private StringBuilder buildUmlNode(List<AdkContext> contextList, int contextIndex, Node node, int indent, int indentStartNode, boolean inSwitch, boolean appendEnd) {
        indent++;
        contextIndex++;
        StringBuilder uml = new StringBuilder();
        if (node instanceof Graph) {
            uml.append(repeat(indent)).append(String.format("partition **%s** {", node.getName())).append("\n");
            uml.append(this.buildUmlNode(contextList, contextIndex, ((Graph) node).getStart(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(repeat(indent)).append("}").append("\n");
        } else if (node instanceof AbstractGroupNode) {
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            uml.append(this.buildUmlNode(contextList, contextIndex, ((Group) node).getEntry(), indent, indentStartNode, inSwitch, appendEnd));
            uml.append(repeat(indent)).append("end group").append("\n");
        } else if (node instanceof AbstractBranchesNode) {
            uml.append(repeat(indent)).append(String.format("switch( %s%s? )", activeLabelStyle, node.getName())).append("\n");
            indent++;
            List<Edge> edgeList = ((AbstractBranchesNode) node).getEdgeList();
            for (int i = 0; i < edgeList.size(); i++) {
                boolean appendEndFlag = i == edgeList.size() - 1;
                Edge edge = edgeList.get(i);
                if (contextIndex < contextList.size()
                        && contextList.get(contextIndex) != null
                        && contextList.get(contextIndex) instanceof RouterContext
                        && ((RouterContext) contextList.get(contextIndex)).getSelectEdge().getName().equalsIgnoreCase(edge.getName())) {
                    uml.append(repeat(indent)).append(String.format("case ( %s%s )", activeLabelStyle, edge.getName())).append("\n");
                    uml.append(this.buildUmlNode(contextList, contextIndex, edge.getNode(), indent, indentStartNode, true, appendEndFlag));
                } else {
                    uml.append(repeat(indent)).append(String.format("case ( %s )", edge.getName())).append("\n");
                    // no active
                    uml.append(this.buildUmlNode(edge.getNode(), indent, indentStartNode, true, appendEndFlag));
                }
            }

            // move ": end;" in switch to after "endswitch;"
            String endInSwitch = activeLabelBgStyle + ": " + activeLabelStyle + "end;\n";
            int endInSwitchIndex = uml.lastIndexOf(endInSwitch);
            if (endInSwitchIndex == -1) {
                endInSwitch = ": " + "end;\n";
                endInSwitchIndex = uml.lastIndexOf(endInSwitch);
            }
            int endInSwitchIndexEnd = endInSwitchIndex + endInSwitch.length();
            if (endInSwitchIndex > -1) {
                while (endInSwitchIndex > 0) {
                    if (uml.charAt(endInSwitchIndex - 1) == ' ') {
                        endInSwitchIndex--;
                    } else {
                        break;
                    }
                }
                uml.replace(endInSwitchIndex, endInSwitchIndexEnd, "");
            }
            indent--;
            uml.append(repeat(indent)).append("endswitch").append("\n");
            if (endInSwitchIndex > -1) {
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                uml.append(repeat(indent)).append(activeLabelBgStyle + ": " + activeLabelStyle + "end;").append("\n");
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            }
        } else if (node instanceof AbstractChainNode) {
            if (node instanceof Start) {
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                indentStartNode = indent;
                uml.append(repeat(indentStartNode)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, node.getName())).append("\n");
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            } else if (node instanceof End) {
                if (!inSwitch || appendEnd) {
                    uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                    uml.append(repeat(indentStartNode)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, node.getName())).append("\n");
                    uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                }
            } else {
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                uml.append(repeat(indent)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, node.getName())).append("\n");
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            }
            Edge edge = ((AbstractChainNode) node).getEdge();
            if (edge != null) {
                indent--;
                uml.append(this.buildUmlNode(contextList, contextIndex, edge.getNode(), indent, indentStartNode, inSwitch, appendEnd));
            }
        }
        return uml;
    }

    private String repeat(int indent) {
        return INDENT_STR.repeat(indent);
    }

    public void generatePng(Graph graph, OutputStream outputStream) throws IOException {
        String source = this.generate(graph);
        System.out.println(source);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream);
    }

    public void generatePng(List<AdkContext> contextList, Graph graph, OutputStream outputStream) throws IOException {
        String source = this.generate(contextList, graph);
        System.out.println(source);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream);
    }

    public ByteArrayOutputStream generatePng(Graph graph) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generatePng(graph, os);
        return os;
    }

    public ByteArrayOutputStream generatePng(List<AdkContext> contextList, Graph graph) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generatePng(contextList, graph, os);
        return os;
    }
}
