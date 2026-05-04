package CSDLPT.Csdlpt.Entity;

import java.io.Serializable;
import java.util.Objects;


public class TonKhoID implements Serializable {
    private  String maKho;
    private String maVatTu;
    public TonKhoID() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TonKhoID tonKhoId = (TonKhoID) o;
        return Objects.equals(maKho, tonKhoId.maKho) && Objects.equals(maVatTu, tonKhoId.maVatTu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKho, maVatTu);
    }

    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public String getMaVatTu() {
        return maVatTu;
    }

    public void setMaVatTu(String maVatTu) {
        this.maVatTu = maVatTu;
    }
}