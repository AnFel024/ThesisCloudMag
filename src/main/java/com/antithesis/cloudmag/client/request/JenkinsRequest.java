package com.antithesis.cloudmag.client.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class JenkinsRequest {
    private String token;
    private String appOrg;
    private String appUrl;
    private String appName;
    private String branchName;
    private String branchType;
    private String createVersion;
    private String versionType;
}
