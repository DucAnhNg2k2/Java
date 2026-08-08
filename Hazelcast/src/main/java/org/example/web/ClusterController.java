package org.example.web;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Xem trạng thái cluster: node nào đang trả lời và có bao nhiêu node đã join. */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    private final HazelcastInstance hazelcast;

    public ClusterController(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    @GetMapping
    public ClusterInfo info() {
        List<String> members = hazelcast.getCluster().getMembers().stream()
                .map(Member::getAddress)
                .map(Object::toString)
                .toList();
        return new ClusterInfo(hazelcast.getName(), members.size(), members);
    }

    public record ClusterInfo(String servedBy, int size, List<String> members) {
    }
}
