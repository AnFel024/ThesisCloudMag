pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        PROJECT_URL = "$env.GIT_URL"
        PROJECT_NAME = "$env.GIT_URL".replaceFirst(/^.*\/([^\/]+?).git$/, '$1').toLowerCase()
        BRANCH = "$env.GIT_BRANCH"
    }
    stages {
        stage('Checkout code') {
            steps {
                git(url: PROJECT_URL, branch: BRANCH, credentialsId: 'github-auth')
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
    }
}
