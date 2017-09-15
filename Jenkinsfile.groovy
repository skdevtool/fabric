// Declarative 
pipeline {
	agent any
	environment {
		REPO_TOKEN = credentials('docker-nexus-admin')
		DOCKER_REMOTE_HOST = '10.178.84.172:2375'
	}
	stages {
		stage('Build Peer Image') {
			steps {
				sh "make peer"
				sh 'make peer-docker'
			}
		}
		stage('Build Orderer Images') {
			steps {
				sh "make orderer"
				sh 'make orderer-docker'
			}
		}
		stage('Upload to Nexus') {
			steps {
            	sh "docker login -u ${REPO_TOKEN_USR} -p ${REPO_TOKEN_PSW} nexus.sk.com"
				echo "upload docker images..."
                sh "docker push nexus.sk.com/fabric-orderer:latest"
				sh "docker push nexus.sk.com/fabric-peer:latest"
            }
		}
		stage('Deploy') {
			steps {
                echo "docker login (remote)"
			    sh "docker -H tcp://${DOCKER_REMOTE_HOST} login -u ${REPO_TOKEN_USR} -p ${REPO_TOKEN_PSW} nexus.sk.com"
			    echo "docker stack deploy (remote)"
                sh "docker -H tcp://${DOCKER_REMOTE_HOST} stack deploy -c docker-stack.yaml --resolve-image changed --with-registry-auth fabric-net"
			}
		}
	}
}