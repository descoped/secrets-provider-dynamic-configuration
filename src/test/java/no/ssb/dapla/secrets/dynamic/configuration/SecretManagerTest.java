package no.ssb.dapla.secrets.dynamic.configuration;

import no.ssb.dapla.secrets.api.SecretManagerClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecretManagerTest {

    @Test
    public void readDynamicSecret() {
        final Map<String, String> providerConfiguration = Map.of(
                "secrets.provider", "dynamic-secret-configuration",
                "secrets.propertyResourcePath", "application-secret.properties"
        );
        try (SecretManagerClient client = SecretManagerClient.create(providerConfiguration)) {
            assertEquals("42", client.readString("AN_ANSWER"));
            assertArrayEquals("42".getBytes(), client.readBytes("AN_ANSWER"));
        }
    }

    @Test
    public void writeThenReadSecret() throws IOException {
        Files.copy(Path.of("src/test/resources/application-secret.properties"), Path.of("/tmp/application-secret.properties"), StandardCopyOption.REPLACE_EXISTING);
        final Map<String, String> providerConfiguration = Map.of(
                "secrets.provider", "dynamic-secret-configuration",
                "secrets.propertyResourcePath", "/tmp/application-secret.properties"
        );
        try (SecretManagerClient client = SecretManagerClient.create(providerConfiguration)) {
            assertEquals("latest", client.addVersion("question", "42".getBytes(StandardCharsets.UTF_8)));
            assertEquals("42", new String(client.readBytes("question"), StandardCharsets.UTF_8));
            assertEquals("latest", client.addVersion("question", "43".getBytes(StandardCharsets.UTF_8)));
            assertEquals("43", new String(client.readBytes("question"), StandardCharsets.UTF_8));
            assertEquals("42", client.readString("AN_ANSWER"));
            assertArrayEquals("42".getBytes(), client.readBytes("AN_ANSWER"));
        }
    }
}
