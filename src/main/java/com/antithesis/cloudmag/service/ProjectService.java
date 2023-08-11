package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.client.responses.GitHubCreateRepositoryResponse;
import com.antithesis.cloudmag.controller.payload.request.CreateAppDto;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseDto;
import com.antithesis.cloudmag.controller.payload.request.DeleteAppDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.*;
import com.antithesis.cloudmag.mapper.DatabaseMapper;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.model.Database;
import com.antithesis.cloudmag.model.Project;
import com.antithesis.cloudmag.repository.*;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProjectService {

    private static final String POSTGRES_TRIGGER = "database_postgres";

    private static final String MYSQL_TRIGGER = "database_mysql";

    private final ProjectRepository projectRepository;
    private final DeployRepository deployRepository;
    private final StatusRepository statusRepository;
    private final DatabaseRepository databaseRepository;
    private final InstanceRepository instanceRepository;
    private final UserRepository userRepository;
    private final GitHubClient gitHubClient;
    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final ProjectMapper projectMapper;
    private final DogStatsdClient dogStatsdClient;
    private final JenkinsClient jenkinsClient;
    private final DatabaseMapper databaseMapper;
    private final VersionRepository versionRepository;

    public MessageResponse<?> createProject(CreateAppDto createAppDto) {
        createProjectInfo(createAppDto, "Application");
        return MessageResponse.builder()
                .message(
                        format("Se ha creado correctamente el projecto con nombre %s",
                        createAppDto.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    private ProjectEntity createProjectInfo(CreateAppDto createAppDto, String projectType) {
        InstanceEntity instanceEntity;
        if (createAppDto.getCloud_provider().equals("AWS")) {
            RunInstancesResponse runInstancesResponse = awsManagementService.generateInstance(
                    InstanceType.fromValue(
                            createAppDto.getInstance_type()
                    ));
            instanceEntity = getInstance(runInstancesResponse);
        }
        else {
            VirtualMachine virtualMachine = azureManagementService.createVirtualMachine(
                    createAppDto.getName());
            instanceEntity = getInstance(virtualMachine);
        }
        ProjectEntity.ProjectEntityBuilder project = createProject(createAppDto, instanceEntity, projectType);
        if ("Application".equals(projectType)) {
            GitHubCreateRepositoryResponse gitHubClientRepository = gitHubClient.createRepository(
                    Strings.concat("tesisV1", createAppDto.getName()), createAppDto.getLanguage());
            project.repositoryUrl(gitHubClientRepository.getHtmlUrl());
            project.language(createAppDto.getLanguage());
        }
        return projectRepository.save(project.build());
    }

    private ProjectEntity.ProjectEntityBuilder createProject(
            CreateAppDto createAppDto,
            InstanceEntity instanceEntity,
            String projectType) {
        UserEntity userEntity = userRepository.findById(createAppDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        StatusEntity statusEntity = StatusEntity.builder()
                .statusName("PENDING")
                .updatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .updatedBy(userEntity)
                .build();
        statusRepository.save(statusEntity);
        return ProjectEntity.builder()
                .name(createAppDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .instanceInfo(instanceEntity)
                .status(statusEntity)
                .creator(userEntity)
                .projectType(projectType);
    }

    private InstanceEntity getInstance(RunInstancesResponse runInstancesResponse) {
        InstanceEntity instanceEntity = InstanceEntity.builder()
                .id(runInstancesResponse.instances().get(0).instanceId())
                .hostUrl(runInstancesResponse.instances().get(0).publicDnsName())
                .provider("AWS")
                .type(runInstancesResponse.instances().get(0).instanceTypeAsString())
                .reservationId(runInstancesResponse.reservationId())
                .build();
        instanceRepository.save(instanceEntity);
        return instanceEntity;
    }

    private InstanceEntity getInstance(VirtualMachine virtualMachine) {
        InstanceEntity instanceEntity = InstanceEntity.builder()
                .id(virtualMachine.id())
                .hostUrl(virtualMachine.getPrimaryPublicIPAddress().leafDomainLabel())
                .provider("AZURE")
                .type(virtualMachine.size().toString())
                .reservationId(null)
                .build();
        instanceRepository.save(instanceEntity);
        return instanceEntity;
    }

    public MessageResponse<String> createDatabase(CreateDatabaseDto createDatabaseDto) {
        if (databaseRepository.existsByName(createDatabaseDto.getName())) {
            return MessageResponse.<String>builder()
                    .message("Error: Database name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        UserEntity userEntity = userRepository.findById(createDatabaseDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        ProjectEntity aws = createProjectInfo(CreateAppDto.builder()
                .cloud_provider("AWS")
                .instance_type("t2.micro")
                .username(createDatabaseDto.getUsername())
                .name(createDatabaseDto.getName()).build(),
                "Database");
        String randomPass = RandomStringUtils.randomAlphabetic(10);
        DatabaseEntity project = DatabaseEntity.builder()
                .name(createDatabaseDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .dbms(createDatabaseDto.getDbms_type())
                .creator(userEntity)
                .initialPassword(randomPass)
                .status("PENDING")
                .projectInfo(aws)
                .build();
        databaseRepository.save(project);
        return MessageResponse.<String>builder()
                .data("La contraseña generada es: " + randomPass + " con nombre de usuario: user. Se recomienda cambiarla una vez la base de datos este creada")
                .message(format(
                        "Se ha creado correctamente la base de datos con nombre %s en %s",
                        createDatabaseDto.getName(), createDatabaseDto.getDbms_type()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<List<Project>> listProjects() {
        // TODO pasar long fecha a localdatetime
        dogStatsdClient.sendMetric();
        List<ProjectEntity> allByCreator = projectRepository.findAll();
        List<Project> projects = allByCreator.stream()
                .map(projectEntity -> {
                    Project project = projectMapper.mapToProject(projectEntity);
                    project.setDate(LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(projectEntity.getCreatedAt()),
                            java.time.ZoneId.systemDefault()).toString());
                    return project;
                })
                .toList();
        return MessageResponse.<List<Project>>builder()
                .data(projects)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<List<Database>> listDatabases() {
        // TODO pasar long fecha a localdatetime
        dogStatsdClient.sendMetric();
        List<Database> projects = databaseRepository.findAll().stream()
                .map(databaseEntity -> {
                    Database database = databaseMapper.mapToDatabase(databaseEntity);
                    database.setDate(LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(databaseEntity.getCreatedAt()),
                            java.time.ZoneId.systemDefault()).toString());
                    return database;
                })
                .toList();
        return MessageResponse.<List<Database>>builder()
                .data(projects)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse validateInstanceStatus() {
        DescribeInstancesResponse describeInstancesResponse = awsManagementService.validateInstanceHealth();
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        projectEntities.stream()
                .filter(projectEntity ->
                        "PENDING".equals(projectEntity.getStatus().getStatusName())
                                || "RETRYING".equals(projectEntity.getStatus().getStatusName()))
                .forEach(
                        projectEntity -> {
                            if ("AWS".equals(projectEntity.getInstanceInfo().getProvider())) {
                                validateAwsInstances(describeInstancesResponse, projectEntity);
                            } else {
                                validateAzureInstances(projectEntity);
                            }
                        });
        return MessageResponse.builder()
                .message("Se ha validado el estado de las instancias")
                .status(HttpStatus.OK)
                .build();
    }

    public void validateAwsInstances(
            DescribeInstancesResponse describeInstancesResponse,
            ProjectEntity projectEntity) {
        describeInstancesResponse.reservations().forEach(reservation -> reservation.instances()
                .forEach(instance -> {
                    if (projectEntity.getInstanceInfo().getId().equals(instance.instanceId())) {
                        projectEntity.getInstanceInfo().setHostUrl(instance.publicDnsName());
                        StatusEntity status = projectEntity.getStatus();
                        status.setStatusName("APPROACHING");
                        status.setUpdatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
                        statusRepository.save(status);
                        projectEntity.setStatus(status);
                        jenkinsClient.triggerScaffoldingJob(
                                instance.publicDnsName(),
                                projectEntity.getName(),
                                "key.pem");
                        projectRepository.save(projectEntity);
            }
        }));
    }

    public void validateAzureInstances(ProjectEntity projectEntity) {
        StatusEntity status = projectEntity.getStatus();
        status.setStatusName("APPROACHING");
        status.setUpdatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        statusRepository.save(status);
        projectEntity.setStatus(status);
        jenkinsClient.triggerScaffoldingJob(projectEntity.getInstanceInfo().getHostUrl(), projectEntity.getName(), "id_rsa");
        projectRepository.save(projectEntity);
    }

    public MessageResponse validateDatabaseStatus() {
        DescribeInstancesResponse describeInstancesResponse = awsManagementService.validateInstanceHealth();
        List<DatabaseEntity> databaseEntities = databaseRepository.findAll();
        databaseEntities.stream()
                .filter(databaseEntity ->
                        "PENDING".equals(databaseEntity.getStatus()) || "RETRYING".equals(databaseEntity.getStatus()))
                .forEach(databaseEntity -> describeInstancesResponse
                        .reservations()
                        .forEach(reservation -> reservation
                                .instances()
                                .forEach(instance -> {
                                    if (databaseEntity.getProjectInfo().getInstanceInfo()
                                            .getId().equals(instance.instanceId())
                                            && "CREATED".equals(databaseEntity.getProjectInfo()
                                            .getStatus().getStatusName())) {
                                        databaseEntity.getProjectInfo()
                                                .getInstanceInfo()
                                                .setHostUrl(instance.publicDnsName());
                                    if ("postgres".equals(databaseEntity.getDbms())) {
                                        jenkinsClient.triggerDatabaseJob(
                                                instance.publicDnsName(),
                                                databaseEntity.getInitialPassword(),
                                                POSTGRES_TRIGGER,
                                                databaseEntity.getName());
                                    }
                                    if ("mysql".equals(databaseEntity.getDbms())) {
                                        jenkinsClient.triggerDatabaseJob(
                                                instance.publicDnsName(),
                                                databaseEntity.getInitialPassword(),
                                                MYSQL_TRIGGER,
                                                databaseEntity.getName());
                                    }
                        databaseEntity.setStatus("APPROACHING");
                        databaseRepository.save(databaseEntity);
                    }
                })));
        return MessageResponse.builder()
                .message("Se ha validado el estado de las bases de datos")
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse deleteProject(DeleteAppDto deleteAppDto) {
        ProjectEntity projectEntity = projectRepository.findByName(deleteAppDto.getName()).orElseThrow(() -> new RuntimeException("Project not found"));
        UserEntity userEntity = userRepository.findById(deleteAppDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectMapper.mapToProject(projectEntity);
        CompletableFuture<Boolean> futureOfInstance = CompletableFuture.supplyAsync(
                () -> terminateOnProvider(project));
        CompletableFuture<Boolean> futureOfRepository = CompletableFuture.supplyAsync(
                () -> gitHubClient.deleteRepository("tesisV1" + project.getName()));
        futureOfInstance
                .thenCombine(futureOfRepository,
                (terminateOnProvider, deleteRepository) -> {
                    if (!terminateOnProvider) {
                        return MessageResponse.builder()
                                .message("No se ha podido eliminar la instancia, valide los logs para detallar el error")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build();
                    }
                    if (!deleteRepository) {
                        return MessageResponse.builder()
                                .message("No se ha podido eliminar el repositorio, valide los logs para detallar el error")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build();
                    }
                    return null;
                })
                .exceptionally(throwable -> {
                    throw new RuntimeException("Error deleting instance or repository");
                });

        CompletableFuture<Boolean> futureOfDeploys = CompletableFuture.supplyAsync(
                () -> {
                    deployRepository.findAllByProjectInfoName(project.getName())
                            .forEach(deployEntity -> {
                                deployEntity.setStatus("FINISHED");
                                deployRepository.save(deployEntity);
                            });
                    return true;
                }
        );

        CompletableFuture<Boolean> futureOfVersions = CompletableFuture.supplyAsync(
                () -> {
                    versionRepository.findAllByProjectInfoName(projectEntity.getName())
                            .forEach(versionEntity -> {
                                versionEntity.setStatus("PROJECT DELETED");
                                versionRepository.save(versionEntity);
                            });
                    return true;
                }
        );
        futureOfDeploys.thenCombine(futureOfVersions, (unused, unused2) -> {
            StatusEntity status = projectEntity.getStatus();
            status.setStatusName("DELETED");
            status.setUpdatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
            status.setUpdatedBy(userEntity);
            statusRepository.save(status);
            projectEntity.setStatus(status);
            projectRepository.save(projectEntity);
            return null;
        }).exceptionally(throwable -> {
            throw new RuntimeException("Error deleting instance or repository");
        });

        return MessageResponse.builder()
                .message("Se ha eliminado el proyecto")
                .status(HttpStatus.OK)
                .build();
        /*}
        return MessageResponse.builder()
                .message("No se ha encontrado el proyecto")
                .status(HttpStatus.NOT_FOUND)
                .build();*/
    }

    private boolean terminateOnProvider(Project projectEntity) {
        if ("AWS".equals(projectEntity.getInstanceInfo().getProvider())) {
            return awsManagementService.terminateInstance(projectEntity.getInstanceInfo().getId());
        }
        return azureManagementService.deleteVirtualMachine(projectEntity.getName());
    }
}
