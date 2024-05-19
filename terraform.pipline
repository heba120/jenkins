pipeline {
    agent { label 'terradock' }

    parameters {
        choice(name: 'WORKSPACE', choices: ['dev', 'prod'], description: 'Choose Terraform Workspace')
    }
    
    environment {
        AWS_ACCESS_KEY_ID     = credentials('awsconfig')
        AWS_SECRET_ACCESS_KEY = credentials('awsconfig')
    }
    
    stages {
        stage('Clone repo') {
            steps {
                git url: 'https://github.com/heba120/terraform.git', branch: 'main'
            }
        }
        
        stage('Check Files') {
            steps {
                script {
                    sh "ls -l"  // Lists all files in the current directory
                }
            }
        }

        stage('init') {
            steps {
                script 
                {
                    sh "terraform init"
                }
            }
        }

        stage('Select Workspace') {
            steps {
                script {
                    sh """
                    
                    if ! terraform workspace select ${params.WORKSPACE}; then
                      terraform workspace new ${params.WORKSPACE}
                      terraform workspace show
                    fi
                    """
                }
            }
        }

        stage('Terraform Plan') {
            steps {
                script {
                    sh "terraform plan -var-file=${params.WORKSPACE}.tfvars"
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                script {
                        sh "terraform destroy -auto-approve -var-file=${params.WORKSPACE}.tfvars"
                }
            }
        }
    }
}

