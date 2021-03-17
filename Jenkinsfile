#!/usr/bin/groovy

pipeline{
    agent any

    stages{
        stage("Getcode"){
           steps{
               echo "Getting code..."
               //代码获取
               checkout scm
           }
        }
        stage("Maven"){
            steps{
                echo "Mavening..."
                //maven构建
//                sh "mvn -Dmaven.test.skip=true clean package"
                sh "set JAVA_HOME=/usr/local/jdk-11.0.10"
                sh "mvn --version"
            }

        }
        stage("Upload"){
            steps{
                echo "Uploading..."
                //ssh传送文件并执行脚本
                sshPublisher(publishers: [sshPublisherDesc(configName: 'kg666', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'nohup sh /usr/local/backend/start.sh', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: 'usr/local/backend/', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/Backend-COIN-1.0-SNAPSHOT.jar', usePty: true)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
            }
        }

    }
}