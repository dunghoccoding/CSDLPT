package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import CSDLPT.Csdlpt.repository.LichSuGiaoDichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (tuNgay != null && denNgay != null) {
            return ResponseEntity.ok(lichSuRepo.findByThoiGianBetween(tuNgay, denNgay));
        }

        if (maNguon != null && loaiGD != null) {
            return ResponseEntity.ok(
                    lichSuRepo.findByMaNguonAndLoaiGiaoDichOrderByThoiGianDesc(maNguon, loaiGD));
        }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> xemChiTiet(@PathVariable String id) {
        return lichSuRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
