package dev.yukmekim.spatialdatastreamingpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SpatialDataStreamingPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpatialDataStreamingPocApplication.class, args);
	}

}
