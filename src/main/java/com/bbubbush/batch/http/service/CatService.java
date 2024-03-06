package com.bbubbush.batch.http.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatService implements PetService {

	@Override
	public boolean downloadImage(String uri) {
		byte[] body = RestClient.create()
				.get()
				.uri(uri)
				.retrieve()
				.body(byte[].class);
		log.info("Cat Image");
		int indexOf = uri.lastIndexOf("/") + 1;
		String fileName = uri.substring(indexOf);
		try {
			Files.write(Paths.get(Instant.now().toEpochMilli() + "_" + fileName), body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public void getRandomImage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getRandomImages(int size) {
		// TODO Auto-generated method stub

	}

}
