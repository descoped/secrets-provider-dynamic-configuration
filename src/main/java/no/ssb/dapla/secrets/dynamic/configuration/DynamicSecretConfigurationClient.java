package no.ssb.dapla.secrets.dynamic.configuration;

import no.ssb.config.StoreBasedDynamicConfiguration;
import no.ssb.dapla.secrets.api.SecretManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class DynamicSecretConfigurationClient implements SecretManagerClient {

    static final Logger LOG = LoggerFactory.getLogger(DynamicSecretConfigurationClient.class);
    final Map<String, String> map = new ConcurrentHashMap<>();
    final String propertyResourcePath;
    final Object lock = new Object();

    public DynamicSecretConfigurationClient(String propertyResourcePath) {
        new StoreBasedDynamicConfiguration.Builder()
                .propertiesResource(propertyResourcePath)
                .build()
                .asMap()
                .forEach((key, value) -> map.put(key, Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8))));
        this.propertyResourcePath = propertyResourcePath;
    }

    public DynamicSecretConfigurationClient(Map<String, String> configuration) {
        configuration.forEach((key, value) -> map.put(key, Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8))));
        this.propertyResourcePath = null;
    }

    @Override
    public byte[] readBytes(String secretName, String secretVersion) {
        return ofNullable(map.get(secretName)).map(s -> Base64.getDecoder().decode(s)).orElse(null);
    }

    @Override
    public String addVersion(String secretName, byte[] secretValue) {

        map.put(secretName, Base64.getEncoder().encodeToString(secretValue));
        Path path = Path.of(propertyResourcePath);
        if (!Files.isWritable(path)) {
            LOG.warn("Secret configuration is NOT writable: {}", path.normalize().toAbsolutePath());
            return "latest";
        }
        synchronized (lock) {
            try {
                String secretResources = Files.readString(path);
                Properties props = new Properties();
                props.load(new StringReader(secretResources));
                props.put(secretName, new String(secretValue, StandardCharsets.UTF_8));
                try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                    props.store(out, null);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "latest";
    }

    @Override
    public void close() {
    }
}
