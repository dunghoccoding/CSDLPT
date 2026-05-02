package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.DlqMessage;
import CSDLPT.Csdlpt.repository.DlqMessageRepository;
import CSDLPT.Csdlpt.service.DlqConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API quản lý Dead Letter Queue (DLQ).
 * Hoạt động trên TẤT CẢ node (không có @Profile).
 *
 * Endpoints:
 *  GET  /api/admin/dlq                    → Xem tất cả message lỗi đang chờ
 *  GET  /api/admin/dlq/tat-ca             → Xem kể cả đã retry/xóa
 *  GET  /api/admin/dlq/thong-ke           → Thống kê số lượng theo trạng thái
 *  POST /api/admin/dlq/{id}/retry         → Retry 1 message cụ thể
 *  POST /api/admin/dlq/retry-tat-ca       → Retry tất cả message đang chờ
 *  DELETE /api/admin/dlq/{id}             → Xóa (đánh dấu) 1 message
 */
@RestController
@RequestMapping("/api/admin/dlq")
public class AdminDlqController {

    @Autowired
    private DlqMessageRepository dlqRepo;

    @Autowired
    private DlqConsumerService dlqConsumerService;

    /** Xem danh sách message lỗi đang chờ xử lý */
    @GetMapping
    public ResponseEntity<List<DlqMessage>> xemDangCho() {
        return ResponseEntity.ok(dlqRepo.findByTrangThaiOrderByThoiGianLoiDesc("DANG_CHO"));
    }

    /** Xem tất cả message (kể cả đã retry / đã xóa) */
    @GetMapping("/tat-ca")
    public ResponseEntity<List<DlqMessage>> xemTatCa() {
        return ResponseEntity.ok(dlqRepo.findAll());
    }

    /** Thống kê nhanh số lượng theo trạng thái */
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

    /** Retry một message cụ thể theo ID */
    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retryOne(@PathVariable String id) {
        return ResponseEntity.ok(dlqConsumerService.retryMessage(id));
    }

    /** Retry tất cả message đang ở trạng thái DANG_CHO */
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

    /** Xóa (đánh dấu DA_XOA) một message */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> xoaOne(@PathVariable String id) {
        return ResponseEntity.ok(dlqConsumerService.xoaMessage(id));
    }
}
