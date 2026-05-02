package CSDLPT.Csdlpt.dto;

import java.time.LocalDateTime;

public class GiaoDichMessage {
    private String maKho;
    private String maVatTu;
    private String loaiGiaoDich;
    private int soLuong;
    private LocalDateTime thoiGian;

    public GiaoDichMessage() {}

    public GiaoDichMessage(String maKho, String maVatTu, String loaiGiaoDich, int soLuong) {
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soLuong = soLuong;
        this.thoiGian = LocalDateTime.now();
    }

    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}