package com.antithesis.cloudmag.controller.payload.request;

import lombok.Data;

@Data
public class CreateTaskDto {
	private String action_url;
	private String name;
	private String crontab_rule;
	private String username;
}
