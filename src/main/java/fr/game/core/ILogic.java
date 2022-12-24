package fr.game.core;

public interface ILogic {

    void init() throws Exception;

    void input() throws Exception;

    void update(MouseManager mouseInput) throws Exception;

    void render();

    void cleanup();
}
