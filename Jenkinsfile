pipeline {
    agent any

    stages {
        stage('Init') {
            steps {
                script {
                    echo 'Initializing...'
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building...'
                    sh './mvnw install -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo 'Testing...'
                    sh './mvnw test -Dtest=*ImplTest'
                }
            }
        }
    }

    post {
        success {
            echo 'CI/CD pipeline executed successfully!'
            emailext subject: "CI/CD Pipeline Successful: ${currentBuild.fullDisplayName}",
                     body: "The CI/CD pipeline for ${currentBuild.fullDisplayName} passed successfully.",
                     to: <EMAIL>
        }

        failure {
            echo 'CI/CD pipeline failed!'
            emailext subject: "CI/CD Pipeline Failed: ${currentBuild.fullDisplayName}",
                     body: "The CI/CD pipeline for ${currentBuild.fullDisplayName} failed.",
                     to: <EMAIL>
        }
    }
}
