package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumMap;

public class GameKeyManager implements KeyListener {
    private final EnumMap<GameKey, Boolean> keys = new EnumMap<>(GameKey.class);
    private final EnumMap<GameKey, Boolean> justPressed = new EnumMap<>(GameKey.class);
    private final EnumMap<GameKey, Boolean> cantPress = new EnumMap<>(GameKey.class);

    public GameKeyManager() {
        for (GameKey key : GameKey.values()) {
            keys.put(key, false);
            justPressed.put(key, false);
            cantPress.put(key, false);
        }
    }

    private GameKey convertToGameKey(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP -> GameKey.UP;
            case KeyEvent.VK_DOWN -> GameKey.DOWN;
            case KeyEvent.VK_RIGHT -> GameKey.RIGHT;
            case KeyEvent.VK_LEFT -> GameKey.LEFT;
            case KeyEvent.VK_ENTER -> GameKey.ENTER;
            case KeyEvent.VK_ESCAPE -> GameKey.ESC;
            default -> null;
        };
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Optional implementation
    }

    @Override
    public void keyPressed(KeyEvent e) {
        GameKey gameKey = convertToGameKey(e.getKeyCode());
        if (gameKey != null) {
            keys.put(gameKey, true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        GameKey gameKey = convertToGameKey(e.getKeyCode());
        if (gameKey != null) {
            keys.put(gameKey, false);
            cantPress.put(gameKey, false);
        }
    }

    public void update() {
        for (GameKey key : GameKey.values()) {
            if (cantPress.get(key) && !keys.get(key)) {
                cantPress.put(key, false);
            } else if (justPressed.get(key)) {
                cantPress.put(key, true);
                justPressed.put(key, false);
            }
            if (!cantPress.get(key) && keys.get(key)) {
                justPressed.put(key, true);
            }
        }
    }

    public boolean isKeyPressed(GameKey key) {
        return keys.get(key);
    }

    public boolean isKeyJustPressed(GameKey key){
        return justPressed.get(key);
    }


}

