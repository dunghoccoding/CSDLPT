package CSDLPT.Csdlpt.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "TonKhoMien")
public class TonKhoMien {

    @Id
    private String id;

    @Field("MaNguon")
    private String maNguon;

    @Field("MaKho")
    private String maKho;

    @Field("MaVatTu")
    private String maVatTu;

    @Field("SoLuongTon")
    private Integer soLuongTon;

    @Field("CapNhatLuc")
    private String capNhatLuc;

    public TonKhoMien() {}

    public TonKhoMien(String id, String maNguon, String maKho,
                      String maVatTu, Integer soLuongTon, String capNhatLuc) {
        this.id = id;
        this.maNguon = maNguon;
        this.maKho = maKho;
        this.maVatTu = maVatTu;
        this.soLuongTon = soLuongTon;
        this.capNhatLuc = capNhatLuc;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMaNguon() { return maNguon; }
    public void setMaNguon(String maNguon) { this.maNguon = maNguon; }
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(String capNhatLuc) { this.capNhatLuc = capNhatLuc; }
}
