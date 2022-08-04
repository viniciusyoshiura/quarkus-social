package com.mycompany.social.config;

import io.quarkus.jsonb.JsonbConfigCustomizer;

import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

public class JsonBCustomizer implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig jsonbConfig) {
        jsonbConfig.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
    }
}
