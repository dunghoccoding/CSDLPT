package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.LichSuGiaoDich;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuGiaoDichRepository extends MongoRepository<LichSuGiaoDich, String> {

    List<LichSuGiaoDich> findByMaNguonOrderByThoiGianDesc(String maNguon);

    List<LichSuGiaoDich> findByLoaiGiaoDichOrderByThoiGianDesc(String loaiGiaoDich);

    List<LichSuGiaoDich> findByMaVatTuOrderByThoiGianDesc(String maVatTu);

    List<LichSuGiaoDich> findByTrangThaiOrderByThoiGianDesc(String trangThai);


    List<LichSuGiaoDich> findByMaNguonAndLoaiGiaoDichOrderByThoiGianDesc(
            String maNguon, String loaiGiaoDich);


    @Query("{ 'ThoiGian': { $gte: ?0, $lte: ?1 } }")
    List<LichSuGiaoDich> findByThoiGianBetween(String tuNgay, String denNgay);

    List<LichSuGiaoDich> findAllByOrderByThoiGianDesc();
}
