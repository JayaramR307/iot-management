package com.thingwire.iot.entity;

import com.thingwire.iot.config.JsonToStringConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonToStringConverter.class)
    private Map<String, Object> metadata;

    public enum Status {
        ONLINE, OFFLINE, ERROR
    }
}
