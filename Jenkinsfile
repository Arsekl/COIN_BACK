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
                //sh "mvn -Dmaven.test.skip=true clean package"
            }
        }
        stage("Deploy"){
            steps{
                echo "Deploying..."
            }
        }
    }
}