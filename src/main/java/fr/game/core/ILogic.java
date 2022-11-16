package fr.game.core;

public interface ILogic {

    void init() throws Exception;

    void input();

    void update(MouseManager mouseInput);

    void render();

    void cleanup();
}
