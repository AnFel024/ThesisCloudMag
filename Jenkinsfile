@Library('tesis-shared-libraries')_
pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        DOCKERHUB_CREDENTIALS = ('docker-auth')
        PROJECT_URL = "$app_url"
        PROJECT_NAME = "$app_name"
        TAG_NAME = "$version_tag"
        BRANCH = "$branch_name"
        DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
    }
    stages {
        stage('Env vars updated') {
            steps {
                echo 'Hola a todos! Empezando pruebas'
                echo "The git url is $PROJECT_URL"
                echo "Project name $PROJECT_NAME"
                echo "Branch name $BRANCH"
               }
        }
        stage('Demo Shared') {
            steps {
                echo 'Hello world'
                helloWorld()
            }
        }
        stage('Checkout code') {
            steps {
                git(url: PROJECT_URL, branch: BRANCH, credentialsId: 'gh-token-auth')
            }
        }
        stage('build gradle') {
            steps {
                sh 'echo hola'
                withGradle {
                    sh './gradlew clean'
                }

                withGradle {
                    sh './gradlew bootJar'
                }
            }
        }
        stage('Build image') {
            steps {
                script {
                    dockerImage = docker.build DOCKER_REGISTRY + TAG_NAME
                }
            }
        }
        stage('Push') {
            steps {
                script {
                    docker.withRegistry('', DOCKERHUB_CREDENTIALS) {
                        dockerImage.push()
                    }
                }
            }
        }
        stage('Cleaning up') {
            steps {
                sh "docker rmi $DOCKER_REGISTRY:$TAG_NAME"
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}
