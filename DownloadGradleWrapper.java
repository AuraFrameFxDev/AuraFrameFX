import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadGradleWrapper {
    public static void main(String[] args) {
        String version = "8.11.1";
        String wrapperJarUrl = "https://services.gradle.org/distributions/gradle-" + version + "-wrapper.jar";
        String outputDir = "gradle/wrapper";
        String outputFile = outputDir + "/gradle-wrapper.jar";
        
        System.out.println("Downloading Gradle wrapper " + version + "...");
        
        try {
            // Create directories if they don't exist
            Files.createDirectories(Paths.get(outputDir));
            
            // Download the file using HttpClient (modern approach)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(wrapperJarUrl))
                .GET()
                .build();
            
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                Files.copy(in, Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Successfully downloaded Gradle wrapper to: " + outputFile);
                System.out.println("You can now run: .\\gradlew.bat build");
            }
        } catch (IOException e) {
            System.err.println("Error downloading Gradle wrapper: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
