package green.tyler.gpstd;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Vector;

/**
 * Created by Tyler Green on 2018-05-11.
 */

public class Manager {
    private Vector<Enemy> enemies;
    private Vector<Tower> towers;
    Manager() {
        enemies = new Vector<>();
        towers = new Vector<>();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void movement() {
      for (Enemy enemy: enemies) {
          enemy.move();
      }
    }
}
