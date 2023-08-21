package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.AuditsResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.azure.resourcemanager.costmanagement.models.BlobInfo;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.valueOf;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuditsService {

    private static final String COST_DOLLAR_TEMPLATE = "$%s USD";
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
        // TODO Se manda dummy para no hacer llamadas a AWS y generar costos, habilitar en expo
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        AtomicDouble totalCost = new AtomicDouble(0.0);
        List<AuditsResponse.AwsRanges> awsRanges = new ArrayList<>();
                getCostAndUsageResponse.resultsByTime()
                        .forEach(result -> {
                            var total = result.total();
                            var date = result.timePeriod();
                            double costForMonth = Double.parseDouble(
                                    total.values().stream()
                                            .findAny().orElse(MetricValue.builder().amount("0.0").build())
                                            .amount());
                            awsRanges.add(
                                AuditsResponse.AwsRanges.builder()
                                    .cost(
                                            format(
                                                    COST_DOLLAR_TEMPLATE,
                                                    totalCost.accumulateAndGet(costForMonth, Double::sum)))
                                    .data(date.start())
                                    .build());
                        });

        AuditsResponse.AuditsResponseBuilder response = AuditsResponse.builder();
                response.awsProjects(projectEntities.stream()
                        .filter(projectEntity -> "AWS".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList());
        response.azureProjects(projectEntities.stream()
                        .filter(projectEntity -> "AZURE".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList());
//        response.azureUrlInvoice(blobInfos.get(0).blobLink());
        response.azureUrlInvoice(
                        "https://ccmreportstoragewestus.blob.core.windows.net/" +
                                "armmusagedetailsreportdownloadcontainer/" +
                                "20230821/" +
                                "486acd6e-8d0b-497f-b256-9c0f4ae2331e" +
                                "?sv=2018-03-28&sr=b" +
                                "&sig=PBzrNw4XQRyGAVGqZIiJiF%2FwXNNNl7v1EuxTSsgPOuc%3D&spr=https" +
                                "&st=2023-08-21T20%3A22%3A26Z&se=2023-08-22T08%3A27%3A26Z&sp=r"
                );
        response.awsDateRange(
                AuditsResponse.AwsDateRange.builder()
                        .ranges(awsRanges)
                        .monthlyCost(format(COST_DOLLAR_TEMPLATE, totalCost.get()))
                        .build()
        );
        response.newRelicUrl(NEW_RELIC_URL);
        response.datadogUrl(DATADOG_URL);
        return MessageResponse.<AuditsResponse>builder()
                .data(response.build())
                .status(HttpStatus.OK)
                .build();
    }
}
