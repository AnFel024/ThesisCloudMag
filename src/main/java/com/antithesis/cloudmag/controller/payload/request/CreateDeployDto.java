package com.antithesis.cloudmag.controller.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateDeployDto extends JenkinsDto {
	private String username;
	private String ipDir;
	private String tag;
	private String tagId;
}
