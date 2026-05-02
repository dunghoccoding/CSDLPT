package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.DlqMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DlqMessageRepository extends MongoRepository<DlqMessage, String> {

    /** Lấy tất cả message đang chờ retry */
    List<DlqMessage> findByTrangThaiOrderByThoiGianLoiDesc(String trangThai);

    /** Lấy theo queue gốc */
    List<DlqMessage> findByQueueNguonOrderByThoiGianLoiDesc(String queueNguon);

    /** Đếm số message lỗi đang chờ */
    long countByTrangThai(String trangThai);
}
