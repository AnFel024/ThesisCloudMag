package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.client.JenkinsClient;
import com.antithesis.cloudmag.client.responses.GitHubCreateRepositoryResponse;
import com.antithesis.cloudmag.controller.payload.request.CreateAppDto;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseDto;
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
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProjectService {

    private static final String POSTGRES_TRIGGER = "database_postgres";

    private static final String MYSQL_TRIGGER = "database_mysql";

    private static final String ORACLE_TRIGGER = "database_oracle";

    private final ProjectRepository projectRepository;
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

    public MessageResponse<?> createProject(CreateAppDto createAppDto) {
        if (projectRepository.existsByName(createAppDto.getName())) {
            return MessageResponse.builder()
                    .message("Error: Project name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        GitHubCreateRepositoryResponse gitHubClientRepository = gitHubClient.createRepository(
                Strings.concat("tesisV1", createAppDto.getName()));
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
        createProject(createAppDto, instanceEntity, gitHubClientRepository.getHtmlUrl());

        return MessageResponse.builder()
                .message(
                        format("Se ha creado correctamente el projecto con nombre %s",
                        createAppDto.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    private void createProject(CreateAppDto createAppDto, InstanceEntity instanceEntity, String repositoryUrl) {
        UserEntity userEntity = userRepository.findById(createAppDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name(createAppDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .repositoryUrl(repositoryUrl)
                .instanceInfo(instanceEntity)
                .status("PENDING")
                .creator(userEntity)
                .build();
        projectRepository.save(projectEntity);
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
                .hostUrl(virtualMachine.getPrimaryPublicIPAddress().ipAddress())
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
        RunInstancesResponse runInstancesResponse = awsManagementService.generateInstance(
                InstanceType.fromValue("t2.micro"));
        var instanceEntity = getInstance(runInstancesResponse);
        String randomPass = RandomStringUtils.randomAlphabetic(10);
        final String db_user = "mysql".equals(createDatabaseDto.getDbms_type()) ? "user" : "postgres";
        DatabaseEntity project = DatabaseEntity.builder()
                .name(createDatabaseDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .dbms(createDatabaseDto.getDbms_type())
                .creator(userEntity)
                .initialPassword(randomPass)
                .status("PENDING")
                .instanceInfo(instanceEntity)
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
        List<Project> projects = allByCreator.stream().map(projectMapper::mapToProject).toList();
        return MessageResponse.<List<Project>>builder()
                .data(projects)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<List<Database>> listDatabases() {
        // TODO pasar long fecha a localdatetime
        dogStatsdClient.sendMetric();
        List<Database> projects = databaseRepository.findAll().stream().map(databaseMapper::mapToDatabase).toList();
        return MessageResponse.<List<Database>>builder()
                .data(projects)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse validateInstanceStatus() {
        DescribeInstancesResponse describeInstancesResponse = awsManagementService.validateInstanceHealth();
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        projectEntities.stream().filter(projectEntity -> "PENDING".equals(projectEntity.getStatus())).forEach(projectEntity -> {
            describeInstancesResponse.reservations().forEach(reservation -> {
                reservation.instances().forEach(instance -> {
                    if (projectEntity.getInstanceInfo().getId().equals(instance.instanceId())) {
                        InstanceEntity instanceEntity = instanceRepository.findById(instance.instanceId()).get();
                        instanceEntity.setHostUrl(instance.publicDnsName());
                        projectEntity.setInstanceInfo(instanceEntity);
                        projectEntity.setStatus("RUNNING");
                        jenkinsClient.triggerScaffoldingJob(instance.publicDnsName());
                        projectRepository.save(projectEntity);
                    }
                });
            });
        });
        List<DatabaseEntity> databaseEntities = databaseRepository.findAll();
        databaseEntities.stream().filter(projectEntity -> "PENDING".equals(projectEntity.getStatus())).forEach(projectEntity -> {
            describeInstancesResponse.reservations().forEach(reservation -> {
                reservation.instances().forEach(instance -> {
                    if (projectEntity.getInstanceInfo().getId().equals(instance.instanceId())) {
                        InstanceEntity instanceEntity = instanceRepository.findById(instance.instanceId()).get();
                        instanceEntity.setHostUrl(instance.publicDnsName());
                        projectEntity.setInstanceInfo(instanceEntity);
                        projectEntity.setStatus("RUNNING");
                        if ("postgres".equals(projectEntity.getDbms())) {
                            jenkinsClient.triggerDatabaseJob(instance.publicDnsName(), projectEntity.getInitialPassword(), POSTGRES_TRIGGER);
                        }
                        if ("mysql".equals(projectEntity.getDbms())) {
                            jenkinsClient.triggerDatabaseJob(instance.publicDnsName(), projectEntity.getInitialPassword(), MYSQL_TRIGGER);
                        }
                        databaseRepository.save(projectEntity);
                    }
                });
            });
        });
        return MessageResponse.builder()
                .message("Se ha validado el estado de las instancias")
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse deleteProject(String name) {
        Optional<ProjectEntity> project = projectRepository.findByName(name);
        if (project.isPresent()) {
            ProjectEntity projectEntity = project.get();
            boolean terminateOnProvider = terminateOnProvider(projectEntity);
            boolean deleteRepository = gitHubClient.deleteRepository("tesisV1" + projectEntity.getName());
            if (!terminateOnProvider || !deleteRepository) {
                return MessageResponse.builder()
                        .message("No se ha podido eliminar el proyecto, valide los logs para detallar el error")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
            projectEntity.setStatus("DELETED");
            projectRepository.save(projectEntity);
            return MessageResponse.builder()
                    .message("Se ha eliminado el proyecto")
                    .status(HttpStatus.OK)
                    .build();
        }
        return MessageResponse.builder()
                .message("No se ha encontrado el proyecto")
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    private boolean terminateOnProvider(ProjectEntity projectEntity) {
        if ("AWS".equals(projectEntity.getInstanceInfo().getProvider())) {
            return awsManagementService.terminateInstance(projectEntity.getInstanceInfo().getId());
        }
        return awsManagementService.terminateInstance(projectEntity.getInstanceInfo().getId());
    }
}
