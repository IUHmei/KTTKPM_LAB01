rabbitmq = Message Broker
producer = Gui message
consumer = Xu ly message

Các service giao tiếp qua network Docker


+----------+        +------------+        +-----------+
| Producer | -----> | RabbitMQ   | -----> | Consumer  |
+----------+        +------------+        +-----------+
                         Queue

curl -X POST http://localhost:3000/send \
  -H "Content-Type: application/json" \
  -d '{"message":"Order #1000 send"}'

curl -X POST http://localhost:3000/send \
-H "Content-Type: application/json" \
-d '{"orderId":"Order #1000 send", "message":"Test"}'

![Sơ đồ Message queue](image.png)

### 1. Tạo file docker-compose.yml
rabbitmq:4.2.2-management-alpine -> management-alpine là phiên bản nhẹ

### 2. Tạo file Dockerfile trong từng folder service

### 3. Tạo file xử lý cho từng service producer - consumer (producer.js - consumer.js)

### 4. Tạo file package.json trong từng folder service

### 5.
```cmd
docker compose build
docker compose up
```

### 6. Mở trang web quản trị RabbitMQ
`localhost:15672`

### 7. Test API
```cmd
curl -X POST http://localhost:3000/send \
  -H "Content-Type: application/json" \
  -d '{"message":"Order #1000 created"}'
```

```cmd
curl -X POST http://localhost:3000/send \
-H "Content-Type: application/json" \
-d '{"orderId":"Order #1000 send", "message":"Test"}'
```

Nếu thiếu message → 400
```cmd 
curl -X POST http://localhost:3000/send \
-H "Content-Type: application/json" \
-d '{"orderId":"Order #1000 send"}'
```

### 8. Xem log producer / consumer
```cmd
docker compose logs producer
```

```cmd
docker compose logs consumer
```

### 9. Chạy test register
```cmd
curl -X POST http://localhost:3000/register \
-H "Content-Type: application/json" \
-d '{"userId":"u01","email":"u01@gmail.com"}'
```

### 10. DLQ (Dead Letter Queue) - hàng đợi chứa message xử lý lỗi

```
Producer
   ↓
user.registered  ──(lỗi)──▶  user.registered.dlq
```

Xử lý OK → ack

Xử lý FAIL → nack(requeue=false) → vào DLQ

Producer DLQ
![So sánh producer.js và producer_dlq.js](image-1.png)
So sánh `producer.js` và `producer_dlq.js`

Cấu hình dead-letter ngay từ producer
👉 Nếu consumer nack → message tự động sang order_queue.dlq

`comuser_dlq.js`

Gửi message lỗi sang DLQ bằng:
```
channel.nack(msg, false, false);
```

### 10. Vì test DLQ trên file khác
Sửa 2 file `Dockerfile`

![alt text](image-2.png)

### 11. Build lại Docker
```cmd
docker compose down
docker compose up --build
```

### 12. Test lỗi thiếu email
```cmd
curl -X POST http://localhost:3000/register \
-H "Content-Type: application/json" \
-d '{"userId":"u100"}'
```

### 13. Test lại message đúng
```cmd
curl -X POST http://localhost:3000/register \
-H "Content-Type: application/json" \
-d '{"userId":"u101","email":"u101@gmail.com"}'
```



