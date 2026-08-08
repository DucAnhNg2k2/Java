package org.example.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tạo Hazelcast member nhúng ngay trong ứng dụng Spring Boot (embedded mode).
 * Cấu hình bằng Java API thay vì hazelcast.xml để chỉ có một nguồn sự thật duy nhất,
 * và để override được qua biến môi trường khi chạy Docker.
 */
@Configuration
@EnableConfigurationProperties(HazelcastProperties.class)
public class HazelcastConfiguration {

    public static final String CACHE_MAP = "cache";

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance(HazelcastProperties properties) {
        Config config = new Config();
        config.setInstanceName(properties.instanceName());
        config.setClusterName(properties.clusterName());
        // Không gửi metrics ẩn danh về Hazelcast
        config.setProperty("hazelcast.phone.home.enabled", "false");

        NetworkConfig network = config.getNetworkConfig();
        network.setPort(properties.port())
                .setPortAutoIncrement(true)
                .setPortCount(20);

        JoinConfig join = network.getJoin();
        // Bắt buộc tắt auto-detection trước khi bật TCP/IP, nếu không Hazelcast sẽ ném
        // InvalidConfigurationException vì có nhiều cơ chế join cùng bật một lúc.
        join.getAutoDetectionConfig().setEnabled(false);
        // Tắt multicast để không vô tình join cluster của máy khác trong cùng mạng LAN
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig()
                .setEnabled(true)
                .setMembers(properties.members());

        // Mỗi entry được giữ thêm 1 bản sao ở node khác, mất 1 node vẫn không mất dữ liệu
        config.getMapConfig(CACHE_MAP).setBackupCount(1);

        return Hazelcast.newHazelcastInstance(config);
    }
}
