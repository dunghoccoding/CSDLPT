package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.TonKhoID;
import CSDLPT.Csdlpt.Entity.TonKhoReplicaMienBac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TonKhoReplicaMienBacRepository
        extends JpaRepository<TonKhoReplicaMienBac, TonKhoID> {
}
