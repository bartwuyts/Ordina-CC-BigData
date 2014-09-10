package be.timysewyn;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableAsync;

import be.timysewyn.integration.transformer.AntwerpTransformer;
import be.timysewyn.integration.transformer.GhentTransformer;
import be.timysewyn.integration.transformer.ITransformer;

@ComponentScan
@EnableAutoConfiguration

@Configuration
@EnableConfigurationProperties

@EnableAsync

@EnableIntegration
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
					.sources(Application.class)
					.run(args);
	}
	
	@Bean
    public ITransformer antwerpTransformer() {
        return new AntwerpTransformer();
    }
	
	@Bean
    public ITransformer ghentTransformer() {
        return new GhentTransformer();
    }

}