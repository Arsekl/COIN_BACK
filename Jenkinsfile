#!/usr/bin/groovy

pipeline{
    agent any

    tools{
        jdk "jdk11"
    }

    stages{
        stage("Getcode"){
           steps{
               echo "Getting code..."
               //代码获取
               checkout scm
           }
        }
        stage("Test"){
            steps{
                echo "Testing..."
                //maven测试
                sh "mvn clean test -Dmaven.test.failure.ignore=true"
            }
        }
        stage("Jacoco"){
            steps{
                echo "Jacocoing..."
                //jacoco
                jacoco exclusionPattern: '**/controller/*.class,**/vo/*.class', sourceExclusionPattern: '**/controller/*.java,**/vo/*.java'
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
                //ssh传送文件并执行脚本
                sshPublisher(publishers: [sshPublisherDesc(configName: 'kg666', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'nohup sh /usr/local/backend/start.sh', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: 'usr/local/backend/', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/Backend-COIN-1.0-SNAPSHOT.jar', usePty: true)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
            }
        }

    }
}