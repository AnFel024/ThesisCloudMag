package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.AuditsResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuditsService {

    private static final String NEW_RELIC_URL =
            "https://one.newrelic.com/nr1-core" +
                    "?account=4028185&duration=1800000&state=fdb7e2ee-534b-96dd-95c8-cb2712d23c23";

    private static final String DATADOG_URL =
            "https://us5.datadoghq.com/logs" +
                    "?query=&agg_q=host" +
                    "&analyticsOptions=%5B%22bars%22%2C%22dog_classic%22%2Cnull%2Cnull%5D" +
                    "&cols=host%2Cservice" +
                    "&index=%2A&messageDisplay=inline" +
                    "&sort_m=" +
                    "&sort_t=&stream_sort=desc&top_n=10&top_o=top&viz=timeseries" +
                    "&x_missing=true&from_ts=1691875794509&to_ts=1691876694509&live=true";

    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public MessageResponse<AuditsResponse> audits() {
        //List<BlobInfo> blobInfos = azureManagementService.describeCosts();
        GetCostAndUsageResponse getCostAndUsageResponse = awsManagementService.describeCosts();
        // TODO Se mnanda dummy para no hacer llamadas a AWS y generar costos, habilitar en expo
        List<ProjectEntity> projectEntities = projectRepository.findAll();

        List<AuditsResponse.AwsRanges> awsRanges = new ArrayList<>();
                getCostAndUsageResponse.resultsByTime().stream()
                        .forEach(result -> {
                            var total = result.total();
                            var date = result.timePeriod();
                                awsRanges.add(
                                AuditsResponse.AwsRanges.builder()
                                        .cost(Double.valueOf(
                                                total.values().stream()
                                                        .findAny().orElse(MetricValue.builder().amount("0.0").build())
                                                        .amount())
                                        )
                                        .data(date.start())
                                        .build());
                        });

        AuditsResponse response = AuditsResponse.builder()
                .awsProjects(projectEntities.stream()
                        .filter(projectEntity -> "AWS".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList())
                .azureProjects(projectEntities.stream()
                        .filter(projectEntity -> "AZURE".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList())
                //.azureUrlInvoice(blobInfos.get(0).blobLink())
                .azureUrlInvoice(
                        "https://ccmreportstorageeastus3.blob.core.windows.net/" +
                                "armmusagedetailsreportdownloadcontainer/" +
                                "20230812/3a63bb72-53b7-4a34-9681-83469096e9e9" +
                                "?sv=2018-03-28&sr=b" +
                                "&sig=sk%2F%2BBryXYBk64k0lWUx%2BCdL%2FFoME7DqY9vXkyeG%2BfqU%3D&spr=https" +
                                "&st=2023-08-12T21%3A21%3A57Z&se=2023-08-13T09%3A26%3A57Z&sp=r"
                )
                .awsDateRange(AuditsResponse.AwsDateRange.builder()
                        .ranges(awsRanges)
                        .monthlyCost(
                                awsRanges.stream()
                                        .map(AuditsResponse.AwsRanges::getCost)
                                        .reduce(0.0, Double::sum))
                        .build())
                .newRelicUrl(NEW_RELIC_URL)
                .datadogUrl(DATADOG_URL)
                .build();
        return MessageResponse.<AuditsResponse>builder()
                .data(response)
                .status(HttpStatus.OK)
                .build();
    }
}
