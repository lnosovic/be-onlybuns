package com.example.onlybuns;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.cache.Caching;
import java.net.URISyntaxException;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
@EnableCaching
public class OnlyBunsApplication {
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
	public static void main(String[] args) {

		SpringApplication.run(OnlyBunsApplication.class, args);
	}

}
