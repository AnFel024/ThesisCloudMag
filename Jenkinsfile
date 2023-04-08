pipeline {
    agent any
    stages {
        stage('Compile Stage') {
            steps {
                withGradle(gradle : 'gradle') {
                    sh 'gradle bootJar'
                }
            }
        }
    }
}