package no.ssb.dapla.secrets.dynamic.configuration;

import io.descoped.service.provider.api.ProviderName;
import no.ssb.dapla.secrets.api.SecretManagerClient;
import no.ssb.dapla.secrets.api.SecretManagerClientInitializer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ProviderName("dynamic-secret-configuration")
public class DynamicSecretConfigurationClientInitializer implements SecretManagerClientInitializer {

    @Override
    public String providerId() {
        return "dynamic-secret-configuration";
    }

    // Optional: "secrets.property-resource-path"
    @Override
    public Set<String> configurationKeys() {
        return Collections.emptySet();
    }

    @Override
    public SecretManagerClient initialize(Map<String, String> configuration) {
        String propertyResourcePath = configuration.get("secrets.property-resource-path");
        if (propertyResourcePath != null) {
            return new DynamicSecretConfigurationClient(propertyResourcePath);
        } else {
            return new DynamicSecretConfigurationClient(configuration);
        }
    }
}
