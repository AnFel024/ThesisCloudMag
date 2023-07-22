package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.client.responses.GitHubResponse;
import com.antithesis.cloudmag.controller.payload.request.CreateAppDto;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseDto;
import com.antithesis.cloudmag.controller.payload.request.CreateTaskDto;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.*;
import com.antithesis.cloudmag.mapper.DatabaseMapper;
import com.antithesis.cloudmag.mapper.ProjectMapper;
import com.antithesis.cloudmag.mapper.TaskMapper;
import com.antithesis.cloudmag.model.Database;
import com.antithesis.cloudmag.model.Project;
import com.antithesis.cloudmag.model.Task;
import com.antithesis.cloudmag.repository.*;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DatabaseRepository databaseRepository;
    private final InstanceRepository instanceRepository;
    private final UserRepository userRepository;
    private final GitHubClient gitHubClient;
    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final DogStatsdClient dogStatsdClient;
    private final DatabaseMapper databaseMapper;
    private final TaskMapper taskMapper;

    public MessageResponse<?> createProject(CreateAppDto createAppDto) {
        if (projectRepository.existsByName(createAppDto.getName())) {
            return MessageResponse.builder()
                    .message("Error: Project name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
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

        GitHubResponse gitHubClientRepository = gitHubClient.createRepository(
                Strings.concat("tesisV1", createAppDto.getName()));
        createProject(createAppDto, instanceEntity, gitHubClientRepository.getHtmlUrl());

        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente el projecto con nombre %s",
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

    public MessageResponse<?> createDatabase(CreateDatabaseDto createDatabaseDto, String userOwner) {
        if (databaseRepository.existsByName(createDatabaseDto.getName())) {
            return MessageResponse.builder()
                    .message("Error: Database name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        UserEntity userEntity = userRepository.findById(userOwner).orElseThrow(() -> new RuntimeException("User not found"));
        DatabaseEntity project = DatabaseEntity.builder()
                .name(createDatabaseDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .dbms(createDatabaseDto.getDbms_type())
                .creator(userEntity)
                .build();
        databaseRepository.save(project);
        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente la base de datos con nombre %s en %s",
                        createDatabaseDto.getName(), createDatabaseDto.getDbms_type()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<List<Project>> listProjects(String userOwner) {
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


    public MessageResponse<List<Task>> listTasks() {
        // TODO pasar long fecha a localdatetime
        dogStatsdClient.sendMetric();
        List<Task> projects = taskRepository.findAll().stream().map(taskMapper::mapToTask).toList();
        return MessageResponse.<List<Task>>builder()
                .data(projects)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> createTask(CreateTaskDto createTaskDto, String userName) {
        if (taskRepository.existsByName(createTaskDto.getName())) {
            return MessageResponse.builder()
                    .message(format("Error: Task name %s already exist!", createTaskDto.getName()))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        UserEntity userEntity = userRepository.findById(userName).orElseThrow(() -> new RuntimeException("User not found"));
        TaskEntity project = TaskEntity.builder()
                .name(createTaskDto.getName())
                .createdAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .concurrentUrl(createTaskDto.getAction_url())
                .scheduledTime(createTaskDto.getCrontab_rule())
                .creator(userEntity)
                .build();
        taskRepository.save(project);
        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente la tarea con nombre %s que correra cada %s",
                        createTaskDto.getName(), createTaskDto.getCrontab_rule()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<?> deleteProject(String userName, String projectName) {
        // TODO Validar permisos de borrado
        projectRepository.deleteByName(projectName);
        return MessageResponse.builder()
                .message(format("Se ha eliminado correctamente el proyecto %s", projectName))
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> deleteTask(String userName, String taskName) {
        // TODO Validar permisos de borrado
        taskRepository.deleteByName(taskName);
        return MessageResponse.builder()
                .message(format("Se ha eliminado correctamente la tarea %s", taskName))
                .status(HttpStatus.OK)
                .build();
    }
}
