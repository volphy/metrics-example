package com.sample.metrics;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;

@Controller
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
	@Timed
	@ResponseBody
	@RequestMapping("/test/api")
    public String process(HttpServletRequest request, HttpServletResponse response)
		throws URISyntaxException {
		
		LOGGER.info("Processing Request");
        String appId = System.getProperty("APP_ID");
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Bristol,CT&APPID=" + appId;
        LOGGER.info("URL={}", url);
		
		// Get first request
		HttpResponse resp = restProvider.get(new URI(url));
		return resp.getEntity().toString();
	}
}
