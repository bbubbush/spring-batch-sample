package com.bbubbush.batch.http.service;

import java.util.List;

public interface PetService {

	boolean downloadImage(String uri);

	String getRandomImageUrl();

	List<String> getRandomImageUrls(int size);

}
