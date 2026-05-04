package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.TonKhoTruSo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TonKhoTruSoRepository extends JpaRepository<TonKhoTruSo, String> {
    List<TonKhoTruSo> findByMaNguon(String maNguon);
    List<TonKhoTruSo> findByMaVatTu(String maVatTu);
}
