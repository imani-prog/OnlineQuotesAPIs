package com.example.quotes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;


@SpringBootApplication
public class QuotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuotesApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	@Bean
	public CommandLineRunner testConnection(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection()) {
				System.out.println("DATABASE CONNECTION SUCCESSFUL!");
				System.out.println("Catalog: " + conn.getCatalog());
				System.out.println("URL: " + conn.getMetaData().getURL());
				System.out.println("User: " + conn.getMetaData().getUserName());
			} catch (Exception e) {
				System.out.println("❌ DATABASE CONNECTION FAILED!");
				e.printStackTrace();
			}
		};
	}
}

