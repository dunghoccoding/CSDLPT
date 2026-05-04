package CSDLPT.Csdlpt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TonKhoMessage {


    private String loaiGiaoDich;
    private String maNguon;
    private String maKho;
    private String maVatTu;
    private String tenVatTu;
    private Integer soLuong;
    private String capNhatLuc;

    public TonKhoMessage() {}

    public TonKhoMessage(String loaiGiaoDich, String maNguon, String maKho,
                         String maVatTu, String tenVatTu,
                         Integer soLuong, String capNhatLuc) {
        this.loaiGiaoDich = loaiGiaoDich;
        this.maNguon = maNguon;
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.soLuong = soLuong;
        this.capNhatLuc = capNhatLuc;
    }

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
    public String getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(String capNhatLuc) { this.capNhatLuc = capNhatLuc; }
}
