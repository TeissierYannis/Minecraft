package fr.game.test;

import fr.game.core.EngineManager;
import fr.game.core.WindowManager;
import fr.game.core.utils.Consts;
import org.lwjgl.Version;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        System.out.println(Version.getVersion());
        window = new WindowManager(Consts.TITLE, 800, 600, true);
        game = TestGame.getInstance();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }

    /**
     * Create a static method that allow other thread to run code on the main thread
     */
    public static void runOnMainThread(Runnable runnable) {
        if (Thread.currentThread() == window.getThread()) {
            runnable.run();
        } else {
            window.runOnMainThread(runnable);
        }
    }
}
