package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.TonKhoMien;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TonKhoMienRepository extends MongoRepository<TonKhoMien, String> {
    List<TonKhoMien> findByMaNguon(String maNguon);
    List<TonKhoMien> findByMaVatTu(String maVatTu);
}
