package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.dto.DieuChuyenRequest;
import CSDLPT.Csdlpt.dto.VatTuRequest;
import CSDLPT.Csdlpt.dto.XuatKhoRequest;
import CSDLPT.Csdlpt.service.DongBoService;
import CSDLPT.Csdlpt.service.PhanHeSqlService;
import CSDLPT.Csdlpt.Entity.TonKho;
import CSDLPT.Csdlpt.repository.TonKhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Profile({"mien-bac", "mien-nam"})
@RestController
@RequestMapping("/api/vattu")
public class VatTuController {

    @Autowired
    private TonKhoRepository tonKhoRepository;

    @Autowired
    private PhanHeSqlService phanHeSqlService;

    @GetMapping("/ton-kho")
    public ResponseEntity<List<TonKho>> xemTonKhoCucBo() {
        return ResponseEntity.ok(tonKhoRepository.findAll());
    }

    @Autowired
    private DongBoService dongBoService;

    /**
     * Nhập kho: cộng thêm số lượng, publish NHAP → Trụ Sở
     * POST /api/vattu/nhap-kho
     */
    @PostMapping("/nhap-kho")
    public ResponseEntity<String> nhapKho(@RequestBody VatTuRequest request) {
        System.out.println("--- YÊU CẦU NHẬP KHO ---");
        return ResponseEntity.ok(phanHeSqlService.nhapKhoVaBaoTin(request));
    }

    /**
     * Xuất kho: trừ số lượng, publish XUAT → Trụ Sở
     * POST /api/vattu/xuat-kho
     */
    @PostMapping("/xuat-kho")
    public ResponseEntity<String> xuatKho(@RequestBody XuatKhoRequest request) {
        System.out.println("--- YÊU CẦU XUẤT KHO ---");
        return ResponseEntity.ok(phanHeSqlService.xuatKhoVaBaoTin(request));
    }

    /**
     * Điều chuyển kho: trừ ở node này, gửi RabbitMQ sang node khác
     * POST /api/vattu/dieu-chuyen
     */
    @PostMapping("/dieu-chuyen")
    public ResponseEntity<String> dieuChuyen(@RequestBody DieuChuyenRequest request) {
        System.out.println("--- YÊU CẦU ĐIỀU CHUYỂN KHO ---");
        return ResponseEntity.ok(phanHeSqlService.dieuChuyenKho(request));
    }

    /**
     * Initial sync: đẩy toàn bộ tồn kho hiện có lên Trụ Sở (loại SYNC)
     * POST /api/vattu/dong-bo-toan-bo
     */
    @PostMapping("/dong-bo-toan-bo")
    public ResponseEntity<String> dongBoToanBo() {
        System.out.println("--- YÊU CẦU ĐỒNG BỘ TOÀN BỘ ---");
        return ResponseEntity.ok(dongBoService.dongBoToanBo());
    }
}