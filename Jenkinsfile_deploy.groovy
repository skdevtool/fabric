// Declarative 
pipeline {
	agent any
	
	stages {
		stage('Deploy') {
			environment {
				REPO_TOKEN = credentials('docker-nexus-admin')
				DOCKER_REMOTE_HOST = '10.178.84.172:2375'
			}
			steps {
            	echo "docker login (remote)"
			    sh "docker -H tcp://${DOCKER_REMOTE_HOST} login -u ${REPO_TOKEN_USR} -p ${REPO_TOKEN_PSW} nexus.sk.com"
			    echo "docker stack deploy (remote)"
                sh "docker -H tcp://${DOCKER_REMOTE_HOST} stack deploy -c docker-stack.yaml --resolve-image always --with-registry-auth fabric-net"
			}
		}
	}
}