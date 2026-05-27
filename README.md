# Wild-Life Eco Simulation 🌍

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=java&logoColor=white)

Hệ thống mô phỏng hệ sinh thái hoang dã (Wild-Life Eco Simulation) là một đồ án môn học Lập trình Hướng đối tượng (OOP) được xây dựng bằng Java và JavaFX. Đồ án mô phỏng một môi trường sinh thái thu nhỏ với sự tương tác chân thực giữa động vật, thực vật, và môi trường tự nhiên dựa trên các Design Pattern kinh điển.

## 🌟 Tính Năng Nổi Bật

### 🦊 Đa dạng sinh học
- **Thực vật**: Cỏ (Grass), Cây ăn quả (FruitTree).
- **Động vật ăn cỏ**: Thỏ (Rabbit), Hươu (Deer) - Nhanh nhẹn, có khả năng phát hiện kẻ thù và lẩn trốn.
- **Động vật ăn thịt**: Sói (Wolf), Hổ (Tiger) - Có khả năng truy đuổi và tăng tốc độ khi phát hiện con mồi.
- **Động vật đặc biệt**: Voi (Elephant) - Khổng lồ, điềm tĩnh, khiến các loài vật nhỏ hơn tự động phải dạt ra nhường đường.
- **Vật cản & Nơi ẩn nấp**: Đá (Rock) dùng để chắn đường, Bụi rậm (Bush)/Rừng (Forest) dùng làm nơi tàng hình cho thú nhỏ trốn kẻ thù.

### 🧠 Trí tuệ & Tập tính sinh tồn (AI Behaviors)
- **Hệ thống Nhu cầu (Thirst & Hunger):** Động vật biết tự tìm thức ăn khi đói và tự tìm đến vùng Hồ Nước (Lake) khi khát.
- **Chiến thuật Sinh tồn (Strategy):** Thú ăn cỏ biết bỏ chạy thục mạng và chui vào bụi rậm để tàng hình khỏi thú săn mồi. Thú săn mồi biết bứt tốc truy đuổi mục tiêu.
- **Né vật cản (Steering Behaviors):** Thuật toán dò đường động, giúp động vật tự lách qua các tảng đá một cách mượt mà.

### 🌤️ Vòng đời & Thời tiết (Season Cycle)
- Hệ thống tự động chuyển mùa sau mỗi 15 giây.
- **Mùa Xuân:** Cây cối phát triển gấp đôi lượng dinh dưỡng. Động vật no đủ sẽ ngẫu nhiên sinh sản đẻ con non.
- **Mùa Đông:** Cây cỏ thiếu ánh sáng sẽ từ từ héo úa và chết, đẩy hệ sinh thái vào thử thách sinh tồn khắc nghiệt.

## 🏗️ Kiến trúc & Design Pattern
Project được code chuẩn theo mô hình MVC (Model - View - Controller) để tách biệt hoàn toàn giữa Logic sinh tồn (BioLogic) và Render đồ họa (ViewLogic).

Áp dụng 4 Design Pattern cốt lõi:
1. **Strategy Pattern:** Quản lý các hành vi riêng biệt cho động vật (`ScaredStrategy`, `HunterStrategy`, `PassiveStrategy`).
2. **Factory Method Pattern:** Linh hoạt trong việc sinh ra các thực thể mới (`AnimalFactory`, `PlantFactory`, `ObstacleFactory`).
3. **Observer Pattern:** Theo dõi và cập nhật giao diện (HUD) đếm số lượng động vật theo thời gian thực (EventPublisher).
4. **Singleton Pattern:** Quản lý Game Loop (Vòng lặp Update/Render trung tâm).

## 🚀 Cài Đặt & Chạy Ứng Dụng

**Yêu cầu hệ thống:**
- JDK 19.0.1 (hoặc tương đương)
- Maven
- IDE: IntelliJ IDEA (Khuyên dùng) hoặc Eclipse.

**Cách khởi chạy:**
1. Clone repository về máy.
2. Mở project bằng IntelliJ IDEA. Chờ IDE tự động load các dependency từ `pom.xml`.
3. Chạy file `Main.java` (`src/main/java/com/wildlife/Main.java`).

## 🎮 Cách Tương Tác
Sử dụng **Control Panel** (Bảng điều khiển) ở góc phải màn hình:
- **Chuyển đổi Bản đồ (Map Selector):** Chọn giữa Đồng cỏ, Rừng rậm, Hồ nước hoặc Bản đồ tổng hợp.
- **Spawn Entity:** Nhấn các nút bấm để thả thêm Thỏ, Sói, Hổ, Voi, Trồng cây, hoặc Đặt đá/bụi rậm vào môi trường.
- **Đổi mùa thủ công:** Nút `Next Season` để ép chuyển mùa ngay lập tức.
- **Tạm dừng/Tiếp tục:** Nút `Pause/Resume` để ngưng đọng thời gian.


