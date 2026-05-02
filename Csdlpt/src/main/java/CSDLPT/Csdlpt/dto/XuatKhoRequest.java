package CSDLPT.Csdlpt.dto;

public class XuatKhoRequest {
    private String maKho;
    private String maVatTu;
    private int soLuongXuat;

    // Getters và Setters
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public int getSoLuongXuat() { return soLuongXuat; }
    public void setSoLuongXuat(int soLuongXuat) { this.soLuongXuat = soLuongXuat; }
}