package cloud.dsk8s.dependency;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.io.Files;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class DependencyParserTest {

    @Test
    void parseTree() throws Exception {
        File file = new File("tree.txt");

        // Read the lines of a UTF-8 text file
        ImmutableList<String> lines = Files.asCharSource(file, Charsets.UTF_8)
                .readLines();

        MutableGraph<MavenArtefact> graph = GraphBuilder
                .directed()
                .build();

        AtomicInteger lineIndex = new AtomicInteger();
        AtomicReference<String> root = new AtomicReference<>("");
        Map<Integer, MavenArtefact> depthMap = new HashMap<>();
        lines.forEach(line -> {
            //System.out.println("line number: " + lineIndex.getAndIncrement());
            int startIndex = line.lastIndexOf(" ") + 1;
            String coordinates = line.substring(startIndex);
            String[] coordinateGroups = coordinates.split(":");
            MavenArtefact mavenArtefact = null;
            if (coordinateGroups.length == 4) {
                mavenArtefact = new MavenArtefact(coordinateGroups[0], coordinateGroups[1], coordinateGroups[3],
                        "", "", coordinateGroups[2]);
            } else if (coordinateGroups.length == 5) {
                mavenArtefact = new MavenArtefact(coordinateGroups[0],
                        coordinateGroups[1], coordinateGroups[3], coordinateGroups[4],
                        "", coordinateGroups[2]);
            } else if (coordinateGroups.length == 6) {
                mavenArtefact = new MavenArtefact(coordinateGroups[0], coordinateGroups[1], coordinateGroups[4],
                        coordinateGroups[5], coordinateGroups[3], coordinateGroups[2]);
            } else {
                System.out.println("Error splitting line");
            }

            if (startIndex == 0) {
                graph.addNode(mavenArtefact);
                depthMap.put(0, mavenArtefact);
            } else {
                int depth = startIndex / 3;
                MavenArtefact parent = depthMap.get(depth - 1);
                graph.putEdge(parent, mavenArtefact);
                depthMap.put(depth, mavenArtefact);
            }
        });
        //System.out.println(graph);
        //System.out.println(graph.predecessors("com.fasterxml.jackson.core:jackson-annotations:jar:2.13.2:compile"));

        //graph.nodes().forEach(System.out::println);
        graph.edges().forEach(pair -> {
            System.out.println(pair.nodeU() + "->" + pair.nodeV());
        });
    }

}
