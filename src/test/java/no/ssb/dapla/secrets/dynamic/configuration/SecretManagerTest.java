package no.ssb.dapla.secrets.dynamic.configuration;

import no.ssb.dapla.secrets.api.SecretManagerClient;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecretManagerTest {

    @Test
    public void readDynamicSecret() {
        Map<String, String> providerConfiguration = Map.of(
                "secrets.provider", "dynamic-secret-configuration",
                "secrets.propertyResourcePath", "application-secret.properties"
        );

        try (SecretManagerClient client = SecretManagerClient.create(providerConfiguration)) {
            assertEquals("42", client.readString("AN_ANSWER"));
            assertArrayEquals("42".getBytes(), client.readBytes("AN_ANSWER"));
        }
    }

}
