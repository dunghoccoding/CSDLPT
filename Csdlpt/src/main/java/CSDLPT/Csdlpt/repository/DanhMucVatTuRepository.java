package CSDLPT.Csdlpt.repository;

import CSDLPT.Csdlpt.Entity.VatTuDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucVatTuRepository extends MongoRepository<VatTuDocument, String> {

}