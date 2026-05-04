# BÁO CÁO DỰ ÁN: HỆ THỐNG QUẢN LÝ KHO PHÂN TÁN (CSDLPT)

## 1. Giới thiệu Dự án
Dự án **Cơ Sở Dữ Liệu Phân Tán (CSDLPT)** là một hệ thống quản lý tồn kho và chuỗi cung ứng được thiết kế theo kiến trúc phân tán thực tế. Thay vì sử dụng một máy chủ cơ sở dữ liệu duy nhất (Monolithic Database), hệ thống được chia cắt (Fragmented) và phân bố dữ liệu theo nguyên tắc địa lý và phân quyền.

Hệ thống bao gồm 3 phân hệ (Node) chính:
- **Node Miền Bắc**: Chịu trách nhiệm quản lý các kho, vật tư và giao dịch tại khu vực Miền Bắc.
- **Node Miền Nam**: Chịu trách nhiệm quản lý khu vực Miền Nam.
- **Node Trụ Sở (Global/Admin)**: Tổng hợp dữ liệu từ tất cả các chi nhánh, giám sát luồng giao dịch toàn quốc, quản trị hệ thống và vận hành cơ chế xử lý lỗi (Dead Letter Queue).

## 2. Kiến trúc Kỹ thuật (Tech Stack)
- **Backend Framework**: Spring Boot 3+ (Java 21)
- **Local Database (Chi nhánh)**: SQL Server (JPA/Hibernate) - *Đảm bảo tính ACID cho các giao dịch nội bộ*.
- **Global Database (Trụ sở)**: MongoDB - *Lưu trữ dữ liệu phi cấu trúc, log giao dịch tập trung và Replica dữ liệu linh hoạt tốc độ cao*.
- **Message Broker**: RabbitMQ - *Xử lý bất đồng bộ (Asynchronous Communication), đảm bảo độ tin cậy trong giao tiếp giữa các Node không bị gián đoạn*.
- **Security**: Spring Security + JWT Token - *Bảo mật Stateless, phân quyền (Role-based Access Control)*.
- **Frontend**: Vanilla HTML/CSS/JS (Giao diện Light Mode gọn nhẹ, Responsive).

---

## 3. Các Tính năng Cốt lõi (Core Features)

### 3.1. Quản lý Tồn kho Phân tán
- Nhân viên chi nhánh chỉ được thao tác Nhập/Xuất trên cơ sở dữ liệu SQL Server của chi nhánh mình.
- Dữ liệu tồn kho sau khi biến động sẽ được gửi một bản tin qua RabbitMQ đẩy lên Trụ Sở (MongoDB) để tổng hợp tồn kho toàn quốc theo thời gian thực (Real-time Replication).

### 3.2. Quy trình Yêu cầu Điều chuyển (Approval Workflow)
- Khi một chi nhánh cần hàng, họ tạo **"Yêu cầu điều chuyển"**. 
- Phiếu yêu cầu được lưu chung trên MongoDB ở trạng thái `PENDING`.
- Chi nhánh nắm giữ hàng hóa đăng nhập, kiểm tra và bấm **Duyệt**.
- Hệ thống chi nhánh xuất hàng tự động trừ kho nội bộ, gửi RabbitMQ sang chi nhánh nhận để tự động cộng kho, đồng thời cập nhật phiếu thành `APPROVED`.

### 3.3. Giám sát & Lịch sử Giao dịch (Audit Logging)
- Mọi thao tác Nhập, Xuất, Điều chuyển đều sinh ra Log.
- Log được lưu tập trung tại MongoDB để Trụ Sở dễ dàng truy vết (Ai, làm gì, ở Node nào, số lượng bao nhiêu, trạng thái thành công/thất bại).

### 3.4. Cơ chế Cứu hộ Lỗi (Dead Letter Queue - DLQ)
- Các bản tin RabbitMQ gửi đi nếu gặp lỗi (như máy chủ đầu nhận bị tắt, hoặc lỗi Database) sẽ tự động Retry 3 lần.
- Sau 3 lần thất bại, bản tin bị ném vào "Khu cách ly" (DLQ).
- Trụ sở có giao diện Quản trị DLQ để theo dõi nguyên nhân lỗi và có thể bấm nút **Retry** để đẩy bản tin chạy lại khi hệ thống đã khắc phục sự cố.

---

## 4. Hướng dẫn Cài đặt & Triển khai (Setup & Deployment)

Để chạy được dự án này, máy tính của bạn cần cài đặt các phần mềm sau:
1. **JDK 21** và **Maven** (Để compile và chạy Spring Boot).
2. **Microsoft SQL Server** (Cho Database chi nhánh).
3. **MongoDB** (Cho Database Trụ sở).
4. **RabbitMQ / Erlang** (Cho hệ thống hàng đợi tin nhắn).

### Bước 4.1: Chuẩn bị Cơ Sở Dữ Liệu
Bạn cần tạo sẵn các Database trống trong máy:
- **SQL Server**: Mở SSMS, chạy lệnh tạo 2 Database:
  ```sql
  CREATE DATABASE TonKhoMienBac;
  CREATE DATABASE TonKhoMienNam;
  ```
- **MongoDB**: Đảm bảo dịch vụ MongoDB đang chạy ở port `27017` (Tên DB `CSDLPT_TonKho` sẽ được Spring Boot tự động tạo).
- **RabbitMQ**: Đảm bảo RabbitMQ đang chạy ở port `5672` (Username: `guest` / Password: `guest`).

### Bước 4.2: Cấu hình Project
Các thông số kết nối Database đã được cấu hình sẵn trong `application.properties`. 
*Lưu ý:* Nếu SQL Server của bạn có đặt mật khẩu `sa`, hãy mở file `application.properties` để sửa lại thông tin `spring.datasource.password` cho tương ứng với máy bạn.

### Bước 4.3: Khởi động Hệ thống (Chạy 3 Node song song)
Hệ thống sử dụng cơ chế **Spring Profiles** để một bộ code duy nhất có thể biến hình thành 3 Node khác nhau. Bạn cần mở **3 cửa sổ Terminal / PowerShell** riêng biệt tại thư mục gốc của dự án (`C:\Users\Admin\Downloads\Csdlpt\Csdlpt`) và chạy:

**Terminal 1 (Khởi động Node Miền Bắc - Chạy ở Port 8081):**
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=mien-bac"
```

**Terminal 2 (Khởi động Node Miền Nam - Chạy ở Port 8082):**
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=mien-nam"
```

**Terminal 3 (Khởi động Node Trụ Sở - Chạy ở Port 8080):**
```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=tru-so"
```

> 💡 *Khi chạy lần đầu tiên, hệ thống (lớp `DataSeeder`) sẽ tự động tạo cấu trúc bảng (Tables) và chèn 3 tài khoản mẫu vào MongoDB.*

---

## 5. Hướng dẫn Sử dụng & Testing

Mở trình duyệt web và sử dụng các tài khoản mặc định sau để test:

### Kịch bản 1: Nhập/Xuất Kho Nội Bộ
- Mở URL: `http://localhost:8081`
- Đăng nhập: `user_mb` / Password: `123456`
- **Hành động**: Tạo một giao dịch "Nhập Kho" với Mã Kho: `KB01`, Vật tư: `VT001`, Số lượng: `100`.
- **Kết quả**: Giao diện Tồn Kho cập nhật lên 100.

### Kịch bản 2: Trụ Sở Giám Sát Real-time
- Mở URL: `http://localhost:8080`
- Đăng nhập: `admin` / Password: `123456`
- **Hành động**: Bấm sang tab **Tồn Kho**. Bạn sẽ thấy `VT001` (100 cái) vừa được nhân viên Miền Bắc nhập đã tự động "bay" lên màn hình của Trụ Sở thông qua RabbitMQ.
- Bấm qua tab **Lịch Sử Giao Dịch** để xem bằng chứng (Log).

### Kịch bản 3: Yêu Cầu Điều Chuyển Kho
1. Ở tab Miền Bắc (8081), bấm sang tab **Yêu Cầu Điều Chuyển**. Tạo phiếu xin Miền Nam 20 cái.
2. Mở URL: `http://localhost:8082` (Miền Nam) và đăng nhập bằng `user_mn` / `123456`.
3. Qua tab Yêu Cầu Điều Chuyển, sẽ thấy tờ phiếu của Miền Bắc gửi đến. Nhấn **Duyệt**.
4. Hệ thống sẽ:
   - Trừ 20 cái ở kho Miền Nam (SQL Server Miền Nam).
   - Gửi bản tin qua RabbitMQ.
   - Cộng 20 cái ở kho Miền Bắc (SQL Server Miền Bắc).
   - Cập nhật Log lên Trụ Sở (MongoDB).

### Kịch bản 4: Giả lập Sự cố & Test DLQ (Dead Letter Queue)
1. Hãy thử tắt nóng Terminal 1 (Tắt server Miền Bắc đi).
2. Ở Miền Nam (8082), tạo một lệnh Điều chuyển hàng sang Miền Bắc.
3. Vì Miền Bắc đang "sập nguồn", RabbitMQ không gửi tin nhắn được. Nó sẽ thử lại (Retry) 3 lần, mỗi lần cách nhau vài giây.
4. Sau 3 lần thất bại, bản tin bị đẩy vào DLQ.
5. Đăng nhập Admin ở Trụ Sở (8080), qua tab **Quản Trị DLQ** sẽ thấy thông báo lỗi.
6. Bật lại Terminal 1 (Khôi phục Miền Bắc). Về lại màn hình DLQ của Admin và bấm **Retry**. Bản tin sẽ được giải cứu và cộng tiền lại cho Miền Bắc thành công!

---
*Báo cáo được thiết kế để minh họa toàn vẹn kiến trúc phân tán và tính sẵn sàng cao (High Availability) của CSDLPT.*
