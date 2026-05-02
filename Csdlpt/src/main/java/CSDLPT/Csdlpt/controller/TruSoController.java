package CSDLPT.Csdlpt.controller;

import CSDLPT.Csdlpt.Entity.TonKhoMien;
import CSDLPT.Csdlpt.Entity.TonKhoReplicaMienBac;
import CSDLPT.Csdlpt.Entity.TonKhoReplicaMienNam;
import CSDLPT.Csdlpt.repository.TonKhoMienRepository;
import CSDLPT.Csdlpt.repository.TonKhoReplicaMienBacRepository;
import CSDLPT.Csdlpt.repository.TonKhoReplicaMienNamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Profile("tru-so")
@RestController
@RequestMapping("/api/tong-hop")
public class TruSoController {

    @Autowired
    private TonKhoReplicaMienBacRepository replicaBacRepo;

    @Autowired
    private TonKhoReplicaMienNamRepository replicaNamRepo;

    @Autowired
    private TonKhoMienRepository tonKhoMienRepository;

    @GetMapping("/ton-kho/mien-bac")
    public ResponseEntity<List<TonKhoReplicaMienBac>> xemMienBac() {
        return ResponseEntity.ok(replicaBacRepo.findAll());
    }

    @GetMapping("/ton-kho/mien-nam")
    public ResponseEntity<List<TonKhoReplicaMienNam>> xemMienNam() {
        return ResponseEntity.ok(replicaNamRepo.findAll());
    }

    @GetMapping("/ton-kho/mongo")
    public ResponseEntity<List<TonKhoMien>> xemQuaMongo() {
        return ResponseEntity.ok(tonKhoMienRepository.findAll());
    }

    @GetMapping("/ton-kho/mongo/{maNguon}")
    public ResponseEntity<List<TonKhoMien>> xemMongoTheoMien(@PathVariable String maNguon) {
        return ResponseEntity.ok(tonKhoMienRepository.findByMaNguon(maNguon));
    }

    @GetMapping("/ton-kho/tim-kiem")
    public ResponseEntity<Map<String, Object>> timKiemTheoVatTu(
            @RequestParam String maVatTu) {

        List<TonKhoReplicaMienBac> bacList = replicaBacRepo.findAll().stream()
                .filter(t -> maVatTu.equalsIgnoreCase(t.getMaVatTu()))
                .toList();

        List<TonKhoReplicaMienNam> namList = replicaNamRepo.findAll().stream()
                .filter(t -> maVatTu.equalsIgnoreCase(t.getMaVatTu()))
                .toList();

        List<TonKhoMien> mongoList = tonKhoMienRepository.findByMaVatTu(maVatTu);

        int tongMienBac = bacList.stream().mapToInt(TonKhoReplicaMienBac::getSoLuongTon).sum();
        int tongMienNam = namList.stream().mapToInt(TonKhoReplicaMienNam::getSoLuongTon).sum();
        int tongToanQuoc = tongMienBac + tongMienNam;

        Map<String, Object> ketQua = new HashMap<>();
        ketQua.put("maVatTu",      maVatTu);
        ketQua.put("tongToanQuoc", tongToanQuoc);
        ketQua.put("tongMienBac",  tongMienBac);
        ketQua.put("tongMienNam",  tongMienNam);
        ketQua.put("chiTietMienBac_SQL",  bacList);
        ketQua.put("chiTietMienNam_SQL",  namList);
        ketQua.put("chiTietMongo",        mongoList);

        return ResponseEntity.ok(ketQua);
    }
    @GetMapping("/ton-kho/tong-quan")
    public ResponseEntity<Map<String, Object>> tongQuan() {
        int tongBac = replicaBacRepo.findAll().stream()
                .mapToInt(TonKhoReplicaMienBac::getSoLuongTon).sum();
        int tongNam = replicaNamRepo.findAll().stream()
                .mapToInt(TonKhoReplicaMienNam::getSoLuongTon).sum();

        Map<String, Object> result = new HashMap<>();
        result.put("tongMienBac",  tongBac);
        result.put("tongMienNam",  tongNam);
        result.put("tongToanQuoc", tongBac + tongNam);
        result.put("soLoaiVatTuMienBac", replicaBacRepo.count());
        result.put("soLoaiVatTuMienNam", replicaNamRepo.count());

        return ResponseEntity.ok(result);
    }
}
