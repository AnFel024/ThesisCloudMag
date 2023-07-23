pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    parameters {
        string(defaultValue: "$env.GIT_URL", description: 'K', name: 'app_name')
        string(defaultValue: "0.0.0", description: 'K', name: 'version_tag')
        string(defaultValue: "false", description: 'K', name: 'create_version')
    }
    environment {
        DOCKERHUB_CREDENTIALS = ('docker-auth')
        PROJECT_URL = "$env.GIT_URL"
        PROJECT_NAME = "$env.GIT_URL".replaceFirst(/^.*\/([^\/]+?).git$/, '$1').toLowerCase()
        BRANCH = "$env.GIT_BRANCH"
        DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
        CREATE_VERSION = "$create_version"
    }
    stages {
        stage ('Initialize') {
            steps {
                script {
                    echo "$env"
                }
            }
        }
        stage('Initialize docker environment') {
            when {
                expression {
                    return CREATE_VERSION != null && Boolean.valueOf(CREATE_VERSION)
                }
            }
            steps {
                script {
                    PROJECT_NAME = "$app_name".toLowerCase()
                    TAG_NAME = "$version_tag"
                    DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
                    echo 'Se creara una version desplegable'
                }
            }
        }
        stage('Checkout code') {
            steps {
                git(url: PROJECT_URL, branch: BRANCH, credentialsId: 'gh-token-auth')
            }
        }
        stage('build gradle') {
            steps {
                withGradle {
                    sh './gradlew clean'
                }

                withGradle {
                    sh './gradlew bootJar'
                }
            }
        }
        stage('Build and push docker image') {
            when {
                expression {
                    return CREATE_VERSION != null && Boolean.valueOf(CREATE_VERSION)
                }
            }
            steps {
                script {
                    echo 'Building docker image...'
                    dockerImage = docker.build DOCKER_REGISTRY +":"+ TAG_NAME
                }

                script {
                    echo 'Pushing docker image...'
                    docker.withRegistry('', DOCKERHUB_CREDENTIALS) {
                        dockerImage.push()
                    }
                }

                script {
                    echo 'cleaning docker environment'
                    sh "docker rmi $DOCKER_REGISTRY:$TAG_NAME"
                }
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}
