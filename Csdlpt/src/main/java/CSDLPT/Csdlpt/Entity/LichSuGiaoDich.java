package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "LichSuGiaoDich")
public class LichSuGiaoDich {

    @Id
    private String id;

    @Field("LoaiGiaoDich")
    private String loaiGiaoDich;

    @Field("MaNguon")
    @Indexed
    private String maNguon;

    @Field("MaKho")
    private String maKho;

    @Field("MaVatTu")
    @Indexed
    private String maVatTu;

    @Field("TenVatTu")
    private String tenVatTu;

    @Field("SoLuong")
    private Integer soLuong;

    @Field("TrangThai")
    private String trangThai;

    @Field("GhiChu")
    private String ghiChu;

    @Field("ThoiGian")
    @Indexed
    private String thoiGian;

    public LichSuGiaoDich() {}

    public LichSuGiaoDich(String id, String loaiGiaoDich, String maNguon,
                          String maKho, String maVatTu, String tenVatTu,
                          Integer soLuong, String trangThai, String ghiChu, String thoiGian) {
        this.id = id;
        this.loaiGiaoDich = loaiGiaoDich;
        this.maNguon = maNguon;
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.thoiGian = thoiGian;
    }

    // ── Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    public String getMaNguon() { return maNguon; }
    public void setMaNguon(String maNguon) { this.maNguon = maNguon; }
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getThoiGian() { return thoiGian; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }
}
