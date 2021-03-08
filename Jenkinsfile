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
                sh "mvn -Dmaven.test.skip=true clean package"
            }
        }
        stage("Upload"){
            steps{
                echo "Uploading..."

                sshPublisher(publishers: [sshPublisherDesc(configName: 'kg666', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'echo "good"', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: 'usr/local/backend/', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/Backend-COIN-1.0-SNAPSHOT.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }

    }
}