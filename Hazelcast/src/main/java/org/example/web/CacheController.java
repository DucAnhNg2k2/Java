package org.example.web;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.example.config.HazelcastConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

/**
 * CRUD tối giản trên một distributed map của Hazelcast.
 * <p>
 * Mọi response đều kèm {@code servedBy} là tên node đã xử lý request — dùng để thấy rõ
 * dữ liệu ghi ở node này đọc được từ node kia.
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final String nodeName;
    private final IMap<String, String> cache;

    public CacheController(HazelcastInstance hazelcast) {
        this.nodeName = hazelcast.getName();
        this.cache = hazelcast.getMap(HazelcastConfiguration.CACHE_MAP);
    }

    /** Ghi một cặp key/value, value nằm trong body dạng text thuần. */
    @PutMapping("/{key}")
    public Entry put(@PathVariable String key, @RequestBody String value) {
        cache.put(key, value);
        return new Entry(nodeName, key, value);
    }

    /** Ghi bằng query param: {@code PUT /api/cache?key=ducanh&value=235} — tiện khi test nhanh. */
    @PutMapping
    public Entry putByQueryParam(@RequestParam String key, @RequestParam String value) {
        cache.put(key, value);
        return new Entry(nodeName, key, value);
    }

    /** Đọc value theo key, trả 404 nếu không có. */
    @GetMapping("/{key}")
    public ResponseEntity<Entry> get(@PathVariable String key) {
        String value = cache.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Entry(nodeName, key, value));
    }

    /** Liệt kê toàn bộ entry đang có trong cluster. */
    @GetMapping
    public Listing list() {
        // Chỉ hợp lý với dữ liệu demo cỡ nhỏ: entrySet() kéo toàn bộ map về node này.
        Map<String, String> entries = new TreeMap<>(cache);
        return new Listing(nodeName, entries.size(), entries);
    }

    /** Xoá một key. */
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        cache.delete(key);
        return ResponseEntity.noContent().build();
    }

    public record Entry(String servedBy, String key, String value) {
    }

    public record Listing(String servedBy, int size, Map<String, String> entries) {
    }
}
