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

        stage('Deploy to EC2') {
            steps {
                withCredentials([
                    [
                        $class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws-ecr-jenkins'
                    ],
                    string(
                        credentialsId: 'rds-db-password',
                        variable: 'RDS_PASSWORD'
                    )
                ]) {
                    sh '''
                        aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 969788249229.dkr.ecr.ap-south-1.amazonaws.com

                        docker pull 969788249229.dkr.ecr.ap-south-1.amazonaws.com/task-backend:jenkins

                        docker stop task-backend || true
                        docker rm task-backend || true

                        docker run -d \
                          --name task-backend \
                          -p 8082:8082 \
                          -e SPRING_DATASOURCE_URL="jdbc:mysql://task-management-db.clmu80i0wh3y.ap-south-1.rds.amazonaws.com:3306/task_management_db" \
                          -e SPRING_DATASOURCE_USERNAME="admin" \
                          -e SPRING_DATASOURCE_PASSWORD="$RDS_PASSWORD" \
                          -e SERVER_PORT=8082 \
                          969788249229.dkr.ecr.ap-south-1.amazonaws.com/task-backend:jenkins
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "Checking container status..."
                    docker ps --filter name=task-backend

                    echo "Waiting for application to become ready..."

                    i=1

                    while [ $i -le 30 ]; do
                        if curl -fsS http://localhost:8082/tasks > /tmp/tasks-response.json; then
                            echo "Application is ready!"
                            cat /tmp/tasks-response.json
                            exit 0
                        fi

                        echo "Application not ready yet... attempt $i/30"
                        i=$((i + 1))
                        sleep 2
                    done

                    echo "Application failed to become ready."
                    echo "Container logs:"
                    docker logs task-backend
                    exit 1
                '''
            }
        }
    }
}
