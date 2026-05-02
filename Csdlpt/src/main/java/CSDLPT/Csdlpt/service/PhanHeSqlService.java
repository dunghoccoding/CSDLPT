package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.TonKho;
import CSDLPT.Csdlpt.config.RabbitMQConfig;
import CSDLPT.Csdlpt.dto.DieuChuyenRequest;
import CSDLPT.Csdlpt.dto.TonKhoMessage;
import CSDLPT.Csdlpt.dto.VatTuRequest;
import CSDLPT.Csdlpt.dto.XuatKhoRequest;
import CSDLPT.Csdlpt.repository.TonKhoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Profile({"mien-bac", "mien-nam"})
@Service
public class PhanHeSqlService {

    @Value("${app.ma-nguon}")
    private String maNguon;

    @Autowired
    private TonKhoRepository tonKhoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private LichSuService lichSuService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public String nhapKhoVaBaoTin(VatTuRequest req) {
        try {
            Optional<TonKho> existing = tonKhoRepository.findByMaKhoAndMaVatTu(
                    req.getMaKho(), req.getMaVatTu());

            int soLuongNhap = req.getSoLuongTon();
            TonKho tonKho;
            if (existing.isPresent()) {
                tonKho = existing.get();
                tonKho.setSoLuongTon(tonKho.getSoLuongTon() + soLuongNhap);
            } else {
                tonKho = new TonKho(req.getMaKho(), req.getMaVatTu(), soLuongNhap);
            }
            tonKhoRepository.save(tonKho);
            System.out.println(">> [SQL " + maNguon + "] Nhập " + soLuongNhap
                    + " | Tổng tồn: " + tonKho.getSoLuongTon());


            String jsonVatTu = objectMapper.writeValueAsString(req);
            rabbitTemplate.convertAndSend("Q_DongBoVatTu", jsonVatTu);

            publishTonKho("NHAP", req.getMaKho(), req.getMaVatTu(),
                    req.getTenVatTu(), soLuongNhap, now());

            lichSuService.ghiThanhCong("NHAP", maNguon, req.getMaKho(),
                    req.getMaVatTu(), req.getTenVatTu(), soLuongNhap);

            return "Thành công! [" + maNguon + "] Nhập " + soLuongNhap
                    + " | Tồn kho hiện tại: " + tonKho.getSoLuongTon();

        } catch (Exception e) {
            lichSuService.ghiThatBai("NHAP", maNguon, req.getMaKho(),
                    req.getMaVatTu(), req.getSoLuongTon(), e.getMessage());
            return "Lỗi nhập kho: " + e.getMessage();
        }
    }

    @Transactional
    public String xuatKhoVaBaoTin(XuatKhoRequest req) {
        try {
            Optional<TonKho> existing = tonKhoRepository.findByMaKhoAndMaVatTu(
                    req.getMaKho(), req.getMaVatTu());

            if (existing.isEmpty()) {
                return "Lỗi: Không tìm thấy vật tư " + req.getMaVatTu()
                        + " trong kho " + req.getMaKho();
            }

            TonKho tonKho = existing.get();
            int soLuongXuat = req.getSoLuongXuat();

            if (tonKho.getSoLuongTon() < soLuongXuat) {
                return "Lỗi: Tồn kho không đủ! Hiện có: " + tonKho.getSoLuongTon()
                        + " | Yêu cầu xuất: " + soLuongXuat;
            }

            tonKho.setSoLuongTon(tonKho.getSoLuongTon() - soLuongXuat);
            tonKhoRepository.save(tonKho);
            System.out.println(">> [SQL " + maNguon + "] Xuất " + soLuongXuat
                    + " | Tồn còn lại: " + tonKho.getSoLuongTon());


            publishTonKho("XUAT", req.getMaKho(), req.getMaVatTu(),
                    null, soLuongXuat, now());

            lichSuService.ghiThanhCong("XUAT", maNguon, req.getMaKho(),
                    req.getMaVatTu(), null, soLuongXuat);

            return "Thành công! [" + maNguon + "] Xuất " + soLuongXuat
                    + " | Tồn còn lại: " + tonKho.getSoLuongTon();

        } catch (Exception e) {
            lichSuService.ghiThatBai("XUAT", maNguon, req.getMaKho(),
                    req.getMaVatTu(), req.getSoLuongXuat(), e.getMessage());
            return "Lỗi xuất kho: " + e.getMessage();
        }
    }

    @Transactional
    public String dieuChuyenKho(DieuChuyenRequest req) {
        try {
            Optional<TonKho> existing = tonKhoRepository.findByMaKhoAndMaVatTu(
                    req.getMaKhoXuat(), req.getMaVatTu());

            if (existing.isEmpty()) {
                return "Lỗi: Không tìm thấy vật tư " + req.getMaVatTu() + " trong kho " + req.getMaKhoXuat();
            }

            TonKho tonKho = existing.get();
            int soLuongChuyen = req.getSoLuong();

            if (tonKho.getSoLuongTon() < soLuongChuyen) {
                return "Lỗi: Tồn kho không đủ để điều chuyển! Hiện có: " + tonKho.getSoLuongTon();
            }


            tonKho.setSoLuongTon(tonKho.getSoLuongTon() - soLuongChuyen);
            tonKhoRepository.save(tonKho);
            System.out.println(">> [SQL " + maNguon + "] Điều chuyển XUẤT " + soLuongChuyen
                    + " | Tồn còn lại: " + tonKho.getSoLuongTon());


            publishTonKho("XUAT", req.getMaKhoXuat(), req.getMaVatTu(), null, soLuongChuyen, now());


            String jsonDieuChuyen = objectMapper.writeValueAsString(req);
            String routingKey = "dieuchuyen." + req.getMaNguonNhan();
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_DIEU_CHUYEN, routingKey, jsonDieuChuyen);
            System.out.println(">> [RabbitMQ] Đã gửi yêu cầu điều chuyển tới: " + routingKey);

            lichSuService.ghiThanhCong("DIEU_CHUYEN_XUAT", maNguon, req.getMaKhoXuat(),
                    req.getMaVatTu(), null, soLuongChuyen);

            return "Thành công! Đã trừ kho và gửi yêu cầu điều chuyển sang " + req.getMaNguonNhan();

        } catch (Exception e) {
            lichSuService.ghiThatBai("DIEU_CHUYEN_XUAT", maNguon, req.getMaKhoXuat(),
                    req.getMaVatTu(), req.getSoLuong(), e.getMessage());
            return "Lỗi điều chuyển: " + e.getMessage();
        }
    }

    public void publishTonKho(String loaiGiaoDich, String maKho, String maVatTu,
                               String tenVatTu, Integer soLuong, String thoiGian) {
        try {
            String routingKey = "tonkho." + maNguon;
            TonKhoMessage msg = new TonKhoMessage(
                    loaiGiaoDich, maNguon, maKho, maVatTu, tenVatTu, soLuong, thoiGian);
            String json = objectMapper.writeValueAsString(msg);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TON_KHO, routingKey, json);
            System.out.println(">> [RabbitMQ] " + loaiGiaoDich + " | " + maVatTu
                    + " | SL=" + soLuong + " | routing=" + routingKey);
        } catch (Exception e) {
            System.err.println("Lỗi publish RabbitMQ: " + e.getMessage());
        }
    }

    public String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}