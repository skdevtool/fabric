// Declarative 
pipeline {
	agent any
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
            	sh "docker login -u admin -p admin123 nexus.sk.com"
			    
				echo "upload docker images..."
                sh "docker push nexus.sk.com/fabric-orderer:latest"
				sh "docker push nexus.sk.com/fabric-peer:latest"
            }
		}
		stage('Deploy') {
			steps {
                echo "docker login (remote)"
			    sh "docker -H tcp://10.178.84.172:2375 login -u admin -p admin123 nexus.sk.com"
			    echo "docker stack deploy (remote)"
                sh "docker -H tcp://10.178.84.172:2375 stack deploy -c docker-stack.yaml --resolve-image changed --with-registry-auth fabric-net"
			}
		}
	}
}
