package CSDLPT.Csdlpt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class VatTuRequest {
    // SQL Server
    private String maKho;
    private String maVatTu;
    private Integer soLuongTon;

    //  MongoDB
    private String tenVatTu;
    private String loai;
    private Map<String, Object> thongSoKyThuat;

    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public Map<String, Object> getThongSoKyThuat() { return thongSoKyThuat; }
    public void setThongSoKyThuat(Map<String, Object> thongSoKyThuat) { this.thongSoKyThuat = thongSoKyThuat; }
}