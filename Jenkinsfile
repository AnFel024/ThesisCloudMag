pipeline {
  agent any
  stages {
    stage('Checkout code') {
      steps {
        git(url: 'https://github.com/AnFel024/ThesisCloudMag', branch: 'master', credentialsId: 'github-atuh')
      }
    }

    stage('build') {
      steps {
        sh 'echo hola'
        withGradle() {
          sh './gradlew clean'
        }

        withGradle() {
          sh './gradlew bootJar'
        }

      }
    }

    stage('docker') {
      steps {
        sh 'docker build -t anfel024/tesis-hub:spring-docker-3 .'
      }
    }

  }
}