package CSDLPT.Csdlpt.service;

import CSDLPT.Csdlpt.Entity.TonKho;
import CSDLPT.Csdlpt.repository.TonKhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Profile({"mien-bac", "mien-nam"})
@Service
public class DongBoService {

    @Value("${app.ma-nguon}")
    private String maNguon;

    @Autowired
    private TonKhoRepository tonKhoRepository;

    @Autowired
    private PhanHeSqlService phanHeSqlService;

    public String dongBoToanBo() {
        List<TonKho> tatCaTonKho = tonKhoRepository.findAll();

        if (tatCaTonKho.isEmpty()) {
            return "[" + maNguon + "] Không có dữ liệu tồn kho để đồng bộ.";
        }

        String thoiGian = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int demSo = 0;

        for (TonKho tk : tatCaTonKho) {
            phanHeSqlService.publishTonKho(
                    "SYNC",
                    tk.getMaKho(),
                    tk.getMaVatTu(),
                    null,
                    tk.getSoLuongTon(),
                    thoiGian
            );
            demSo++;
        }

        return "Đồng bộ thành công! [" + maNguon + "] Đã gửi " + demSo
                + " bản ghi tồn kho lên Trụ Sở.";
    }
}
