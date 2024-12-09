package com.myproject.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CloudinaryConfig {

    private static final Dotenv dotenv = Dotenv.load();

    @Value("${cloudinary.cloud.name}")
    private String cloudName;

    @Value("${cloudinary.apikey}")
    private String cloudApiKey;

    @Value("${cloudinary.api_secret}")
    private String cloudApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                //for local
//                "cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"),
//                "api_key", dotenv.get("CLOUDINARY_API_KEY"),
//                "api_secret", dotenv.get("CLOUDINARY_API_SECRET")

                //for enviroment variable
                "cloud_name", cloudName,
                "api_key", cloudApiKey,
                "api_secret", cloudApiSecret
        ));
    }
}
