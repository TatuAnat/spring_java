package com.example.demo.entity;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name = "ACTIVE_CHAT")
public class ActiveChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CHAT_ID")
    private Long chatId;

    public ActiveChat setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }
}