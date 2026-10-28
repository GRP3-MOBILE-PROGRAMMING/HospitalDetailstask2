// UHC HospitalDetails CI pipeline.
// Lint gate -> build -> unit tests -> archive APK. Any lint error fails the build.


// Named runGradle because the Gradle plugin already provides a 'gradle' step.
def runGradle(String tasks) {
    if (isUnix()) {
        sh "./gradlew ${tasks} --stacktrace"
    } else {
        bat "gradlew.bat ${tasks} --stacktrace"
    }
}

pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timestamps()
    }

    triggers {
        githubPush()              // build on every GitHub webhook push
        pollSCM('H/5 * * * *')    // fallback if the webhook misses a push
    }

    environment {
        GRADLE_OPTS  = '-Dorg.gradle.daemon=false'
        NOTIFY_EMAIL = 'aarontaremwa8@gmail.com'
        LINT_XML     = 'app/build/reports/lint-results-debug.xml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    if (isUnix()) { sh 'chmod +x gradlew' }
                }
            }
        }

        stage('Lint rule tests') {
            steps {
                runGradle(':lint-checks:test')
            }
        }

        stage('Lint gate') {
            steps {
                // abortOnError = true in app/build.gradle.kts, so errors fail this stage
                runGradle(':app:lintDebug')
            }
        }

        stage('Build') {
            steps {
                runGradle(':app:assembleDebug')
            }
        }

        stage('Unit tests') {
            steps {
                runGradle(':app:testDebugUnitTest')
            }
        }

        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', fingerprint: true
            }
        }
    }

    post {
        always {
            recordIssues(
                enabledForFailure: true,
                tools: [androidLintParser(pattern: env.LINT_XML)],
                qualityGates: [[threshold: 1, type: 'TOTAL_ERROR', criticality: 'FAILURE']]
            )
            archiveArtifacts artifacts: 'app/build/reports/lint-results-debug.*', allowEmptyArchive: true
            junit testResults: '**/build/test-results/**/*.xml', allowEmptyResults: true
        }
        success {
            emailext(
                to: env.NOTIFY_EMAIL,
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <p>The UHC HospitalDetails build passed the lint gate, build and unit tests.</p>
                    <ul>
                      <li>Job: ${env.JOB_NAME}</li>
                      <li>Build: #${env.BUILD_NUMBER}</li>
                      <li>Branch: ${env.GIT_BRANCH ?: 'n/a'}</li>
                      <li>Commit: ${env.GIT_COMMIT ?: 'n/a'}</li>
                    </ul>
                    <p><a href="${env.BUILD_URL}">Open build</a> &middot;
                       <a href="${env.BUILD_URL}artifact/app/build/reports/lint-results-debug.html">Lint report</a></p>
                """
            )
        }
    }
}
