package co.edu.usco.petcotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class SpringBootSecurityMemoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityMemoryApplication.class, args);
	}

}
