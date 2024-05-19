pipeline {
    agent { label 'private' }

    environment {
        DOCKER_IMAGE_NAME = 'hebaadel/app:nodejs'
        RDS_HOSTNAME = credentials('RDS_HOSTNAME')
        RDS_USERNAME = credentials('RDS_USERNAME')
        RDS_PASSWORD = credentials('RDS_PASSWORD')
        RDS_PORT = credentials('RDS_PORT')
        REDIS_HOSTNAME = credentials('REDIS_HOSTNAME')
        REDIS_PORT = credentials('REDIS_PORT')
    }

    stages {
        stage('Clone repo') {
            steps {
                git branch: 'rds_redis', url: 'https://github.com/mahmoud254/jenkins_nodejs_example.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image
                    sh "docker build -t ${DOCKER_IMAGE_NAME} -f dockerfile ."
                }
            }
        }

        stage('Log in to Docker Hub') {
            steps {
                script {
                    // Log in to Docker Hub
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "docker login -u ${USERNAME} -p ${PASSWORD}"
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    // Push the Docker image to Docker Hub
                    sh "docker push ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Deploy Docker Container') {
            steps {
                script {
                    // Run the Docker container with environment variables
                    sh """docker run -d -p 3000:3000 \\
                        -e RDS_HOSTNAME \\
                        -e RDS_USERNAME \\
                        -e RDS_PASSWORD \\
                        -e RDS_PORT\\
                        -e REDIS_HOSTNAME \\
                        -e REDIS_PORT= \\
                        ${DOCKER_IMAGE_NAME}"""
                }
            }
        }
    }
}

