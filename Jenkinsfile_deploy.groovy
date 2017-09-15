// Declarative 
pipeline {
	agent any
	
	stages {
		stage('Deploy') {
			environment {
				DOCKER_SECRET = credentials('docker-nexus-admin')
			}
			steps {
            	echo "docker login (remote)"
			    sh "docker -H tcp://10.178.84.172:2375 login -u ${DOCKER_SECRET_USR} -p ${DOCKER_SECRET_PSW} nexus.sk.com"
			    echo "docker stack deploy (remote)"
                sh "docker -H tcp://10.178.84.172:2375 stack deploy -c docker-stack.yaml --resolve-image changed --with-registry-auth fabric-net"
			}
		}
	}
}
