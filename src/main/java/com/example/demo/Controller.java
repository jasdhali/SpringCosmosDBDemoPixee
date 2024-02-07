package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/cosmos")
@RestController
public class Controller {

	@Value("${azure.cosmos.uri}")
	private String cosmosDbUrl;

	@Value("${azure.cosmos.key}")
	private String cosmosDbKey;

	@Value("${azure.cosmos.database}")
	private String cosmosDbDatabase;

	private CosmosAsyncContainer container;

	@PostConstruct
	public void init() {
		container = new CosmosClientBuilder().endpoint(cosmosDbUrl).key(cosmosDbKey).buildAsyncClient()
				.getDatabase(cosmosDbDatabase).getContainer("City");
	}

	@GetMapping("/cities")
	public Flux<List<City>> getCities() {
		CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
		return container.queryItems("SELECT TOP 20 * FROM City c", options, City.class).byPage()
				.map(FeedResponse::getResults);
	} 

	@PostMapping("/cities")
	public String createCities(@RequestBody City city) {		
		Mono<CosmosItemResponse<City>> mono =  container.createItem(city);
		return "City Created";
	}

	@GetMapping("/hello")
	public String getHello() {
		return "Hello";
	}
}
