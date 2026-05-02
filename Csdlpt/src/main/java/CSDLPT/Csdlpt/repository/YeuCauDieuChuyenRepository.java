package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.YeuCauDieuChuyen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YeuCauDieuChuyenRepository extends MongoRepository<YeuCauDieuChuyen, String> {
    List<YeuCauDieuChuyen> findByMaNguonXin(String maNguonXin);
    List<YeuCauDieuChuyen> findByMaNguonCho(String maNguonCho);
}
