package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Map;

@Document(collection = "DanhMucVatTu")
public class VatTuDocument {

    @Id
    private String id;

    @Field("TenVatTu")
    private String tenVatTu;

    @Field("Loai")
    private String loai;


    @Field("ThongSoKyThuat")
    private Map<String, Object> thongSoKyThuat;


    public VatTuDocument() {}

    public VatTuDocument(String id, String tenVatTu, String loai, Map<String, Object> thongSoKyThuat) {
        this.id = id;
        this.tenVatTu = tenVatTu;
        this.loai = loai;
        this.thongSoKyThuat = thongSoKyThuat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenVatTu() {
        return tenVatTu;
    }

    public void setTenVatTu(String tenVatTu) {
        this.tenVatTu = tenVatTu;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public Map<String, Object> getThongSoKyThuat() {
        return thongSoKyThuat;
    }

    public void setThongSoKyThuat(Map<String, Object> thongSoKyThuat) {
        this.thongSoKyThuat = thongSoKyThuat;
    }
}