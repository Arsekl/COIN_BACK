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
                sh "cp -f target/Backend-COIN-1.0-SNAPSHOT.jar /usr/local/backend/Backend-COIN-1.0-SNAPSHOT.jar "
            }
        }
        stage("Deploy"){
            steps{
                echo "Deploying..."
                //sh "java -jar target/Backend-COIN-1.0-SNAPSHOT.jar"
            }
        }
    }
}