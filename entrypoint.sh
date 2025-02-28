#!/bin/bash
# Dừng ngay nếu có lỗi
set -e 

echo "⏳ Waiting for MySQL to be ready..."
while ! nc -z db-mysql 3306; do   
  sleep 1
done

echo "✅ MySQL is ready. Starting the application..."
exec java -jar /app/jobhunter-application.jar