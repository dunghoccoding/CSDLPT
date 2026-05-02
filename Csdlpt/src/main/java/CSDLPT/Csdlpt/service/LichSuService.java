package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import CSDLPT.Csdlpt.repository.LichSuGiaoDichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service ghi lịch sử giao dịch (Audit Log) vào MongoDB.
 * Được inject vào mọi service xử lý nghiệp vụ (PhanHeSqlService,
 * DieuChuyenSubscriberService, TruSoSubscriberService).
 *
 * Không khai báo @Profile → hoạt động trên TẤT CẢ node.
 */
@Service
public class LichSuService {

    @Autowired
    private LichSuGiaoDichRepository lichSuRepo;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Ghi một bản ghi lịch sử thành công.
     *
     * @param loaiGiaoDich  NHAP | XUAT | DIEU_CHUYEN_XUAT | DIEU_CHUYEN_NHAP | SYNC
     * @param maNguon       MIEN_BAC | MIEN_NAM | TRU_SO
     * @param maKho         Mã kho thực hiện
     * @param maVatTu       Mã vật tư
     * @param tenVatTu      Tên vật tư (có thể null)
     * @param soLuong       Số lượng giao dịch
     */
    public void ghiThanhCong(String loaiGiaoDich, String maNguon, String maKho,
                             String maVatTu, String tenVatTu, Integer soLuong) {
        ghi(loaiGiaoDich, maNguon, maKho, maVatTu, tenVatTu, soLuong, "THANH_CONG", null);
    }

    /**
     * Ghi một bản ghi lịch sử thất bại.
     *
     * @param loaiGiaoDich  Loại giao dịch
     * @param maNguon       Node thực hiện
     * @param maKho         Mã kho
     * @param maVatTu       Mã vật tư
     * @param soLuong       Số lượng (nếu có)
     * @param lyDoLoi       Thông báo lỗi
     */
    public void ghiThatBai(String loaiGiaoDich, String maNguon, String maKho,
                           String maVatTu, Integer soLuong, String lyDoLoi) {
        ghi(loaiGiaoDich, maNguon, maKho, maVatTu, null, soLuong, "THAT_BAI", lyDoLoi);
    }

    // ── Internal ──────────────────────────────────────────────────────────
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
            // Lỗi audit không nên làm gián đoạn luồng chính
            System.err.println("[LichSuService] Không thể ghi lịch sử: " + e.getMessage());
        }
    }
}
