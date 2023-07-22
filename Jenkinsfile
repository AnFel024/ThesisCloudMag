pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-auth')
        PROJECT_URL = "${env.GIT_URL}"
        PROJECT_NAME = "${env.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')}"
        BRANCH = "${env.GIT_BRANCH}"
    }
    stages {
        stage('Env vars') {
            steps {
                echo 'Hola a todos! Empezando pruebas'
                echo "The git url is ${env.GIT_URL}"
                echo "Project name ${PROJECT_NAME}"
            }
        }
        stage('Checkout code') {
            steps {
                git(url: '${PROJECT_URL}', branch: '${BRANCH}', credentialsId: 'github-atuh')
            }
        }

        stage('build') {
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

        stage('docker') {
            steps {
                sh 'docker build -t anfel024/${PROJECT_NAME}:spring-docker-3 .'
            }
        }
        stage('Build') {
            steps {
                sh 'docker build -t anfel024/${PROJECT_NAME} .'
            }
        }
        stage('Login') {
            steps {
                sh 'cat $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        stage('Push') {
            steps {
                sh 'docker push anfel024/${PROJECT_NAME}'
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}
