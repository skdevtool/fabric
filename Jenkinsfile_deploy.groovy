// Declarative 
pipeline {
	agent any
	stages {
                environment{
                           DOCKER_SECRET = credentials('docker-nexus-admin')
                        }
		stage('Deploy') {
			steps {
                echo "docker login (remote)"
			    echo $DOCKER_SECRET
                            echo $DOCKER_SECRET_USR
			    sh "docker -H tcp://10.178.84.172:2375 login -u admin -p admin123 nexus.sk.com"
			    echo "docker stack deploy (remote)"
                sh "docker -H tcp://10.178.84.172:2375 stack deploy -c docker-stack.yaml --resolve-image changed --with-registry-auth fabric-net"
			}
		}
	}
}
