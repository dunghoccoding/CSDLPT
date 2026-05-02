package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.YeuCauDieuChuyen;
import CSDLPT.Csdlpt.dto.DieuChuyenRequest;
import CSDLPT.Csdlpt.repository.YeuCauDieuChuyenRepository;
import CSDLPT.Csdlpt.service.PhanHeSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/yeu-cau")
public class YeuCauController {

    @Autowired
    private YeuCauDieuChuyenRepository yeuCauRepository;

    @Autowired(required = false) // Không có trên Trụ sở
    private PhanHeSqlService phanHeSqlService;

    @Value("${app.ma-nguon:TRU_SO}")
    private String maNguon;

    @PostMapping("/tao")
    public ResponseEntity<String> taoYeuCau(@RequestBody YeuCauDieuChuyen yeuCau) {
        yeuCau.setMaNguonXin(maNguon);
        yeuCau.setTrangThai("PENDING");
        yeuCau.setNgayTao(LocalDateTime.now());
        yeuCauRepository.save(yeuCau);
        return ResponseEntity.ok("Đã gửi yêu cầu điều chuyển tới " + yeuCau.getMaNguonCho());
    }

    @GetMapping
    public ResponseEntity<Map<String, List<YeuCauDieuChuyen>>> layDanhSachYeuCau() {
        Map<String, List<YeuCauDieuChuyen>> result = new HashMap<>();
        if ("TRU_SO".equals(maNguon)) {
            result.put("tatCa", yeuCauRepository.findAll());
        } else {
            result.put("guiDi", yeuCauRepository.findByMaNguonXin(maNguon));
            result.put("nhanDuoc", yeuCauRepository.findByMaNguonCho(maNguon));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/duyet")
    public ResponseEntity<String> duyetYeuCau(@PathVariable String id) {
        Optional<YeuCauDieuChuyen> opt = yeuCauRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu");
        
        YeuCauDieuChuyen yeuCau = opt.get();
        if (!"PENDING".equals(yeuCau.getTrangThai())) {
            return ResponseEntity.badRequest().body("Yêu cầu này không ở trạng thái chờ duyệt");
        }
        if (!maNguon.equals(yeuCau.getMaNguonCho())) {
            return ResponseEntity.status(403).body("Bạn không có quyền duyệt yêu cầu của kho khác!");
        }

        // Gọi hàm điều chuyển cục bộ -> trừ kho -> bắn RabbitMQ
        DieuChuyenRequest req = new DieuChuyenRequest();
        req.setMaKhoXuat(yeuCau.getMaKhoCho());
        req.setMaNguonNhan(yeuCau.getMaNguonXin());
        req.setMaKhoNhan(yeuCau.getMaKhoXin());
        req.setMaVatTu(yeuCau.getMaVatTu());
        req.setSoLuong(yeuCau.getSoLuong());

        String result = phanHeSqlService.dieuChuyenKho(req);
        if (result.startsWith("Lỗi")) {
            return ResponseEntity.badRequest().body(result);
        }

        yeuCau.setTrangThai("APPROVED");
        yeuCau.setNgayDuyet(LocalDateTime.now());
        yeuCauRepository.save(yeuCau);

        return ResponseEntity.ok("Đã duyệt thành công. Hàng đang được chuyển đi!");
    }

    @PostMapping("/{id}/tu-choi")
    public ResponseEntity<String> tuChoiYeuCau(@PathVariable String id) {
        Optional<YeuCauDieuChuyen> opt = yeuCauRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu");
        
        YeuCauDieuChuyen yeuCau = opt.get();
        if (!maNguon.equals(yeuCau.getMaNguonCho())) {
            return ResponseEntity.status(403).body("Bạn không có quyền từ chối yêu cầu của kho khác!");
        }

        yeuCau.setTrangThai("REJECTED");
        yeuCau.setNgayDuyet(LocalDateTime.now());
        yeuCauRepository.save(yeuCau);

        return ResponseEntity.ok("Đã từ chối yêu cầu.");
    }
}
