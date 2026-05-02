package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.dto.VatTuRequest;
import CSDLPT.Csdlpt.Entity.VatTuDocument;
import CSDLPT.Csdlpt.repository.DanhMucVatTuRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"mien-bac", "mien-nam"})
@Service
public class PhanHeMongoService {

    @Autowired
    private DanhMucVatTuRepository danhMucRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "Q_DongBoVatTu")
    public void nhanThuTuBuuDien(String jsonMessage) {
        try {
            System.out.println("<< [RabbitMQ] Nhận được thư: " + jsonMessage);

            // 1. Dịch ngược từ chuỗi JSON về lại cái hộp VatTuRequest
            VatTuRequest req = objectMapper.readValue(jsonMessage, VatTuRequest.class);

            // 2. Chuyển thông tin từ hộp sang định dạng Document của Mongo
            VatTuDocument document = new VatTuDocument(
                    req.getMaVatTu(),
                    req.getTenVatTu(),
                    req.getLoai(),
                    req.getThongSoKyThuat()
            );

            // 3. Lưu vào MongoDB
            danhMucRepository.save(document);
            System.out.println("<< [MongoDB] Đã cập nhật xong hồ sơ chi tiết cho " + req.getMaVatTu());

        } catch (Exception e) {
            System.out.println("Lỗi khi xử lý thư: " + e.getMessage());
            e.printStackTrace();
        }
    }
}