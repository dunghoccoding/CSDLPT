package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.TonKhoID;
import CSDLPT.Csdlpt.Entity.TonKhoReplicaMienNam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TonKhoReplicaMienNamRepository
        extends JpaRepository<TonKhoReplicaMienNam, TonKhoID> {
}
