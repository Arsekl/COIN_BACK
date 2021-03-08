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
                //新版本包强制覆盖
                sh "cp -f target/Backend-COIN-1.0-SNAPSHOT.jar /usr/local/backend/Backend-COIN-1.0-SNAPSHOT.jar "
            }
        }
        stage("Deploy"){
            steps{
                echo "Deploying..."
                //执行宿主机脚本部署
                sh "sh /usr/local/backend/start.sh"
            }
        }
    }
}