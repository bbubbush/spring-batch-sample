package com.bbubbush.batch.http.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CatResDto {

	private String id;
	private String url;
	private int width;
	private int height;
}
