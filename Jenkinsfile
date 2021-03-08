#!/usr/bin/groovy

pipeline{
    agent any

    stages{
        stage("Getcode"){
           steps{
               echo "Getting code..."
               checkout scm
               echo ${JAVA_HOME}
           }
        }
        stage("Maven"){
            steps{
                echo "Mavening..."
            }
        }
        stage("Deploy"){
            steps{
                echo "Deploying..."
            }
        }
    }
}