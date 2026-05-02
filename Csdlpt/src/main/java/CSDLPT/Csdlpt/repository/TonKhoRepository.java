package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.TonKho;
import CSDLPT.Csdlpt.Entity.TonKhoID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, TonKhoID> {
    Optional<TonKho> findByMaKhoAndMaVatTu(String maKho, String maVatTu);
}
