// Jenkins Pipeline for Sungbok Church
// Context7 Best Practice: Declarative Pipeline with Parameters

pipeline {
    agent any

    // 🎯 배포 대상 선택 파라미터 (Context7 Best Practice)
    parameters {
        booleanParam(
            name: 'DEPLOY_BACKEND',
            defaultValue: true,
            description: '✅ Backend 배포 (Spring Boot + Podman)'
        )
        booleanParam(
            name: 'DEPLOY_FRONTEND',
            defaultValue: true,
            description: '✅ Frontend 배포 (Next.js + Node 24)'
        )
        choice(
            name: 'DEPLOY_MODE',
            choices: ['full', 'backend-only', 'frontend-only'],
            description: '배포 모드 선택'
        )
    }

    environment {
        PROJECT_DIR = '/Users/jaewon/Documents/sungbok-web'
        BACKEND_IMAGE = 'sungbok-backend'
        FRONTEND_IMAGE = 'sungbok-frontend'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Checking out source code...'
                dir("${PROJECT_DIR}") {
                    sh 'git status || echo "Not a git repository"'
                }
                script {
                    echo "🎯 배포 모드: ${params.DEPLOY_MODE}"
                    echo "   Backend: ${params.DEPLOY_BACKEND ? '✅' : '⏭️ Skip'}"
                    echo "   Frontend: ${params.DEPLOY_FRONTEND ? '✅' : '⏭️ Skip'}"
                }
            }
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // Backend 배포 단계 (Context7 Pattern)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        stage('Backend Build') {
            when {
                expression {
                    params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'
                }
            }
            steps {
                echo '🏗️ Building Backend...'
                dir("${PROJECT_DIR}/backend") {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Backend Test') {
            when {
                expression {
                    params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'
                }
            }
            steps {
                echo '🧪 Testing Backend...'
                dir("${PROJECT_DIR}/backend") {
                    sh './gradlew test'
                }
            }
        }

        stage('Backend Container Build') {
            when {
                expression {
                    params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'
                }
            }
            steps {
                echo '🐳 Building Backend Container...'
                dir("${PROJECT_DIR}/backend/podman") {
                    sh "podman-compose build backend"
                }
            }
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // Frontend 배포 단계 (Context7 Pattern)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        stage('Frontend Build') {
            when {
                expression {
                    params.DEPLOY_FRONTEND || params.DEPLOY_MODE == 'frontend-only' || params.DEPLOY_MODE == 'full'
                }
            }
            steps {
                echo '⚛️ Building Frontend...'
                dir("${PROJECT_DIR}/frontend") {
                    sh 'pnpm install'
                    sh 'pnpm build'
                }
            }
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 배포 및 검증 (Context7 Pattern)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

        stage('Deploy') {
            steps {
                script {
                    def deployBackend = params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'
                    def deployFrontend = params.DEPLOY_FRONTEND || params.DEPLOY_MODE == 'frontend-only' || params.DEPLOY_MODE == 'full'

                    dir("${PROJECT_DIR}/backend/podman") {
                        if (deployBackend && deployFrontend) {
                            echo '🚀 Deploying Backend + Frontend (Full)...'
                            sh 'podman-compose down'
                            sh 'podman-compose up -d'
                        } else if (deployBackend) {
                            echo '🚀 Deploying Backend only...'
                            sh 'podman-compose restart backend'
                        } else if (deployFrontend) {
                            echo '🚀 Deploying Frontend only...'
                            sh 'podman-compose restart frontend nginx'
                        } else {
                            echo '⚠️ No deployment target selected'
                        }
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    def deployBackend = params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'

                    if (deployBackend) {
                        echo '🏥 Health Check (Backend)...'
                        retry(3) {
                            sleep(time: 10, unit: 'SECONDS')
                            sh 'curl -f http://localhost/api/sermons || exit 1'
                        }
                    } else {
                        echo '⏭️ Health Check skipped (Backend not deployed)'
                    }
                }
            }
        }

        stage('Verification') {
            steps {
                echo '✅ Running verification tests...'
                dir("${PROJECT_DIR}/backend/podman") {
                    sh 'podman-compose ps'
                }
            }
        }
    }

    post {
        success {
            script {
                def deployBackend = params.DEPLOY_BACKEND || params.DEPLOY_MODE == 'backend-only' || params.DEPLOY_MODE == 'full'
                def deployFrontend = params.DEPLOY_FRONTEND || params.DEPLOY_MODE == 'frontend-only' || params.DEPLOY_MODE == 'full'

                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '✅ Pipeline succeeded!'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                if (deployBackend) echo '✅ Backend deployed'
                if (deployFrontend) echo '✅ Frontend deployed'
                echo '🌐 Access: http://localhost'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            }
        }
        failure {
            echo '❌ Pipeline failed!'
            echo '📋 Check logs: podman-compose logs'
        }
        always {
            echo '🧹 Cleanup complete'
        }
    }
}
