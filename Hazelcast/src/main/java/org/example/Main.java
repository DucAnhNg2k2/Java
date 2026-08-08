package org.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Demo Hazelcast embedded member: khởi động 1 node ngay trong JVM của ứng dụng.
 * Cấu hình cluster được đọc từ src/main/resources/hazelcast.xml.
 */
public class Main {
    public static void main(String[] args) {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        try {
            // Distributed Map
            IMap<String, String> users = hz.getMap("users");
            users.put("1", "Duc Anh");
            users.put("2", "Hazelcast");
            users.put("3", "Gradle", 30, TimeUnit.SECONDS); // entry tự hết hạn sau 30s

            System.out.println("Cluster name : " + hz.getConfig().getClusterName());
            System.out.println("Members      : " + hz.getCluster().getMembers());
            System.out.println("users.size() : " + users.size());
            for (Map.Entry<String, String> e : users.entrySet()) {
                System.out.println("  " + e.getKey() + " -> " + e.getValue());
            }
        } finally {
            hz.shutdown();
        }
    }
}
