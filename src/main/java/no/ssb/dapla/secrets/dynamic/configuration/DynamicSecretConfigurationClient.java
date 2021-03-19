package no.ssb.dapla.secrets.dynamic.configuration;

import no.ssb.config.StoreBasedDynamicConfiguration;
import no.ssb.dapla.secrets.api.SecretManagerClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class DynamicSecretConfigurationClient implements SecretManagerClient {

    final Map<String, String> map = new ConcurrentHashMap<>();

    public DynamicSecretConfigurationClient(Map<String, String> configuration) {
        configuration.forEach((key, value) -> map.put(key, Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8))));
    }

    public DynamicSecretConfigurationClient(String propertyResourcePath) {
        this(new StoreBasedDynamicConfiguration.Builder()
                .propertiesResource(propertyResourcePath)
                .build()
                .asMap());
    }

    @Override
    public byte[] readBytes(String secretName, String secretVersion) {
        return ofNullable(map.get(secretName)).map(s -> Base64.getDecoder().decode(s)).orElse(null);
    }

    @Override
    public String addVersion(String secretName, byte[] secretValue) {
        map.put(secretName, Base64.getEncoder().encodeToString(secretValue));
        return "latest";
    }

    @Override
    public void close() {
    }
}
