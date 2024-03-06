package com.bbubbush.batch.http.service;

public interface PetService {

	boolean downloadImage(String uri);

	void getRandomImage();

	void getRandomImages(int size);

}
