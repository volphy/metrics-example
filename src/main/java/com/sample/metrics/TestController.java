package com.sample.metrics;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
class TestController {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
    private RestProvider restProvider;

    /**
     * Method that handles /test/api HTTP GET call
     * @param request HTTP request
     * @param response HTTP response
     * @return response as String
     */
	@Timed(name = "weather-conditions-request")
	@ResponseBody
	@RequestMapping("/test/api")
    public String process(HttpServletRequest request, HttpServletResponse response)
		throws  MetricsException {
		
		LOGGER.info("Processing Request");
        String appId = System.getProperty("APP_ID");
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Bristol,CT&APPID=" + appId;
        LOGGER.info("URL={}", url);
		
		// Get first request
        String jsonResponse;
        try {
            URI uri = new URI(url);
            HttpResponse resp = restProvider.get(uri);

            jsonResponse = StreamUtils.copyToString(resp.getEntity().getContent(), Charset.forName("UTF-8"));
            LOGGER.info("Response={}", jsonResponse);
        } catch (URISyntaxException e) {
            LOGGER.error("API URL is incorrect: {}", e.getMessage(), e);
            throw new MetricsException(e.getMessage());

        } catch (IOException e) {
            LOGGER.error("HTTP response is incorrect: {}", e.getMessage(), e);
            throw new MetricsException(e.getMessage());
        }

        return jsonResponse;
	}
}
