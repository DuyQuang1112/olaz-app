package com.myproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_join_form")
public class JoinForm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receive_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "status")
    String status;

    @Column(name = "timestamp")
    LocalDateTime timestamp;

}
