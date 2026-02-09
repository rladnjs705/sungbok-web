#!/bin/bash
set -e

echo "🔐 Generating self-signed SSL certificate..."

# SSL 디렉토리로 이동
cd "$(dirname "$0")/ssl"

# 자체 서명 인증서 생성
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout key.pem \
  -out cert.pem \
  -subj "/C=KR/ST=Seoul/L=Seoul/O=Sungbok Church/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,DNS:*.localhost,IP:127.0.0.1"

# 권한 설정
chmod 644 cert.pem
chmod 600 key.pem

echo "✅ SSL certificate generated successfully!"
echo ""
echo "📄 Files created:"
echo "   - cert.pem (Certificate)"
echo "   - key.pem (Private Key)"
echo ""
echo "💡 Next: Run 'podman-compose up -d' to start all services"
