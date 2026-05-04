package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.DlqMessage;
import CSDLPT.Csdlpt.repository.DlqMessageRepository;
import CSDLPT.Csdlpt.service.DlqConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/dlq")
public class AdminDlqController {

    @Autowired
    private DlqMessageRepository dlqRepo;

    @Autowired
    private DlqConsumerService dlqConsumerService;

    @GetMapping
    public ResponseEntity<List<DlqMessage>> xemDangCho() {
        return ResponseEntity.ok(dlqRepo.findByTrangThaiOrderByThoiGianLoiDesc("DANG_CHO"));
    }

    @GetMapping("/tat-ca")
    public ResponseEntity<List<DlqMessage>> xemTatCa() {
        return ResponseEntity.ok(dlqRepo.findAll());
    }
    @GetMapping("/thong-ke")
    public ResponseEntity<Map<String, Long>> thongKe() {
        long dangCho  = dlqRepo.countByTrangThai("DANG_CHO");
        long daRetry  = dlqRepo.countByTrangThai("DA_RETRY");
        long daXoa    = dlqRepo.countByTrangThai("DA_XOA");
        long tongCong = dlqRepo.count();

        return ResponseEntity.ok(Map.of(
                "dangCho",  dangCho,
                "daRetry",  daRetry,
                "daXoa",    daXoa,
                "tongCong", tongCong
        ));
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retryOne(@PathVariable String id) {
        return ResponseEntity.ok(dlqConsumerService.retryMessage(id));
    }

    @PostMapping("/retry-tat-ca")
    public ResponseEntity<String> retryAll() {
        List<DlqMessage> dangCho = dlqRepo.findByTrangThaiOrderByThoiGianLoiDesc("DANG_CHO");
        if (dangCho.isEmpty()) {
            return ResponseEntity.ok("Không có message nào đang chờ retry.");
        }
        int thanhCong = 0;
        int thatBai   = 0;
        for (DlqMessage dlq : dangCho) {
            String ketQua = dlqConsumerService.retryMessage(dlq.getId());
            if (ketQua.startsWith("Retry thành công")) thanhCong++;
            else thatBai++;
        }
        return ResponseEntity.ok(
                "Đã retry " + dangCho.size() + " message: "
                        + thanhCong + " thành công, " + thatBai + " thất bại.");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> xoaOne(@PathVariable String id) {
        return ResponseEntity.ok(dlqConsumerService.xoaMessage(id));
    }
}
