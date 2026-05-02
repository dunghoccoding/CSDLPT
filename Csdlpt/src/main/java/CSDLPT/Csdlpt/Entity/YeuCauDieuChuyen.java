package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "YEU_CAU_DIEU_CHUYEN")
public class YeuCauDieuChuyen {

    @Id
    private String id;

    @Field("maNguonXin")
    private String maNguonXin; // VD: MIEN_BAC

    @Field("maKhoXin")
    private String maKhoXin; // VD: KB01

    @Field("maNguonCho")
    private String maNguonCho; // VD: MIEN_NAM

    @Field("maKhoCho")
    private String maKhoCho; // VD: KN01

    @Field("maVatTu")
    private String maVatTu;

    @Field("soLuong")
    private Integer soLuong;

    @Field("trangThai")
    private String trangThai; // PENDING, APPROVED, REJECTED

    @Field("ngayTao")
    private LocalDateTime ngayTao;

    @Field("ngayDuyet")
    private LocalDateTime ngayDuyet;

    public YeuCauDieuChuyen() {}

    public YeuCauDieuChuyen(String maNguonXin, String maKhoXin, String maNguonCho, String maKhoCho, String maVatTu, Integer soLuong) {
        this.maNguonXin = maNguonXin;
        this.maKhoXin = maKhoXin;
        this.maNguonCho = maNguonCho;
        this.maKhoCho = maKhoCho;
        this.maVatTu = maVatTu;
        this.soLuong = soLuong;
        this.trangThai = "PENDING";
        this.ngayTao = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMaNguonXin() { return maNguonXin; }
    public void setMaNguonXin(String maNguonXin) { this.maNguonXin = maNguonXin; }

    public String getMaKhoXin() { return maKhoXin; }
    public void setMaKhoXin(String maKhoXin) { this.maKhoXin = maKhoXin; }

    public String getMaNguonCho() { return maNguonCho; }
    public void setMaNguonCho(String maNguonCho) { this.maNguonCho = maNguonCho; }

    public String getMaKhoCho() { return maKhoCho; }
    public void setMaKhoCho(String maKhoCho) { this.maKhoCho = maKhoCho; }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }
}
