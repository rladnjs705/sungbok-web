#!/bin/bash
set -e

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔧 Nginx + SSL 설정 스크립트"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 1. Nginx 기본 설정
echo "📝 Step 1/5: Nginx 기본 설정 파일 생성..."
sudo tee /usr/local/etc/nginx/servers/sungbok.conf > /dev/null << 'EOF'
upstream backend {
    server localhost:8080;
}

upstream frontend {
    server localhost:3000;
}

server {
    listen 80;
    server_name localhost;

    # Backend API 프록시
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Frontend 프록시
    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF
echo "✅ Nginx 기본 설정 완료"

# 2. 정적 파일 디렉토리 생성 및 복사
echo ""
echo "📁 Step 2/5: 정적 파일 디렉토리 설정..."
sudo mkdir -p /usr/local/var/www/sungbok
sudo cp -r /Users/jaewon/Documents/sungbok-web/frontend/.next/static /usr/local/var/www/sungbok/
sudo chown -R $(whoami):staff /usr/local/var/www/sungbok
echo "✅ 정적 파일 복사 완료"

# 3. 정적 파일 캐싱 설정 추가
echo ""
echo "⚙️  Step 3/5: 정적 파일 캐싱 설정..."
sudo tee -a /usr/local/etc/nginx/servers/sungbok.conf > /dev/null << 'EOF'

    # 정적 파일 캐싱 (1년)
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|woff|woff2)$ {
        root /usr/local/var/www/sungbok;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
EOF
echo "✅ 캐싱 설정 완료"

# 4. SSL 인증서 생성
echo ""
echo "🔐 Step 4/5: SSL 인증서 생성..."
sudo mkdir -p /usr/local/etc/nginx/ssl
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout /usr/local/etc/nginx/ssl/sungbok.key \
  -out /usr/local/etc/nginx/ssl/sungbok.crt \
  -subj "/C=KR/ST=Seoul/L=Seoul/O=Sungbok Church/CN=localhost" \
  > /dev/null 2>&1
echo "✅ SSL 인증서 생성 완료"

# 5. HTTPS 설정 추가
echo ""
echo "🔒 Step 5/5: HTTPS 설정..."
sudo tee /usr/local/etc/nginx/servers/sungbok-ssl.conf > /dev/null << 'EOF'
server {
    listen 443 ssl http2;
    server_name localhost;

    ssl_certificate /usr/local/etc/nginx/ssl/sungbok.crt;
    ssl_certificate_key /usr/local/etc/nginx/ssl/sungbok.key;

    # 보안 헤더
    add_header Strict-Transport-Security "max-age=31536000" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
    }

    # 정적 파일 캐싱
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|woff|woff2)$ {
        root /usr/local/var/www/sungbok;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}

# HTTP → HTTPS 리다이렉트
server {
    listen 80;
    server_name localhost;
    return 301 https://$server_name$request_uri;
}
EOF
echo "✅ HTTPS 설정 완료"

# 6. Nginx 설정 검증
echo ""
echo "🔍 설정 검증 중..."
if sudo nginx -t; then
    echo "✅ Nginx 설정 검증 통과"
else
    echo "❌ Nginx 설정 오류 발생"
    exit 1
fi

# 7. Nginx 재시작
echo ""
echo "🔄 Nginx 재시작 중..."
sudo brew services restart nginx
sleep 3

# 8. 최종 확인
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Nginx + SSL 설정 완료!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🌐 접속 URL:"
echo "   HTTP:  http://localhost"
echo "   HTTPS: https://localhost"
echo ""
echo "🔍 설정 파일 위치:"
echo "   Nginx 기본: /usr/local/etc/nginx/servers/sungbok.conf"
echo "   Nginx SSL:  /usr/local/etc/nginx/servers/sungbok-ssl.conf"
echo "   SSL 인증서: /usr/local/etc/nginx/ssl/"
echo ""
echo "💡 다음 단계:"
echo "   1. Frontend dev server 시작: cd frontend && pnpm dev"
echo "   2. 브라우저에서 https://localhost 접속"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
