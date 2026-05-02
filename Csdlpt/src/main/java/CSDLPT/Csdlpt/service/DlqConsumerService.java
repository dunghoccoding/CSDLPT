package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.DlqMessage;
import CSDLPT.Csdlpt.config.RabbitMQConfig;
import CSDLPT.Csdlpt.repository.DlqMessageRepository;
import com.rabbitmq.client.LongString;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Lắng nghe Dead Letter Queue — lưu tất cả message lỗi vào MongoDB.
 * Hoạt động trên TẤT CẢ node (không có @Profile).
 *
 * Khi một message bị reject (throw exception) từ listener gốc,
 * RabbitMQ sẽ chuyển nó sang X_DeadLetter → Q_DeadLetter.
 * Service này nhận và lưu vào MongoDB để admin xem & retry.
 */
@Service
public class DlqConsumerService {

    @Autowired
    private DlqMessageRepository dlqRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Nhận message từ Dead Letter Queue và lưu vào MongoDB.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEAD_LETTER)
    public void nhanMessageLoi(Message message) {
        String noiDung = new String(message.getBody());
        String queueNguon = extractQueueNguon(message);
        String lyDoLoi    = extractLyDoLoi(message);

        System.err.println("[DLQ] Nhận message lỗi từ queue: " + queueNguon);
        System.err.println("[DLQ] Nội dung: " + noiDung);
        System.err.println("[DLQ] Lý do: "    + lyDoLoi);

        DlqMessage dlq = new DlqMessage(
                UUID.randomUUID().toString(),
                queueNguon,
                noiDung,
                lyDoLoi,
                1,
                "DANG_CHO",
                LocalDateTime.now().format(FORMATTER)
        );
        dlqRepo.save(dlq);
    }

    /**
     * Retry một message DLQ: lấy từ MongoDB, re-publish về queue gốc.
     *
     * @param dlqId  ID của DlqMessage trong MongoDB
     * @return kết quả retry
     */
    public String retryMessage(String dlqId) {
        Optional<DlqMessage> opt = dlqRepo.findById(dlqId);
        if (opt.isEmpty()) {
            return "Không tìm thấy message DLQ với id: " + dlqId;
        }

        DlqMessage dlq = opt.get();
        if ("DA_XOA".equals(dlq.getTrangThai())) {
            return "Message này đã bị xóa, không thể retry.";
        }

        try {
            // Re-publish về queue gốc
            rabbitTemplate.convertAndSend(dlq.getQueueNguon(), dlq.getNoiDungMessage());

            // Cập nhật trạng thái
            dlq.setTrangThai("DA_RETRY");
            dlq.setSoLanThu(dlq.getSoLanThu() + 1);
            dlq.setThoiGianRetry(LocalDateTime.now().format(FORMATTER));
            dlqRepo.save(dlq);

            System.out.println("[DLQ] Retry thành công message " + dlqId
                    + " → queue: " + dlq.getQueueNguon());
            return "Retry thành công! Message đã được gửi lại về queue: " + dlq.getQueueNguon();

        } catch (Exception e) {
            return "Retry thất bại: " + e.getMessage();
        }
    }

    /**
     * Xóa (đánh dấu) một message DLQ khỏi danh sách chờ.
     */
    public String xoaMessage(String dlqId) {
        Optional<DlqMessage> opt = dlqRepo.findById(dlqId);
        if (opt.isEmpty()) return "Không tìm thấy message DLQ: " + dlqId;

        DlqMessage dlq = opt.get();
        dlq.setTrangThai("DA_XOA");
        dlqRepo.save(dlq);
        return "Đã đánh dấu xóa message: " + dlqId;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private String extractQueueNguon(Message message) {
        try {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            Object xDeath = headers.get("x-death");
            if (xDeath instanceof java.util.List<?> list && !list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof Map<?, ?> deathMap) {
                    Object queue = ((Map<String, Object>) deathMap).get("queue");
                    if (queue instanceof LongString) return queue.toString();
                    if (queue instanceof String) return (String) queue;
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN";
    }

    @SuppressWarnings("unchecked")
    private String extractLyDoLoi(Message message) {
        try {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            Object xDeath = headers.get("x-death");
            if (xDeath instanceof java.util.List<?> list && !list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof Map<?, ?> deathMap) {
                    Object reason = ((Map<String, Object>) deathMap).get("reason");
                    if (reason != null) return reason.toString();
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN";
    }
}
