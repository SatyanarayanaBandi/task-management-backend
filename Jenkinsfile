pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t task-backend:jenkins .'
            }
        }

        stage('ECR Push') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-ecr-jenkins'
                ]]) {
                    sh '''
                        aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 969788249229.dkr.ecr.ap-south-1.amazonaws.com

                        docker tag task-backend:jenkins 969788249229.dkr.ecr.ap-south-1.amazonaws.com/task-backend:jenkins

                        docker push 969788249229.dkr.ecr.ap-south-1.amazonaws.com/task-backend:jenkins
                    '''
                }
            }
        }
    }
}
