package com.example.aerospikedemo.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aerospikedemo.model.MyDTO;
import com.example.aerospikedemo.utils.AerospikeUtil;

@RestController
@RequestMapping("/aerospike")
public class AerospikeController {

    @Autowired
    private AerospikeUtil aerospikeUtil;

    @PostMapping("/save")
    public ResponseEntity<String> saveRecord(@RequestBody MyDTO dto, @RequestParam int ttl) {
        aerospikeUtil.addUpdateCache("mySet", dto.getPrimaryKey(), ttl, dto);
        return ResponseEntity.ok("Record saved with TTL");
    }

    @GetMapping("/read")
    public ResponseEntity<?> readRecord(@RequestParam String key) {
        MyDTO dto = aerospikeUtil.getRecord("mySet", key, MyDTO.class);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.ok("No data");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRecord(@RequestBody MyDTO dto, @RequestParam int ttl) {
        aerospikeUtil.addUpdateCache("mySet", dto.getPrimaryKey(), ttl, dto);
        return ResponseEntity.ok("Record updated with TTL");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRecord(@RequestParam String key) {
        boolean deleted = aerospikeUtil.deleteRecord(null, "mySet", key);
        return ResponseEntity.ok(deleted ? "Record deleted" : "No data");
    }

    @PostMapping("/saveBatch")
    public ResponseEntity<String> saveRecords(@RequestBody List<MyDTO> dtos, @RequestParam int ttl) {
        dtos.forEach(dto -> aerospikeUtil.addUpdateCache("mySet", dto.getPrimaryKey(), ttl, dto));
        return ResponseEntity.ok("Records saved with TTL");
    }

    @GetMapping("/readMap")
    public ResponseEntity<?> readRecordAsMap(@RequestParam String key) {
        Map<String, Object> data = aerospikeUtil.getRecord("mySet", key);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.ok("No data");
    }
}
