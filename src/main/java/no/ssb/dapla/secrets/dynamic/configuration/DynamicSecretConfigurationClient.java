package no.ssb.dapla.secrets.dynamic.configuration;

import no.ssb.config.DynamicConfiguration;
import no.ssb.config.StoreBasedDynamicConfiguration;
import no.ssb.dapla.secrets.api.SecretManagerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicSecretConfigurationClient implements SecretManagerClient {

    private final DynamicConfiguration configuration;

    public DynamicSecretConfigurationClient(Map<String, String> configuration) {
        this.configuration = new StoreBasedDynamicConfiguration.Builder()
                .values(convertMapToKeyValuePairs(configuration))
                .build();
    }

    public DynamicSecretConfigurationClient(String propertyResourcePath) {
        configuration = new StoreBasedDynamicConfiguration.Builder()
                .propertiesResource(propertyResourcePath)
                .build();
    }

    static String[] convertMapToKeyValuePairs(Map<String, String> map) {
        List<String> keyValuePair = new ArrayList<>();
        map.forEach((key, value) -> {
            keyValuePair.add(key);
            keyValuePair.add(value);
        });
        return keyValuePair.toArray(new String[0]);
    }

    @Override
    public String readString(String secretName) {
        return readString(secretName, null);
    }

    @Override
    public String readString(String secretName, String secretVersion) {
        return configuration.evaluateToString(secretName);
    }

    @Override
    public byte[] readBytes(String secretName) {
        return readBytes(secretName, null);
    }

    @Override
    public byte[] readBytes(String secretName, String secretVersion) {
        return configuration.evaluateToString(secretName) != null ? configuration.evaluateToString(secretName).getBytes() : null;
    }

    @Override
    public void close() {

    }
}
