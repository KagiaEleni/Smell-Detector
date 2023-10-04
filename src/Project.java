import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Project {
    private String projectDirectory;
    private List<String> sourceFiles;

    public Project(String projectDirectory) {
        this.projectDirectory = projectDirectory;
        this.sourceFiles = findJavaSourceFiles(projectDirectory);
    }

    private List<String> findJavaSourceFiles(String directory) {
        try {
            return Files.walk(Paths.get(directory))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .map(Path::toString)
                        .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<String> getSourceFiles() {
        return sourceFiles;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

}
