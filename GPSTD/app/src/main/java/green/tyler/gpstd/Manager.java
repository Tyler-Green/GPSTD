package green.tyler.gpstd;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class Manager {
    private Vector<Enemy> enemies;
    private Vector<Tower> towers;
    private Timer timer;
    private TimerTask timerTask;
    private Handler movementHandler;

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }
    Manager(Handler _MOVEMENTHANDLER) {
        movementHandler = _MOVEMENTHANDLER;
        enemies = new Vector<>();
        towers = new Vector<>();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                movementHandler.sendEmptyMessage(1);
            }
        };
        start();
    }

    public void addTower(Tower tower) {
        towers.add(tower);
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
