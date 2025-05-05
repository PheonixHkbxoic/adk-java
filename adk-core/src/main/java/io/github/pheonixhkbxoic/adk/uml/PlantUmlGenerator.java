package io.github.pheonixhkbxoic.adk.uml;

import io.github.pheonixhkbxoic.adk.core.node.Graph;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 17:36
 * @desc
 */
public class PlantUmlGenerator {


    public String generate(Graph graph) {
        return """
                @startuml
                start
                
                group "graph **%s**"
                    : start;
                    switch (activeAgent?)
                    case ( echoAgent )
                      :echoAgent;
                    case ( mathAgent )
                      :mathAgent;
                    case ( None )
                      :fallback;
                    endswitch
                
                    : 解析响应;
                    :end;
                
                    : next;
                end group
                
                -[#red,dashed]->
                
                group "graph **B**"
                    : start;
                
                    : ssss;
                
                    : end;
                end group
                
                stop
                @enduml
                """.formatted(graph.getName());
    }

    public void generatePng(Graph graph, OutputStream outputStream) throws IOException {
        String source = this.generate(graph);
        SourceStringReader reader = new SourceStringReader(source);
        reader.outputImage(outputStream);
    }

    public ByteArrayOutputStream generatePng(Graph graph) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.generatePng(graph, os);
        return os;
    }
}
