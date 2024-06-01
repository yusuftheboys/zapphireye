pipeline {
    agent none
    stages {
        stage('Secret Scanning Using Trufflehog') {
            agent {
                docker {
                    image 'trufflesecurity/trufflehog:latest'
                    args '-u root --entrypoint='
                }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'trufflehog filesystem . --exclude-paths trufflehog-excluded-paths.txt --fail > trufflehog-scan-result.txt'
                }
                sh 'cat trufflehog-scan-result.txt'
                archiveArtifacts artifacts: 'trufflehog-scan-result.txt'
            }
        }
        stage('Build') {
            agent {
              docker {
                  image 'node:lts-buster-slim'
              }
            }
            steps {
                sh 'npm install'
            }
        }
        stage('Test') {
            agent {
              docker {
                  image 'node:lts-buster-slim'
              }
            }
            steps {
                sh 'echo test'
            }
        }
        stage('SCA Retire Js') {
            agent {
              docker {
                  image 'node:lts-buster-slim'
              }
            }
            steps {
                sh 'npm install retire'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './node_modules/retire/lib/cli.js --outputpath retire-scan-report.txt'
                }
                sh 'cat retire-scan-report.txt'
                archiveArtifacts artifacts: 'retire-scan-report.txt'
            }
        }
        stage('SCA OWASP Dependency Check') {
            agent {
              docker {
                  image 'owasp/dependency-check:latest'
                  args '-u root -v /var/run/docker.sock:/var/run/docker.sock --entrypoint='
              }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh '/usr/share/dependency-check/bin/dependency-check.sh --scan . --project "zapphireye" --format ALL'
                }
                archiveArtifacts artifacts: 'dependency-check-report.html'
                archiveArtifacts artifacts: 'dependency-check-report.json'
                archiveArtifacts artifacts: 'dependency-check-report.xml'
            }
        }
        stage('SCA Trivy Scan Dockerfile Misconfiguration') {
            agent {
              docker {
                  image 'aquasec/trivy:latest'
                  args '-u root --network host --entrypoint='
              }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'trivy config Dockerfile --exit-code=1 > trivy-scan-dockerfile-report.txt'
                }
                sh 'cat trivy-scan-dockerfile-report.txt'
                archiveArtifacts artifacts: 'trivy-scan-dockerfile-report.txt'
            }
        }
        stage('Build Docker Image') {
            agent {
                docker {
                    image 'docker:dind'
                    args '--user root --network host -v /var/run/docker.sock:/var/run/docker.sock'
                }
            }
            steps {
                sh 'echo il0v3mys3lf | docker login -u yusuftheboys --password-stdin'
                sh 'docker build -t zapphireye:0.1 .'
                sh 'docker push yusuftheboys/zapphireye'
            }
        }

         stage('Deploy Docker Image') {
            agent {
                docker {
                    image 'kroniak/ssh-client'
                    args '--user root --network host'
                }
            }
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: "DeploymentSSHKey", keyFileVariable: 'keyfile')]) {
                    sh 'ssh -i ${keyfile} -o StrictHostKeyChecking=no yusuftheboys@10.0.2.15 "echo il0v3mys3lf | docker login -u yusuftheboys --password-stdin"'
                    sh 'ssh -i ${keyfile} -o StrictHostKeyChecking=no yusuftheboys@10.0.2.15 docker pull yusuftheboys/zapphireye'
                    sh 'ssh -i ${keyfile} -o StrictHostKeyChecking=no yusuftheboys@10.0.2.15 docker rm --force zapphireye'
                    sh 'ssh -i ${keyfile} -o StrictHostKeyChecking=no yusuftheboys@10.0.2.15 docker run -it --detach -p 4000:4000 --name zapphireye --network host yusuftheboys/zapphireye'
                }
            }
         }

    }
}
