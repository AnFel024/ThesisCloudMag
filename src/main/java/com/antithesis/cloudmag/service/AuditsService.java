package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.AuditsResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.azure.resourcemanager.costmanagement.models.BlobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuditsService {
    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public MessageResponse<AuditsResponse> audits() {
        List<BlobInfo> blobInfos = azureManagementService.describeCosts();
        GetCostAndUsageResponse getCostAndUsageResponse = awsManagementService.describeCosts();
        List<ProjectEntity> projectEntities = projectRepository.findAll();

        List<AuditsResponse.AwsRanges> awsRanges = new ArrayList<>();
                getCostAndUsageResponse.resultsByTime().stream()
                        .map(ResultByTime::total)
                        .forEach(result -> awsRanges.add(
                                AuditsResponse.AwsRanges.builder()
                                        .cost(Double.valueOf(
                                                result.values().stream()
                                                        .findAny().orElse(MetricValue.builder().amount("0.0").build())
                                                        .amount())
                                        )
                                        .data(result.keySet().stream().findAny().get())
                                        .build()));

        AuditsResponse response = AuditsResponse.builder()
                .awsProjects(projectEntities.stream()
                        .filter(projectEntity -> "AWS".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList())
                .azureProjects(projectEntities.stream()
                        .filter(projectEntity -> "Azure".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList())
                .azureUrlInvoice(blobInfos.get(0).blobLink())
                .awsDateRange(AuditsResponse.AwsDateRange.builder()
                        .ranges(awsRanges)
                        .monthlyCost(0.0)
                        .build())
                .newRelicUrl("TODO")
                .datadogUrl("TODO")
                .build();
        return MessageResponse.<AuditsResponse>builder()
                .data(response)
                .status(HttpStatus.OK)
                .build();
    }
}
