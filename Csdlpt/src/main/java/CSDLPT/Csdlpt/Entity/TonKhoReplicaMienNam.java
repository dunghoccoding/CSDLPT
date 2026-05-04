package CSDLPT.Csdlpt.Entity;

import jakarta.persistence.*;

/**
 * Đọc từ bảng TON_KHO_MIEN_NAM tại Trụ Sở.
 * Bảng này được SQL Server Transactional Replication tự đồng bộ từ QuanLyKho_MienNam.
 * READ-ONLY — không được ghi vào bảng này từ phía Trụ Sở.
 */
@Entity
@Table(name = "TON_KHO_MIEN_NAM")
@IdClass(TonKhoID.class)
public class TonKhoReplicaMienNam {

    @Id
    @Column(name = "MaKho", length = 10)
    private String maKho;

    @Id
    @Column(name = "MaVatTu", length = 50)
    private String maVatTu;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    public TonKhoReplicaMienNam() {}

    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
}
