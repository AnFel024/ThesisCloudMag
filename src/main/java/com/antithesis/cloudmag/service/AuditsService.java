package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;

import javax.inject.Inject;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuditsService {
    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final ProjectRepository projectRepository;

    public MessageResponse<GetCostAndUsageResponse> audits() {

        return MessageResponse.<GetCostAndUsageResponse>builder()
                .data(awsManagementService.describeCosts())
                .status(HttpStatus.OK)
                .build();
    }
}
