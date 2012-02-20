/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.samples.cafe;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
/**
 * @author Tom McCuch
 *
 */
public class CafeDemoAppHttp {
	
	private static Logger logger = Logger.getLogger(CafeDemoAppHttp.class);
	private static String uri = "http://localhost:8080/cafe-ha-proxy/placeOrder.htm";
	private static String jsonOrder = "{\"number\":1,\"items\":[{\"shots\":2,\"iced\":false,\"orderNumber\":1,\"drinkType\":\"LATTE\"},{\"shots\":3,\"iced\":true,\"orderNumber\":1,\"drinkType\":\"MOCHA\"}]}";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		RestTemplate template = new RestTemplate();
		logger.info("Created http request: " + jsonOrder);
		HttpEntity<Object> request = new HttpEntity<Object>(jsonOrder);
		logger.info("Posting request to: " + uri);
		ResponseEntity<?> httpResponse = template.exchange(uri, HttpMethod.POST, request, String.class);
		if (!httpResponse.getStatusCode().equals(HttpStatus.OK)){
			logger.error("Problems with the request. Http status: " + httpResponse.getStatusCode());
		}
		logger.info("Response: " + httpResponse.getBody());
	}
}
