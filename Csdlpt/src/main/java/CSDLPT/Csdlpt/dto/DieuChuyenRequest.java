package CSDLPT.Csdlpt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DieuChuyenRequest {
    private String maKhoXuat;
    private String maKhoNhan;
    private String maNguonNhan; // "MIEN_BAC" hoặc "MIEN_NAM"
    private String maVatTu;
    private Integer soLuong;

    public DieuChuyenRequest() {}

    public String getMaKhoXuat() { return maKhoXuat; }
    public void setMaKhoXuat(String maKhoXuat) { this.maKhoXuat = maKhoXuat; }

    public String getMaKhoNhan() { return maKhoNhan; }
    public void setMaKhoNhan(String maKhoNhan) { this.maKhoNhan = maKhoNhan; }

    public String getMaNguonNhan() { return maNguonNhan; }
    public void setMaNguonNhan(String maNguonNhan) { this.maNguonNhan = maNguonNhan; }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}
