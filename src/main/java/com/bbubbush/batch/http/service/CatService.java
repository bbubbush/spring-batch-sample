package com.bbubbush.batch.http.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bbubbush.batch.http.dto.CatResDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatService implements PetService {

	@Value("${cat.api.endpoint}")
	private String endpoint;
	@Value("${cat.api.key}")
	private String apiKey;

	@Override
	public String getRandomImageUrl() {
		List<CatResDto> response = RestClient.create()
			.get()
			.uri(endpoint + "v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=1")
			.header("x-api-key", apiKey)
			.retrieve()
			.body(new ParameterizedTypeReference<List<CatResDto>>() {});
		return response.get(0).getUrl();
	}

	@Override
	public List<String> getRandomImageUrls(int size) {
		List<CatResDto> response = RestClient.create()
			.get()
			.uri(endpoint + "v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=" + size)
			.header("x-api-key", apiKey)
			.retrieve()
			.body(new ParameterizedTypeReference<List<CatResDto>>() {});

		return response.stream()
			.map(CatResDto::getUrl)
			.collect(Collectors.toList());
	}

	@Override
	public boolean downloadImage(String uri) {
		log.info("uri :: {}", uri);

		byte[] body = RestClient.create()
			.get()
			.uri(uri)
			.retrieve()
			.body(byte[].class);
		int indexOf = uri.lastIndexOf("/") + 1;
		String fileName = uri.substring(indexOf);
		try {
			Files.write(Paths.get(Instant.now().toEpochMilli() + "_" + fileName), body);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
