package cloud.dsk8s.dependency;

public record MavenArtefact(String groupId, String artifactId, String version, String scope, String classifier, String type) {
}
