package com.sample.metrics;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class that provides actual REST requests support.
 */
public class RestProvider {
	
	@Autowired
	private HttpClient httpClient;

	@Autowired
	private MetricRegistry registry;
	
    /**
     * Actual method that invokes OpenWeatherMap.org public API.
     * @param uri URL where API request is sent to
     * @return HTTP response
     */
    @Timed
    public HttpResponse get(URI uri) {
		try {
			registry.counter(MetricRegistry.name(RestProvider.class, "counter")).inc();
			
			HttpGet httpget = new HttpGet(uri.toURL().toExternalForm());
			HttpResponse response = this.httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() != 200) {
		    	throw new IllegalStateException("invalid response: " + 
				    response.getStatusLine().getStatusCode() + ": " + 
				   	response.getStatusLine().getReasonPhrase()
				);
		    }
		    
		    return response;
		}
		catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}
}
