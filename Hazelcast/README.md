# Hazelcast + Spring Boot demo

Hai instance Spring Boot, mỗi instance nhúng một Hazelcast member. Cả hai join chung cluster
`dev-cluster` nên map `cache` là dữ liệu dùng chung: ghi ở node này, đọc được từ node kia.

## Chạy bằng Docker Compose

```bash
docker compose up --build
```

| Node  | HTTP host port | Ghi chú                    |
|-------|----------------|----------------------------|
| node1 | 8080           | trong container vẫn là 8080 |
| node2 | 8081           | map 8081 -> 8080            |

Dừng: `docker compose down`

## Chạy 2 instance ở local (không Docker)

Hai terminal khác nhau:

```bash
./gradlew bootRun
./gradlew bootRun --args="--server.port=8081"
```

Instance thứ hai tự lấy port Hazelcast 5702 (`port-auto-increment`) và join vào cluster qua
`127.0.0.1`. Trong IntelliJ có sẵn hai run configuration `node-8080` và `node-8081`.

## API

| Method   | Endpoint                        | Mô tả                          |
|----------|---------------------------------|--------------------------------|
| `PUT`    | `/api/cache/{key}`              | Ghi value (value nằm ở body)   |
| `PUT`    | `/api/cache?key=...&value=...`  | Ghi value bằng query param     |
| `GET`    | `/api/cache/{key}`              | Đọc value, 404 nếu không có    |
| `GET`    | `/api/cache`                    | Liệt kê toàn bộ entry          |
| `DELETE` | `/api/cache/{key}`              | Xoá key                        |
| `GET`    | `/api/cluster`                  | Danh sách member đang join     |

Mọi response đều có field `servedBy` để biết node nào vừa xử lý request.

### Kiểm tra dữ liệu dùng chung

Ghi vào node1:

```bash
curl -X PUT "http://localhost:8080/api/cache?key=hello&value=xin%20chao"
# hoặc value nằm ở body
curl -X PUT http://localhost:8080/api/cache/hello -H "Content-Type: text/plain" -d "xin chao"
```

Đọc từ node2 — vẫn ra giá trị vừa ghi, `servedBy` là `node2`:

```bash
curl http://localhost:8081/api/cache/hello
curl http://localhost:8081/api/cache
```

Xác nhận cluster có đủ 2 member:

```bash
curl http://localhost:8080/api/cluster
```

Trên PowerShell dùng `curl.exe` thay cho `curl` để không bị alias sang `Invoke-WebRequest`.

## Cấu hình

`src/main/resources/application.yml`, prefix `app.hazelcast`. Override bằng biến môi trường:

| Biến                          | Ý nghĩa                          |
|-------------------------------|----------------------------------|
| `APP_HAZELCAST_INSTANCE_NAME` | Tên node hiển thị trong response |
| `APP_HAZELCAST_MEMBERS`       | Danh sách member, ngăn cách dấu phẩy |
| `APP_HAZELCAST_CLUSTER_NAME`  | Chỉ node cùng tên mới join được  |
| `SERVER_PORT`                 | Port HTTP                        |
