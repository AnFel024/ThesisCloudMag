package com.antithesis.cloudmag.controller.payload.response;

import com.antithesis.cloudmag.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuditsResponse {
    private List<Project> awsProjects;
    private List<Project> azureProjects;
    private AwsDateRange awsDateRange;
    private String azureUrlInvoice;
    private String datadogUrl;
    private String newRelicUrl;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class AwsDateRange {
        private Double monthlyCost;
        private List<AwsRanges> ranges;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class AwsRanges {
        private String data;
        private Double cost;
    }
}
