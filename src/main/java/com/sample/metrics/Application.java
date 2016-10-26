package com.sample.metrics;

import com.codahale.metrics.httpclient.InstrumentedHttpClients;
import org.apache.http.client.HttpClient;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.logback.InstrumentedAppender;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

/**
 * Spring Boot main class.
 */
@Configuration
@ComponentScan
@EnableMetrics
@EnableAutoConfiguration
public class Application extends MetricsConfigurerAdapter {

	private MetricRegistry registry;

    /**
     * Spring Boot entry point method.
     * @param args ignored arguments
     */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.run();
	}

    /**
     * Creates new REST provider class.
     * @return REST provider
     */
    @Bean
    public RestProvider getRestProvider() {
		return new RestProvider();
	}

    /**
     * Creates new instrumented HTTP client and exposes it as Spring bean.
     * @return HTTP client
     */
	@Bean
    public HttpClient getHttpClient() {
		return InstrumentedHttpClients.createDefault(getMetricRegistry(),
			HttpClientMetricNameStrategies.QUERYLESS_URL_AND_METHOD);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public MetricRegistry getMetricRegistry() {
		
		if (this.registry == null) {
			registry = new MetricRegistry();
		
			// register JVM metrics
			registry.registerAll(new GarbageCollectorMetricSet());
			registry.registerAll(new MemoryUsageGaugeSet());
			registry.registerAll(new ThreadStatesGaugeSet());
			 
			// register logging metrics
			LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
			Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);
	
			InstrumentedAppender metrics = new InstrumentedAppender(registry);
			metrics.setContext(root.getLoggerContext());
			metrics.start();
			root.addAppender(metrics);
		}
		
		return registry;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public HealthCheckRegistry getHealthCheckRegistry() {
		return new HealthCheckRegistry();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
		JmxReporter.forRegistry(metricRegistry).build().start();
	}	
}
