package CSDLPT.Csdlpt.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TON_KHO")
@IdClass(CSDLPT.Csdlpt.Entity.TonKhoID.class)
public class TonKho {

    @Id
    @Column(name = "MaKho", length = 10)
    private String maKho;

    @Id
    @Column(name = "MaVatTu", length = 50)
    private String maVatTu;

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    public TonKho() {}

    public TonKho(String maKho, String maVatTu, Integer soLuongTon) {
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.soLuongTon = soLuongTon;
    }

    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public String getMaVatTu() {
        return maVatTu;
    }

    public void setMaVatTu(String maVatTu) {
        this.maVatTu = maVatTu;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
}