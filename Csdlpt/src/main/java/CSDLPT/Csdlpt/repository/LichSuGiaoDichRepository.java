package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuGiaoDichRepository extends MongoRepository<LichSuGiaoDich, String> {

    /** Lọc theo nguồn giao dịch (MIEN_BAC / MIEN_NAM / TRU_SO) */
    List<LichSuGiaoDich> findByMaNguonOrderByThoiGianDesc(String maNguon);

    /** Lọc theo loại giao dịch (NHAP / XUAT / DIEU_CHUYEN_XUAT / DIEU_CHUYEN_NHAP / SYNC) */
    List<LichSuGiaoDich> findByLoaiGiaoDichOrderByThoiGianDesc(String loaiGiaoDich);

    /** Lọc theo mã vật tư */
    List<LichSuGiaoDich> findByMaVatTuOrderByThoiGianDesc(String maVatTu);

    /** Lọc theo trạng thái (THANH_CONG / THAT_BAI) */
    List<LichSuGiaoDich> findByTrangThaiOrderByThoiGianDesc(String trangThai);

    /** Kết hợp: lọc theo nguồn + loại giao dịch */
    List<LichSuGiaoDich> findByMaNguonAndLoaiGiaoDichOrderByThoiGianDesc(
            String maNguon, String loaiGiaoDich);

    /** Lọc theo khoảng thời gian (yyyyMMdd HH:mm:ss string compare) */
    @Query("{ 'ThoiGian': { $gte: ?0, $lte: ?1 } }")
    List<LichSuGiaoDich> findByThoiGianBetween(String tuNgay, String denNgay);

    /** Toàn bộ theo thứ tự mới nhất */
    List<LichSuGiaoDich> findAllByOrderByThoiGianDesc();
}
