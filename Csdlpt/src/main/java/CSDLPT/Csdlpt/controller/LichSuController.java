package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import CSDLPT.Csdlpt.repository.LichSuGiaoDichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API xem lịch sử giao dịch (Audit Log).
 * Hoạt động trên TẤT CẢ node (không có @Profile).
 *
 * Endpoints:
 *  GET /api/lich-su                            → Toàn bộ lịch sử (mới nhất trước)
 *  GET /api/lich-su?maNguon=MIEN_BAC           → Lọc theo node
 *  GET /api/lich-su?loaiGD=NHAP                → Lọc theo loại giao dịch
 *  GET /api/lich-su?maVatTu=VT001              → Lọc theo vật tư
 *  GET /api/lich-su?trangThai=THAT_BAI         → Lọc theo trạng thái
 *  GET /api/lich-su?maNguon=MIEN_BAC&loaiGD=NHAP  → Kết hợp 2 filter
 *  GET /api/lich-su?tuNgay=2026-05-01 00:00:00&denNgay=2026-05-02 23:59:59
 */
@RestController
@RequestMapping("/api/lich-su")
public class LichSuController {

    @Autowired
    private LichSuGiaoDichRepository lichSuRepo;

    @GetMapping
    public ResponseEntity<List<LichSuGiaoDich>> xemLichSu(
            @RequestParam(required = false) String maNguon,
            @RequestParam(required = false) String loaiGD,
            @RequestParam(required = false) String maVatTu,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tuNgay,
            @RequestParam(required = false) String denNgay) {

        // ── Khoảng thời gian ─────────────────────────────────────────────
        if (tuNgay != null && denNgay != null) {
            return ResponseEntity.ok(lichSuRepo.findByThoiGianBetween(tuNgay, denNgay));
        }

        // ── Kết hợp maNguon + loaiGD ─────────────────────────────────────
        if (maNguon != null && loaiGD != null) {
            return ResponseEntity.ok(
                    lichSuRepo.findByMaNguonAndLoaiGiaoDichOrderByThoiGianDesc(maNguon, loaiGD));
        }

        // ── Single filters ────────────────────────────────────────────────
        if (maNguon != null) {
            return ResponseEntity.ok(lichSuRepo.findByMaNguonOrderByThoiGianDesc(maNguon));
        }
        if (loaiGD != null) {
            return ResponseEntity.ok(lichSuRepo.findByLoaiGiaoDichOrderByThoiGianDesc(loaiGD));
        }
        if (maVatTu != null) {
            return ResponseEntity.ok(lichSuRepo.findByMaVatTuOrderByThoiGianDesc(maVatTu));
        }
        if (trangThai != null) {
            return ResponseEntity.ok(lichSuRepo.findByTrangThaiOrderByThoiGianDesc(trangThai));
        }

        // ── Toàn bộ ──────────────────────────────────────────────────────
        return ResponseEntity.ok(lichSuRepo.findAllByOrderByThoiGianDesc());
    }

    /** Xem chi tiết một bản ghi */
    @GetMapping("/{id}")
    public ResponseEntity<?> xemChiTiet(@PathVariable String id) {
        return lichSuRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
