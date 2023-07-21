package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.client.DogStatsdClient;
import com.antithesis.cloudmag.client.GitHubClient;
import com.antithesis.cloudmag.client.responses.GitHubResponse;
import com.antithesis.cloudmag.controller.payload.request.CreateAppRequest;
import com.antithesis.cloudmag.controller.payload.request.CreateDatabaseRequest;
import com.antithesis.cloudmag.controller.payload.request.CreateTaskRequest;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.Database;
import com.antithesis.cloudmag.entity.Instance;
import com.antithesis.cloudmag.entity.Project;
import com.antithesis.cloudmag.entity.Task;
import com.antithesis.cloudmag.repository.DatabaseRepository;
import com.antithesis.cloudmag.repository.InstanceRepository;
import com.antithesis.cloudmag.repository.ProjectRepository;
import com.antithesis.cloudmag.repository.TaskRepository;
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
    private final GitHubClient gitHubClient;
    private final AWSManagementService awsManagementService;
    private final AzureManagementService azureManagementService;
    private final TaskRepository taskRepository;
    private final DogStatsdClient dogStatsdClient;

    public MessageResponse<?> createProject(CreateAppRequest createAppRequest, String userOwner) {
        if (projectRepository.existsByName(createAppRequest.getName())) {
            return MessageResponse.builder()
                    .message("Error: Project name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        Instance instance;

        if (createAppRequest.getCloud_provider().equals("AWS")) {
            RunInstancesResponse runInstancesResponse = awsManagementService.generateInstance(
                    InstanceType.fromValue(
                            createAppRequest.getInstance_type()
                    ));
            instance = getInstance(runInstancesResponse);
        }
        else {
            VirtualMachine virtualMachine = azureManagementService.createVirtualMachine(
                    createAppRequest.getName());
            instance = getInstance(virtualMachine);
        }

        GitHubResponse gitHubClientRepository = gitHubClient.createRepository(
                Strings.concat("tesisV1", createAppRequest.getName()));
        createProject(createAppRequest, userOwner, instance, gitHubClientRepository.getHtmlUrl());

        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente el projecto con nombre %s",
                        createAppRequest.getName()))
                .status(HttpStatus.CREATED)
                .build();
    }

    private void createProject(CreateAppRequest createAppRequest, String userOwner, Instance instance, String repositoryUrl) {
        Project project = Project.builder()
                .name(createAppRequest.getName())
                .created_at(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(userOwner)
                .repository_url(repositoryUrl)
                .instance_info(instance)
                .status("PENDING")
                .build();
        projectRepository.save(project);
    }

    private Instance getInstance(RunInstancesResponse runInstancesResponse) {
        Instance instance = Instance.builder()
                .instance_id(runInstancesResponse.instances().get(0).instanceId())
                .host_url(runInstancesResponse.instances().get(0).publicDnsName())
                .provider("AWS")
                .type(runInstancesResponse.instances().get(0).instanceTypeAsString())
                .reservation_id(runInstancesResponse.reservationId())
                .build();
        instanceRepository.save(instance);
        return instance;
    }

    private Instance getInstance(VirtualMachine virtualMachine) {
        Instance instance = Instance.builder()
                .instance_id(virtualMachine.id())
                .host_url(virtualMachine.getPrimaryPublicIPAddress().ipAddress())
                .provider("AZURE")
                .type(virtualMachine.size().toString())
                .reservation_id(null)
                .build();
        instanceRepository.save(instance);
        return instance;
    }

    public MessageResponse<?> createDatabase(CreateDatabaseRequest createDatabaseRequest, String userOwner) {
        if (databaseRepository.existsByName(createDatabaseRequest.getName())) {
            return MessageResponse.builder()
                    .message("Error: Database name already exist!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        Database project = Database.builder()
                .name(createDatabaseRequest.getName())
                .created_at(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(userOwner)
                .dbms(createDatabaseRequest.getDbms_type())
                .build();
        databaseRepository.save(project);
        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente la base de datos con nombre %s en %s",
                        createDatabaseRequest.getName(), createDatabaseRequest.getDbms_type()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<List<Project>> listProjects(String userOwner) {
        // TODO pasar long fecha a localdatetime
        dogStatsdClient.sendMetric();
        return MessageResponse.<List<Project>>builder()
                .data(projectRepository.findByUserOwner(userOwner))
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> createTask(CreateTaskRequest createTaskRequest, String userId) {
        if (taskRepository.existsByName(createTaskRequest.getName())) {
            return MessageResponse.builder()
                    .message(format("Error: Task name %s already exist!", createTaskRequest.getName()))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        Task project = Task.builder()
                .name(createTaskRequest.getName())
                .created_at(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .creator(userId)
                .concurrent_url(createTaskRequest.getAction_url())
                .scheduled_time(createTaskRequest.getCrontab_rule())
                .build();
        taskRepository.save(project);
        return MessageResponse.builder()
                .message(format(
                        "Se ha creado correctamente la tarea con nombre %s que correra cada %s",
                        createTaskRequest.getName(), createTaskRequest.getCrontab_rule()))
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<?> deleteProject(String userId, String projectName) {
        // TODO Validar permisos de borrado
        projectRepository.deleteByName(projectName);
        return MessageResponse.builder()
                .message(format("Se ha eliminado correctamente el proyecto %s", projectName))
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> deleteTask(String userId, String taskName) {
        // TODO Validar permisos de borrado
        taskRepository.deleteByName(taskName);
        return MessageResponse.builder()
                .message(format("Se ha eliminado correctamente la tarea %s", taskName))
                .status(HttpStatus.OK)
                .build();
    }
}
