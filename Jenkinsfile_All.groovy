// Declarative 
pipeline {
	agent any
	environment {
		REPO_TOKEN = credentials('docker-nexus-admin')
		DOCKER_REMOTE_HOST = '10.178.84.172:2375'
	}
	stages {
                stage('Clean Docker Images') {
                	steps {
                        	sh "make docker-clean"
                	}
                }
		stage('Build Native Binaries') {
			steps {
				sh "make native"
			}
		}
		stage('Build Docker Images') {
			steps {
				sh 'make docker'
			}
		}
		stage('Upload to Nexus') {
			steps {
                        	sh "docker login -u ${REPO_TOKEN_USR} -p ${REPO_TOKEN_PSW} nexus.sk.com"
                        	echo "upload docker images..."
                        	sh "docker push nexus.sk.com/fabric-orderer:latest"
				sh "docker push nexus.sk.com/fabric-peer:latest"
                        	sh "docker push nexus.sk.com/fabric-tools:latest"
                       		sh "docker push nexus.sk.com/fabric-couchdb:latest"
                        	sh "docker push nexus.sk.com/fabric-kafka:latest"
                        	sh "docker push nexus.sk.com/fabric-zookeeper:latest"
                        	sh "docker push nexus.sk.com/fabric-ccenv:latest"
				sh "docker push nexus.sk.com/fabric-javaenv:latest"
                        }
		}
		stage('Deploy') {
			steps {
                        	echo "docker login (remote)"
			    	sh "docker -H tcp://${DOCKER_REMOTE_HOST} login -u ${REPO_TOKEN_USR} -p ${REPO_TOKEN_PSW} nexus.sk.com"
				echo "docker stack deploy (remote)"
                            	sh "docker -H tcp://${DOCKER_REMOTE_HOST} stack deploy -c docker-stack.yaml --resolve-image always --with-registry-auth fabric-net"
			}
		}
	}
}
