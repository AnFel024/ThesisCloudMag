pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        PROJECT_URL = "$env.GIT_URL"
        PROJECT_NAME = "$env.GIT_URL".replaceFirst(/^.*\/([^\/]+?).git$/, '$1').toLowerCase()
        BRANCH = "$env.GIT_BRANCH"
        DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
        DOCKERHUB_CREDENTIALS = ('docker-auth')
    }
    stages {
        stage('Initialize docker environment') {
            when {
                expression {
                    return env.create_version != null && env.create_version.toBoolean()
                }
            }
            steps {
                script {
                    PROJECT_URL = "$app_url"
                    PROJECT_ORGANIZATION = "$app_name".toLowerCase()
                    PROJECT_NAME = "$app_name".toLowerCase()
                    TAG_NAME = "$version_tag"
                    BRANCH = "$branch_name"
                    DOCKER_REGISTRY = "anfel024/$PROJECT_NAME"
                }
                echo 'Se creara una version desplegable'
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
                    return env.create_version != null && env.create_version.toBoolean()
                }
            }
            steps {
                script {
                    dockerImage = docker.build DOCKER_REGISTRY +":"+ TAG_NAME
                }

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
