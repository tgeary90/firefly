@Library('cicd-lib') _

import tom.groovy.libs.AppVersion
import tom.groovy.libs.cicd_lib


pipeline {
    agent any
    parameters {
        string(
		name: 'VERSION',
		description: '',
		defaultValue: ''
	)
	string(
		name: 'NEW_VERSION',
		description: '',
		defaultValue: ''
	)
    }
    stages {
        stage('Build') {
            steps {
                script {
		  def appVersion = new AppVersion(version: params.VERSION, newVersion: params.NEW_VERSION)
		  def printer = new cicd_lib()
                  utils.info("Starting build")
		  echo appVersion.version
                  echo appVersion.newVersion
		  sh "cd backend; ./gradlew build"
                }
            }
        }
    }
}
