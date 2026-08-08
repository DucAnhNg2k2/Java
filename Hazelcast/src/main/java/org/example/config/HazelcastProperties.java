package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Cấu hình Hazelcast của ứng dụng, bind từ prefix {@code app.hazelcast} trong application.yml.
 *
 * @param instanceName tên node, hiển thị trong response để biết request do node nào phục vụ
 * @param clusterName  các node phải cùng cluster-name mới join được với nhau
 * @param port         port Hazelcast dùng để giao tiếp giữa các node (khác với port HTTP)
 * @param members      danh sách địa chỉ các node để discovery qua TCP/IP
 */
@ConfigurationProperties(prefix = "app.hazelcast")
public record HazelcastProperties(
        String instanceName,
        String clusterName,
        int port,
        List<String> members
) {
}
