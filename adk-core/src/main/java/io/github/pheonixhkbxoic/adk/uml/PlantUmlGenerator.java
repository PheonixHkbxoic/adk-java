package io.github.pheonixhkbxoic.adk.uml;

import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.context.AdkContext;
import io.github.pheonixhkbxoic.adk.context.LoopContext;
import io.github.pheonixhkbxoic.adk.context.RouterContext;
import io.github.pheonixhkbxoic.adk.core.edge.Edge;
import io.github.pheonixhkbxoic.adk.core.node.*;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractBranchesNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractChainNode;
import io.github.pheonixhkbxoic.adk.core.spec.AbstractGroupNode;
import io.github.pheonixhkbxoic.adk.core.spec.Node;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 17:36
 * @desc plantuml generator to generate plantuml string and png
 * preference: {@see https://plantuml.com/zh/activity-diagram-beta}[preference]
 * and online editor: {@see https://www.plantuml.com/plantuml/uml}[online editor]
 */
@Slf4j
@Data
public class PlantUmlGenerator {
    private String indentStr = "  ";
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
                this.buildUmlNode(graph, -1, null) +
                "stop\n" +
                "@enduml";
    }

    private StringBuilder buildUmlNode(Node node, int indent, Node suffix) {
        if (suffix != null && suffix.getId().equalsIgnoreCase(node.getId())) {
            return new StringBuilder();
        }
        indent++;
        StringBuilder uml = new StringBuilder();
        if (node instanceof Graph) {
            uml.append(repeat(indent)).append(String.format("partition **%s** {", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Graph) node).getStart(), indent, suffix));
            uml.append(repeat(indent)).append("}").append("\n");
        } else if (node instanceof Loop) {
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            // loop
            indent++;
            uml.append(repeat(indent)).append(": entry;").append("\n");
            uml.append(repeat(indent)).append("while ( (maxEpoch <= 0 || epoch < maxEpoch) && !isBreaked )").append("\n");
            uml.append(this.buildUmlNode(((Group) node).getEntry(), indent, suffix));
            uml.append(repeat(indent)).append("endwhile").append("\n");
            uml.append(repeat(indent)).append(": next;").append("\n");
            uml.append(this.buildUmlNode(((Group) node).getNext(), indent, suffix));

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
            uml.append(repeat(indent)).append("end group").append("\n");
            if (endInSwitchIndex > -1) {
                uml.append(repeat(indent)).append(endInSwitch);
            }
        } else if (node instanceof AbstractGroupNode) {
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            uml.append(this.buildUmlNode(((Group) node).getEntry(), indent, suffix));
            uml.append(repeat(indent)).append("end group").append("\n");
        } else if (node instanceof AbstractBranchesNode) {
            uml.append(repeat(indent)).append(String.format("switch( %s? )", node.getName())).append("\n");
            indent++;
            List<Edge> edgeList = ((AbstractBranchesNode) node).getEdgeList();
            Node suffixNode = this.extractSuffix(((AbstractBranchesNode) node));
            log.debug("suffix: {}", suffixNode);
            for (Edge edge : edgeList) {
                uml.append(repeat(indent)).append(String.format("case ( %s )", edge.getName())).append("\n");
                uml.append(this.buildUmlNode(edge.getNode(), indent, suffixNode));
            }

            indent--;
            uml.append(repeat(indent)).append("endswitch").append("\n");
            if (suffixNode != null) {
                indent--;
                uml.append(this.buildUmlNode(suffixNode, indent, null));
            }

        } else if (node instanceof AbstractChainNode) {
            uml.append(repeat(indent)).append(String.format(": %s;", node.getName())).append("\n");
            Edge edge = ((AbstractChainNode) node).getEdge();
            if (edge != null) {
                indent--;
                uml.append(this.buildUmlNode(edge.getNode(), indent, suffix));
            }
        }
        return uml;
    }

    private Node extractSuffix(AbstractBranchesNode branchesNode) {
        List<Node> suffixList = new ArrayList<>();
        Map<String, Integer> nodeCountMap = new HashMap<>();
        for (Edge edge : branchesNode.getEdgeList()) {
            Node next = edge.getNode();
            while (next != null) {
                Integer count = nodeCountMap.get(next.getId());
                if (count == null) {
                    suffixList.add(next);
                    nodeCountMap.put(next.getId(), 1);
                } else {
                    count++;
                    nodeCountMap.put(next.getId(), count);
                }
                if (next instanceof AbstractBranchesNode) {
                    next = this.extractSuffix((AbstractBranchesNode) next);
                } else if (next instanceof AbstractChainNode) {
                    Edge sub = ((AbstractChainNode) next).getEdge();
                    next = sub == null ? null : sub.getNode();
                } else if (next instanceof AbstractGroupNode) {
                    next = ((Loop) next).getNext();
                } else {
                    throw new RuntimeException("sub graph is forbidden: " + next.getName());
                }
            }
        }
        int size = branchesNode.getEdgeList().size();
        return suffixList.stream()
                .filter(n -> nodeCountMap.getOrDefault(n.getId(), 0).equals(size))
                .findFirst()
                .orElse(null);
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
                this.buildUmlNode(contextList, new AtomicInteger(-1), graph, -1, null) +
                "stop\n" +
                "@enduml";
    }

    private StringBuilder buildUmlNode(List<AdkContext> contextList, AtomicInteger contextIndex, Node node, int indent, Node suffix) {
        if (suffix != null && suffix.getId().equalsIgnoreCase(node.getId())) {
            return new StringBuilder();
        }
        indent++;
        contextIndex.incrementAndGet();
        StringBuilder uml = new StringBuilder();
        if (node instanceof Graph) {
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("partition **%s** {", node.getName())).append("\n");
            uml.append(this.buildUmlNode(contextList, contextIndex, ((Graph) node).getStart(), indent, suffix));
            uml.append(repeat(indent)).append("}").append("\n");
        } else if (node instanceof Loop) {
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            // loop
            indent++;
            uml.append(repeat(indent)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, "entry")).append("\n");
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("while ( %s (maxEpoch <= 0 || epoch < maxEpoch) && !isBreaked )", activeLabelStyle)).append("\n");
            int loopContextIndex = contextIndex.get();
            uml.append(this.buildUmlNode(contextList, contextIndex, ((Group) node).getEntry(), indent, suffix));
            AdkContext adkContext = loopContextIndex >= contextList.size() ? null : contextList.get(loopContextIndex);
            if (adkContext != null) {
                LoopContext loopContext = (LoopContext) adkContext;
                uml.append(repeat(indent - 1)).append(activeArrowStyle)
                        .append(activeLabelStyle)
                        .append(" epoch: ").append(loopContext.getEpoch() + 1)
                        .append("/").append(loopContext.getMaxEpoch())
                        .append(" isBreaked: ").append(loopContext.isBreaked())
                        .append(";").append("\n");
            } else {
                uml.append(repeat(indent - 1)).append(activeArrowStyle).append("\n");
            }
            uml.append(repeat(indent)).append("endwhile").append("\n");
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, "next")).append("\n");
            uml.append(this.buildUmlNode(((Group) node).getNext(), indent, null));

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
            uml.append(repeat(indent)).append("end group").append("\n");
            if (endInSwitchIndex > -1) {
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
                uml.append(repeat(indent)).append(activeLabelBgStyle).append(": ").append(activeLabelStyle).append("end;").append("\n");
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            }
        } else if (node instanceof AbstractGroupNode) {
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("group **%s**", node.getName())).append("\n");
            uml.append(this.buildUmlNode(contextList, contextIndex, ((Group) node).getEntry(), indent, suffix));
            uml.append(repeat(indent)).append("end group").append("\n");
        } else if (node instanceof AbstractBranchesNode) {
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("switch( %s%s? )", activeLabelStyle, node.getName())).append("\n");
            indent++;
            List<Edge> edgeList = ((AbstractBranchesNode) node).getEdgeList();
            Node suffixNode = this.extractSuffix(((AbstractBranchesNode) node));
            log.debug("context suffix: {}", suffixNode);
            for (Edge edge : edgeList) {
                AdkContext adkContext = contextList.get(contextIndex.get());
                if (contextIndex.get() < contextList.size()
                        && adkContext != null && (
                        adkContext instanceof RouterContext && ((RouterContext) adkContext).getSelectEdge().getName().equalsIgnoreCase(edge.getName())
                                || node instanceof Scatter)
                ) {
                    uml.append(repeat(indent)).append(String.format("case ( %s%s )", AdkUtil.isEmpty(edge.getName()) ? "" : activeLabelStyle, edge.getName())).append("\n");
                    uml.append(this.buildUmlNode(contextList, contextIndex, edge.getNode(), indent, suffixNode));
                } else {
                    uml.append(repeat(indent)).append(String.format("case ( %s )", edge.getName())).append("\n");
                    // no active
                    uml.append(this.buildUmlNode(edge.getNode(), indent, suffixNode));
                }
            }

            indent--;
            uml.append(repeat(indent)).append("endswitch").append("\n");
            if (suffixNode != null) {
                indent--;
                uml.append(this.buildUmlNode(contextList, contextIndex, suffixNode, indent, null));
            }
        } else if (node instanceof AbstractChainNode) {
            uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            uml.append(repeat(indent)).append(String.format("%s: %s%s;", activeLabelBgStyle, activeLabelStyle, node.getName())).append("\n");
            if (node instanceof End) {
                uml.append(repeat(indent)).append(activeArrowStyle).append("\n");
            }
            Edge edge = ((AbstractChainNode) node).getEdge();
            if (edge != null) {
                indent--;
                uml.append(this.buildUmlNode(contextList, contextIndex, edge.getNode(), indent, suffix));
            }
        }
        return uml;
    }

    private String repeat(int indent) {
        return indentStr.repeat(indent);
    }

    public void generate(Graph graph, OutputStream outputStream, FileFormat format) throws IOException {
        String source = this.generate(graph);
        System.out.println(source);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream, new FileFormatOption(format));
    }


    public ByteArrayOutputStream generate(Graph graph, FileFormat format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generate(graph, os, format);
        return os;
    }

    public void generate(List<AdkContext> contextList, Graph graph, OutputStream outputStream, FileFormat format) throws IOException {
        String source = this.generate(contextList, graph);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream, new FileFormatOption(format));
    }

    public ByteArrayOutputStream generate(List<AdkContext> contextList, Graph graph, FileFormat format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generate(contextList, graph, os, format);
        return os;
    }


}
