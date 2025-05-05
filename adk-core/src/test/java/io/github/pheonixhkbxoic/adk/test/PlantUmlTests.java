package io.github.pheonixhkbxoic.adk.test;

import io.github.pheonixhkbxoic.adk.core.node.Graph;
import io.github.pheonixhkbxoic.adk.uml.PlantUmlGenerator;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 17:54
 * @desc
 */
public class PlantUmlTests {

    @Test
    public void test() {
        PlantUmlGenerator gen = new PlantUmlGenerator();
        Graph graph = new Graph(null, null);
        try {
            FileOutputStream file = new FileOutputStream("target/" + graph.getName() + ".png");
            gen.generatePng(graph, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
