pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        DOCKERHUB_CREDENTIALS = ('docker-auth')
        PROJECT_URL = "$env.GIT_URL"
        PROJECT_NAME = "$env.GIT_URL".replaceFirst(/^.*\/([^\/]+?).git$/, '$1').toLowerCase()
        BRANCH = "$env.GIT_BRANCH"
        DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
    }
    stages {
        stage('Env vars') {
            steps {
                echo 'Hola a todos! Empezando pruebas'
                echo "The git url is $PROJECT_URL"
                echo "Project name $PROJECT_NAME"
            }
        }
        stage('Checkout code') {
            steps {
                git(url: "$PROJECT_URL", branch: "$BRANCH", credentialsId: 'github-atuh')
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
                    dockerImage = docker.build DOCKER_REGISTRY + ':spring-test-2'
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
                sh "docker rmi $DOCKER_REGISTRY:spring-test-1"
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}
