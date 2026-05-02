package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Lưu các message bị lỗi (Dead Letter) từ RabbitMQ.
 * Khi một message bị xử lý thất bại nhiều lần, nó sẽ được đẩy vào đây.
 * Admin có thể xem và retry lại từng message.
 */
@Document(collection = "DlqMessages")
public class DlqMessage {

    @Id
    private String id;               // UUID tự sinh

    @Field("QueueNguon")
    private String queueNguon;       // Queue gốc message từ đó bị rejected

    @Field("NoiDungMessage")
    private String noiDungMessage;   // JSON content của message gốc

    @Field("LyDoLoi")
    private String lyDoLoi;          // Error message / exception message

    @Field("SoLanThu")
    private Integer soLanThu;        // Số lần đã thử xử lý

    @Field("TrangThai")
    private String trangThai;        // DANG_CHO | DA_RETRY | DA_XOA

    @Field("ThoiGianLoi")
    private String thoiGianLoi;      // Thời điểm message vào DLQ

    @Field("ThoiGianRetry")
    private String thoiGianRetry;    // Lần retry gần nhất (null nếu chưa retry)

    public DlqMessage() {}

    public DlqMessage(String id, String queueNguon, String noiDungMessage,
                      String lyDoLoi, Integer soLanThu, String trangThai, String thoiGianLoi) {
        this.id = id;
        this.queueNguon = queueNguon;
        this.noiDungMessage = noiDungMessage;
        this.lyDoLoi = lyDoLoi;
        this.soLanThu = soLanThu;
        this.trangThai = trangThai;
        this.thoiGianLoi = thoiGianLoi;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQueueNguon() { return queueNguon; }
    public void setQueueNguon(String queueNguon) { this.queueNguon = queueNguon; }
    public String getNoiDungMessage() { return noiDungMessage; }
    public void setNoiDungMessage(String noiDungMessage) { this.noiDungMessage = noiDungMessage; }
    public String getLyDoLoi() { return lyDoLoi; }
    public void setLyDoLoi(String lyDoLoi) { this.lyDoLoi = lyDoLoi; }
    public Integer getSoLanThu() { return soLanThu; }
    public void setSoLanThu(Integer soLanThu) { this.soLanThu = soLanThu; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getThoiGianLoi() { return thoiGianLoi; }
    public void setThoiGianLoi(String thoiGianLoi) { this.thoiGianLoi = thoiGianLoi; }
    public String getThoiGianRetry() { return thoiGianRetry; }
    public void setThoiGianRetry(String thoiGianRetry) { this.thoiGianRetry = thoiGianRetry; }
}
