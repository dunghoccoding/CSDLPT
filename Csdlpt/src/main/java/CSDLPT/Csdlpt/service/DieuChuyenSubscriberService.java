package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.TonKho;
import CSDLPT.Csdlpt.config.RabbitMQConfig;
import CSDLPT.Csdlpt.dto.DieuChuyenRequest;
import CSDLPT.Csdlpt.repository.TonKhoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile({"mien-bac", "mien-nam"})
@Service
public class DieuChuyenSubscriberService {

    @Value("${app.ma-nguon}")
    private String maNguon;

    @Autowired
    private TonKhoRepository tonKhoRepository;

    @Autowired
    private PhanHeSqlService phanHeSqlService;

    @Autowired
    private LichSuService lichSuService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lắng nghe gói tin Điều chuyển từ Node khác chuyển tới.
     * Tự động xác định Queue cần nghe dựa trên Profile thông qua @RabbitListener.
     */
    @RabbitListener(queues = "#{ '${app.ma-nguon}' == 'MIEN_BAC' ? '" + RabbitMQConfig.QUEUE_DIEU_CHUYEN_MIEN_BAC + "' : '" + RabbitMQConfig.QUEUE_DIEU_CHUYEN_MIEN_NAM + "' }")
    @Transactional
    public void nhanDieuChuyen(String messageJson) {
        DieuChuyenRequest req = null;
        try {
            req = objectMapper.readValue(messageJson, DieuChuyenRequest.class);

            // Đảm bảo tin nhắn này đúng là gửi cho mình
            if (!req.getMaNguonNhan().equals(maNguon)) {
                return;
            }

            System.out.println(">> [RabbitMQ] Nhận được lô hàng điều chuyển từ "
                    + req.getMaKhoXuat() + " | SL: " + req.getSoLuong());

            // 1. Cộng vào kho nhận
            Optional<TonKho> existing = tonKhoRepository.findByMaKhoAndMaVatTu(
                    req.getMaKhoNhan(), req.getMaVatTu());

            TonKho tonKho;
            if (existing.isPresent()) {
                tonKho = existing.get();
                tonKho.setSoLuongTon(tonKho.getSoLuongTon() + req.getSoLuong());
            } else {
                tonKho = new TonKho(req.getMaKhoNhan(), req.getMaVatTu(), req.getSoLuong());
            }
            tonKhoRepository.save(tonKho);
            System.out.println(">> [SQL " + maNguon + "] Đã nhập điều chuyển "
                    + req.getSoLuong() + " | Tồn mới: " + tonKho.getSoLuongTon());

            // 2. Publish tin nhắn NHAP về Trụ Sở để Replica báo cáo
            phanHeSqlService.publishTonKho("NHAP", req.getMaKhoNhan(), req.getMaVatTu(),
                    null, req.getSoLuong(), phanHeSqlService.now());

            // ── Ghi lịch sử ──────────────────────────────────────────────
            lichSuService.ghiThanhCong("DIEU_CHUYEN_NHAP", maNguon, req.getMaKhoNhan(),
                    req.getMaVatTu(), null, req.getSoLuong());

        } catch (Exception e) {
            // Ghi lịch sử thất bại
            if (req != null) {
                lichSuService.ghiThatBai("DIEU_CHUYEN_NHAP", maNguon,
                        req.getMaKhoNhan(), req.getMaVatTu(), req.getSoLuong(), e.getMessage());
            }
            // Throw để kích hoạt Retry Mechanism → sau 3 lần thất bại → DLQ
            throw new RuntimeException("[DieuChuyenSubscriber] Xử lý thất bại: " + e.getMessage(), e);
        }
    }
}
