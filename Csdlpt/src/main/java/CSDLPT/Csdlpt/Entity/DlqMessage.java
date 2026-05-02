package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "DlqMessages")
public class DlqMessage {

    @Id
    private String id;

    @Field("QueueNguon")
    private String queueNguon;

    @Field("NoiDungMessage")
    private String noiDungMessage;

    @Field("LyDoLoi")
    private String lyDoLoi;

    @Field("SoLanThu")
    private Integer soLanThu;

    @Field("TrangThai")
    private String trangThai;

    @Field("ThoiGianLoi")
    private String thoiGianLoi;

    @Field("ThoiGianRetry")
    private String thoiGianRetry;

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

    // ── Getters & Setters
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
