package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.eng1group9.Main;
import io.github.eng1group9.systems.RenderingSystem;

import java.util.Random;

public class Boss extends MovingEntity {

    private float timeLeft = 1000f;
    private boolean defeated;
    final private float attackCooldownLength;
    private float attackCooldown;
    private final int NUMOFATTACKS = 8;
    private final Random random;
    private final int spacing;

    public Boss(Vector2 startPos, float attackCooldownLength, int spacing) {
        super(new Texture("Characters/bossAnimations.png"), new int[]{1}, 64, 80, 0f, startPos, new Vector2(0,0), false, true);
        setScale(2);
        System.out.println(timeLeft);
        defeated = false;
        random = new Random();
        this.attackCooldownLength = attackCooldownLength;
        attackCooldown = attackCooldownLength;
        this.spacing = spacing;
    }

    /**
     * Spawns the boss in the room and starts the boss fight
     * @param fightLength
     */
    public void start(float fightLength){
        System.out.println("Began boss fight!");
        defeated = false;
        timeLeft = fightLength;
        activate();
    }

    /**
     * Logic for the boss, should be called in the main logic loop.
     */
    public void logic(){
        if(isActive()){
            float delta = Gdx.graphics.getDeltaTime();
            timeLeft -= delta;
            if(timeLeft < 0){
                defeated = true;
                Main.defeatBoss();
                deactivate();
            }
            if(!defeated){
                nextAttack();
            }
        }
    }

    /**
     * Uses a random attack once the cooldown has expired.
     */
    public void nextAttack() {
        float delta = Gdx.graphics.getDeltaTime();
        attackCooldown -= delta;

        if (attackCooldown <= 0) {
            int attack = random.nextInt(NUMOFATTACKS);

            int width = (int) RenderingSystem.getViewportWidth() * 2;
            int height = (int) RenderingSystem.getViewportHeight() * 2;

            switch (attack) {
                case 0:
                    spawnProjectiles('L', 0, height, spacing);
                    break;
                case 1:
                    spawnProjectiles('R', -spacing, height+spacing, spacing);
                    break;
                case 2:
                    spawnProjectiles('U', 0, width, spacing);
                    break;
                case 3:
                    spawnProjectiles('D', -spacing, width+spacing, spacing);
                    break;

                case 4:
                    spawnProjectiles('L', 0, height / 2, spacing/2);
                    break;
                case 5:
                    spawnProjectiles('R', 0, height / 2, spacing/2);
                    break;
                case 6:
                    spawnProjectiles('U', 0, width / 2, spacing/2);
                    break;
                case 7:
                    spawnProjectiles('D', 0, width / 2, spacing/2);
                    break;
            }
            attackCooldown = attackCooldownLength;
        }
    }

    /**
     * Spawns projectiles across a fixed area in a certain direction.
     *
     * @param moveDirection The direction that the projectile will travel.
     *                      'U' for up, 'D' for down, 'L' for left, 'R' for right.
     *                      Projectiles travelling up or down will be spawned along the X axis.
     *                      Projectiles travelling left or right will be spawned along the Y axis.
     * @param from The starting coordinate for where the projectiles should be spawned
     * @param to The last coordinate for where the projectiles should be spawned/
     * @param spacing How many pixels there should be between each projectile.
     */
    private void spawnProjectiles(Character moveDirection,int from, int to,int spacing){

        int width = (int)RenderingSystem.getViewportWidth()*2;
        int height = (int)RenderingSystem.getViewportHeight()*2;

        System.out.println("Spawning projectiles from " +  from + " to " + to);

        for(int i = from; i <= to; i = i + spacing){
            switch(moveDirection){
                case 'L':
                    Main.spawnProjectile(new Vector2(width,i), moveDirection);
                    break;
                case 'R':
                    Main.spawnProjectile(new Vector2(0,i), moveDirection);
                    break;
                case 'U':
                    Main.spawnProjectile(new Vector2(i,0), moveDirection);
                    break;
                case 'D':
                    Main.spawnProjectile(new Vector2(i,height), moveDirection);
                    break;
            }
        }
    }

    public boolean isDefeated(){
        return defeated;
    }

    public float getTimeLeft(){
        return timeLeft;
    }

    @Override
    public void reset(){
        super.reset();

    }

}
