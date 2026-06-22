package com.nsi.gamecatalog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "esrb_fact")
public class EsrbFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false, length = 500)
    private String text;

    public EsrbFact() {}

    public EsrbFact(Game game, String text) {
        this.game = game;
        this.text = text;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
