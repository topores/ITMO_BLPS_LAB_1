package com.rostislavdavydov.blps.lab1.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Table(name = "ARTICLES")
@Entity
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "topic")
    private String topic;

    @Column(name = "text")
    private String text;

    @Column(name = "state")
    private String state;

    public Article setUser(User user) {
        this.user = user;
        return this;
    }

    public Article setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Article setText(String text) {
        this.text = text;
        return this;
    }

    public Article setState(String state) {
        this.state = state;
        return this;
    }
}
