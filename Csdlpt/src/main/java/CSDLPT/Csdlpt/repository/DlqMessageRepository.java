package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.DlqMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DlqMessageRepository extends MongoRepository<DlqMessage, String> {


    List<DlqMessage> findByTrangThaiOrderByThoiGianLoiDesc(String trangThai);

    List<DlqMessage> findByQueueNguonOrderByThoiGianLoiDesc(String queueNguon);

    long countByTrangThai(String trangThai);
}
