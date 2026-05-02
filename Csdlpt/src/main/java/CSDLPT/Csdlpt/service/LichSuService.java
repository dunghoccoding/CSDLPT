package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import CSDLPT.Csdlpt.repository.LichSuGiaoDichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class LichSuService {

    @Autowired
    private LichSuGiaoDichRepository lichSuRepo;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void ghiThanhCong(String loaiGiaoDich, String maNguon, String maKho,
                             String maVatTu, String tenVatTu, Integer soLuong) {
        ghi(loaiGiaoDich, maNguon, maKho, maVatTu, tenVatTu, soLuong, "THANH_CONG", null);
    }

    public void ghiThatBai(String loaiGiaoDich, String maNguon, String maKho,
                           String maVatTu, Integer soLuong, String lyDoLoi) {
        ghi(loaiGiaoDich, maNguon, maKho, maVatTu, null, soLuong, "THAT_BAI", lyDoLoi);
    }

    private void ghi(String loaiGiaoDich, String maNguon, String maKho,
                     String maVatTu, String tenVatTu, Integer soLuong,
                     String trangThai, String ghiChu) {
        try {
            LichSuGiaoDich ls = new LichSuGiaoDich(
                    UUID.randomUUID().toString(),
                    loaiGiaoDich,
                    maNguon,
                    maKho,
                    maVatTu,
                    tenVatTu,
                    soLuong,
                    trangThai,
                    ghiChu,
                    LocalDateTime.now().format(FORMATTER)
            );
            lichSuRepo.save(ls);
        } catch (Exception e) {
            System.err.println("[LichSuService] Không thể ghi lịch sử: " + e.getMessage());
        }
    }
}
