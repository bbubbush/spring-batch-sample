package com.bbubbush.batch.http.dto;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class CatResDto {

	private String id;
	private String url;
	private int width;
	private int height;
}
