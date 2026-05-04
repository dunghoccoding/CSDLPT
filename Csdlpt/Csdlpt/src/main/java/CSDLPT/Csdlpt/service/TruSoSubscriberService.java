package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.TonKhoMien;
import CSDLPT.Csdlpt.Entity.TonKhoTruSo;
import CSDLPT.Csdlpt.config.RabbitMQConfig;
import CSDLPT.Csdlpt.dto.TonKhoMessage;
import CSDLPT.Csdlpt.repository.TonKhoMienRepository;
import CSDLPT.Csdlpt.repository.TonKhoTruSoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Profile("tru-so")
@Service
public class TruSoSubscriberService {

    @Autowired
    private TonKhoMienRepository tonKhoMienRepository;

    @Autowired
    private TonKhoTruSoRepository tonKhoTruSoRepository;

    @Autowired
    private LichSuService lichSuService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MIEN_BAC, RabbitMQConfig.QUEUE_MIEN_NAM})
    public void nhanTonKhoTuMien(String jsonMessage) {
        TonKhoMessage msg = null;
        try {
            System.out.println("<< [TruSo] Nhận message: " + jsonMessage);
            msg = objectMapper.readValue(jsonMessage, TonKhoMessage.class);

            String id = msg.getMaNguon() + "_" + msg.getMaVatTu();
            String thoiGian = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


            apDungDeltaMongo(id, msg, thoiGian);


            apDungDeltaSql(id, msg, thoiGian);


            lichSuService.ghiThanhCong(
                    msg.getLoaiGiaoDich() + "_TRUSOGHINHAP",
                    "TRU_SO",
                    msg.getMaKho(),
                    msg.getMaVatTu(),
                    msg.getTenVatTu(),
                    msg.getSoLuong()
            );

        } catch (Exception e) {
            System.err.println("Lỗi xử lý message tồn kho: " + e.getMessage());
            e.printStackTrace();
            if (msg != null) {
                lichSuService.ghiThatBai(
                        "SYNC_TRUSOGHINHAP", "TRU_SO",
                        msg.getMaKho(), msg.getMaVatTu(), msg.getSoLuong(), e.getMessage());
            }
            throw new RuntimeException("[TruSoSubscriber] Xử lý thất bại: " + e.getMessage(), e);
        }
    }

    private void apDungDeltaMongo(String id, TonKhoMessage msg, String thoiGian) {
        Optional<TonKhoMien> existing = tonKhoMienRepository.findById(id);

        int soLuongHienTai = existing.map(TonKhoMien::getSoLuongTon).orElse(0);
        int soLuongMoi = tinhSoLuongMoi(msg.getLoaiGiaoDich(), soLuongHienTai, msg.getSoLuong());

        TonKhoMien entity = existing.orElse(new TonKhoMien());
        entity.setId(id);
        entity.setMaNguon(msg.getMaNguon());
        entity.setMaKho(msg.getMaKho());
        entity.setMaVatTu(msg.getMaVatTu());
        entity.setSoLuongTon(soLuongMoi);
        entity.setCapNhatLuc(thoiGian);
        tonKhoMienRepository.save(entity);

        System.out.println("<< [MongoDB] " + id + " | " + msg.getLoaiGiaoDich()
                + " " + msg.getSoLuong() + " | " + soLuongHienTai + " → " + soLuongMoi);
    }

    private void apDungDeltaSql(String id, TonKhoMessage msg, String thoiGian) {
        Optional<TonKhoTruSo> existing = tonKhoTruSoRepository.findById(id);

        int soLuongHienTai = existing.map(TonKhoTruSo::getSoLuongTon).orElse(0);
        int soLuongMoi = tinhSoLuongMoi(msg.getLoaiGiaoDich(), soLuongHienTai, msg.getSoLuong());

        TonKhoTruSo entity = existing.orElse(new TonKhoTruSo());
        entity.setId(id);
        entity.setMaNguon(msg.getMaNguon());
        entity.setMaKho(msg.getMaKho());
        entity.setMaVatTu(msg.getMaVatTu());
        entity.setSoLuongTon(soLuongMoi);
        entity.setCapNhatLuc(thoiGian);
        tonKhoTruSoRepository.save(entity);

        System.out.println("<< [SQL TruSo] " + id + " | " + msg.getLoaiGiaoDich()
                + " " + msg.getSoLuong() + " | " + soLuongHienTai + " → " + soLuongMoi);
    }

    private int tinhSoLuongMoi(String loaiGiaoDich, int hienTai, int delta) {
        if (delta == 0 && loaiGiaoDich == null) return hienTai;
        return switch (loaiGiaoDich) {
            case "NHAP" -> hienTai + delta;
            case "XUAT" -> Math.max(0, hienTai - delta);
            case "SYNC" -> delta;
            default -> hienTai;
        };
    }
}
