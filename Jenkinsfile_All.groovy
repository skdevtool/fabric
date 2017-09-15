// Declarative 
pipeline {
	agent any
	environment {
		DOCKER_SECRET = credentials('docker-nexus-admin')
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
                        	sh "docker login -u ${DOCKER_SECRET_USR} -p ${DOCKER_SECRET_PSW} nexus.sk.com"
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
			    	sh "docker -H tcp://10.178.84.172:2375 login -u ${DOCKER_SECRET_USR} -p ${DOCKER_SECRET_PSW} nexus.sk.com"
				echo "docker stack deploy (remote)"
                            	sh "docker -H tcp://10.178.84.172:2375 stack deploy -c docker-stack.yaml --resolve-image changed --with-registry-auth fabric-net"
			}
		}
	}
}
