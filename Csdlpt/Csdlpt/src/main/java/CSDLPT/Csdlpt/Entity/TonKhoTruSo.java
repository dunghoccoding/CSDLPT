package CSDLPT.Csdlpt.Entity;

import jakarta.persistence.*;

/**
 * Bảng SQL Server phía Trụ Sở — lưu tồn kho tổng hợp từ 2 miền.
 * Table: TON_KHO_TONG_HOP
 * PK: Id = maNguon + "_" + maVatTu
 */
@Entity
@Table(name = "TON_KHO_TONG_HOP")
public class TonKhoTruSo {

    @Id
    @Column(name = "Id", length = 100)
    private String id;           // "MIEN_BAC_VT001"

    @Column(name = "MaNguon", length = 20, nullable = false)
    private String maNguon;

    @Column(name = "MaKho", length = 10)
    private String maKho;

    @Column(name = "MaVatTu", length = 50, nullable = false)
    private String maVatTu;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    @Column(name = "CapNhatLuc", length = 50)
    private String capNhatLuc;

    public TonKhoTruSo() {}

    public TonKhoTruSo(String id, String maNguon, String maKho,
                       String maVatTu, Integer soLuongTon, String capNhatLuc) {
        this.id = id;
        this.maNguon = maNguon;
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.soLuongTon = soLuongTon;
        this.capNhatLuc = capNhatLuc;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMaNguon() { return maNguon; }
    public void setMaNguon(String maNguon) { this.maNguon = maNguon; }
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(String capNhatLuc) { this.capNhatLuc = capNhatLuc; }
}
