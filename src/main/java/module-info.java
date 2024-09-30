import no.ssb.dapla.secrets.api.SecretManagerClientInitializer;

module secrets.provider.dynamic.configuration {

    requires io.descoped.service.provider.api;
    requires io.descoped.dynamic.config;

    requires secrets.client.api;

    requires org.slf4j;

    provides SecretManagerClientInitializer with DynamicSecretConfigurationClientInitializer;

    exports no.ssb.dapla.secrets.dynamic.configuration;

}