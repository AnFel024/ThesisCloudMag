package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.controller.payload.response.AuditsResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.ProjectEntity;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.model.Project;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.azure.resourcemanager.costmanagement.models.BlobInfo;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

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
//        List<BlobInfo> blobInfos = azureManagementService.describeCosts();
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
                        .map(projectEntity1 -> {
                            Project project = projectMapper.mapToProject(projectEntity1);
                            project.setDate(LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(projectEntity1.getCreatedAt()),
                                    java.time.ZoneId.systemDefault()).toString());
                            return project;
                        })
                        .toList());
        response.azureProjects(projectEntities.stream()
                        .filter(projectEntity -> "AZURE".equals(projectEntity.getInstanceInfo().getProvider()))
                        .map(projectMapper::mapToProject)
                        .toList());
//        response.azureUrlInvoice(blobInfos.get(0).blobLink());
        response.azureUrlInvoice(
                        "https://ccmreportstorageeastus2.blob.core.windows.net/" +
                                "armmusagedetailsreportdownloadcontainer/" +
                                "20230925/" +
                                "b563316b-58ce-47f1-a722-d5fddd4a5978?sv=2018-03-28" +
                                "&sr=b&sig=qyGvg61ov0sUoZv7%2Fmz4qNltyQN4XCOhBjU6B24BlvA%3D" +
                                "&spr=https&st=2023-09-25T02%3A04%3A52Z&se=2023-09-25T14%3A09%3A52Z&sp=r"
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
