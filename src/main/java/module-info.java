import no.ssb.dapla.secrets.api.SecretManagerClientInitializer;
import no.ssb.dapla.secrets.dynamic.configuration.DynamicSecretConfigurationClientInitializer;

module dapla.secrets.provider.dynamic.configuration {

    requires no.ssb.service.provider.api;
    requires no.ssb.config;

    requires dapla.secrets.client.api;

    provides SecretManagerClientInitializer with DynamicSecretConfigurationClientInitializer;

    exports no.ssb.dapla.secrets.dynamic.configuration;

}