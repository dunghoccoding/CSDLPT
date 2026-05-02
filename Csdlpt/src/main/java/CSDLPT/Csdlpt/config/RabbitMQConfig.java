package CSDLPT.Csdlpt.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // ── Tên constants dùng chung ──────────────────────────────────────────
    public static final String EXCHANGE_TON_KHO   = "X_TonKho";
    public static final String QUEUE_MIEN_BAC     = "Q_TonKho_MienBac_V2";
    public static final String QUEUE_MIEN_NAM     = "Q_TonKho_MienNam_V2";
    public static final String ROUTING_MIEN_BAC   = "tonkho.MIEN_BAC";
    public static final String ROUTING_MIEN_NAM   = "tonkho.MIEN_NAM";

    // ── Constants Điều chuyển kho ─────────────────────────────────────────
    public static final String EXCHANGE_DIEU_CHUYEN        = "X_DieuChuyen";
    public static final String QUEUE_DIEU_CHUYEN_MIEN_BAC  = "Q_DieuChuyen_MienBac_V2";
    public static final String QUEUE_DIEU_CHUYEN_MIEN_NAM  = "Q_DieuChuyen_MienNam_V2";
    public static final String ROUTING_DC_MIEN_BAC         = "dieuchuyen.MIEN_BAC";
    public static final String ROUTING_DC_MIEN_NAM         = "dieuchuyen.MIEN_NAM";

    // ── Constants Dead Letter Queue ───────────────────────────────────────
    public static final String EXCHANGE_DEAD_LETTER = "X_DeadLetter";
    public static final String QUEUE_DEAD_LETTER    = "Q_DeadLetter";
    public static final String ROUTING_DEAD_LETTER  = "dlq.#";



    // Giữ lại queue cũ (đồng bộ DanhMucVatTu)
    @Bean
    public Queue dongBoVatTuQueue() {
        return new Queue("Q_DongBoVatTu", true);
    }

    // ── Dead Letter Exchange & Queue ──────────────────────────────────────
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DEAD_LETTER);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DEAD_LETTER).build();
    }

    @Bean
    public Binding bindingDeadLetter(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(QUEUE_DEAD_LETTER);
    }

    // ── Exchange tồn kho ──────────────────────────────────────────────────
    @Bean
    public TopicExchange tonKhoExchange() {
        return new TopicExchange(EXCHANGE_TON_KHO);
    }

    // ── Exchange Điều chuyển kho ──────────────────────────────────────────
    @Bean
    public TopicExchange dieuChuyenExchange() {
        return new TopicExchange(EXCHANGE_DIEU_CHUYEN);
    }

    // ── Queues (có khai báo DLQ arguments) ───────────────────────────────
    private Map<String, Object> dlqArgs(String queueName) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_DEAD_LETTER);
        args.put("x-dead-letter-routing-key", QUEUE_DEAD_LETTER);
        return args;
    }

    @Bean
    public Queue queueTonKhoMienBac() {
        return QueueBuilder.durable(QUEUE_MIEN_BAC)
                .withArguments(dlqArgs(QUEUE_MIEN_BAC))
                .build();
    }

    @Bean
    public Queue queueTonKhoMienNam() {
        return QueueBuilder.durable(QUEUE_MIEN_NAM)
                .withArguments(dlqArgs(QUEUE_MIEN_NAM))
                .build();
    }

    @Bean
    public Queue queueDieuChuyenMienBac() {
        return QueueBuilder.durable(QUEUE_DIEU_CHUYEN_MIEN_BAC)
                .withArguments(dlqArgs(QUEUE_DIEU_CHUYEN_MIEN_BAC))
                .build();
    }

    @Bean
    public Queue queueDieuChuyenMienNam() {
        return QueueBuilder.durable(QUEUE_DIEU_CHUYEN_MIEN_NAM)
                .withArguments(dlqArgs(QUEUE_DIEU_CHUYEN_MIEN_NAM))
                .build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────
    @Bean
    public Binding bindingMienBac(Queue queueTonKhoMienBac, TopicExchange tonKhoExchange) {
        return BindingBuilder.bind(queueTonKhoMienBac).to(tonKhoExchange).with(ROUTING_MIEN_BAC);
    }

    @Bean
    public Binding bindingMienNam(Queue queueTonKhoMienNam, TopicExchange tonKhoExchange) {
        return BindingBuilder.bind(queueTonKhoMienNam).to(tonKhoExchange).with(ROUTING_MIEN_NAM);
    }

    @Bean
    public Binding bindingDieuChuyenMienBac(Queue queueDieuChuyenMienBac,
                                             TopicExchange dieuChuyenExchange) {
        return BindingBuilder.bind(queueDieuChuyenMienBac)
                .to(dieuChuyenExchange).with(ROUTING_DC_MIEN_BAC);
    }

    @Bean
    public Binding bindingDieuChuyenMienNam(Queue queueDieuChuyenMienNam,
                                             TopicExchange dieuChuyenExchange) {
        return BindingBuilder.bind(queueDieuChuyenMienNam)
                .to(dieuChuyenExchange).with(ROUTING_DC_MIEN_NAM);
    }

    // ── Retry được cấu hình qua application.yaml ─────────────────────────
    // spring.rabbitmq.listener.simple.retry.enabled=true
    // Khi @RabbitListener throw exception sau MAX_ATTEMPTS lần → message vào DLQ
}
