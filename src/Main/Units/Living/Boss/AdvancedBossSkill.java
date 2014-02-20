package Main.Units.Living.Boss;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

//Has cooldown and target
public abstract class AdvancedBossSkill extends BossSkill {
    private int coolDown; //In milliseconds
    private LivingUnit target;
    private boolean activate;

    public AdvancedBossSkill() {
    }

    public AdvancedBossSkill(Game game, LivingUnit target) {//Remember to set coolDown
        super(game);
        this.target = target;
    }

    @Override
    protected abstract void applyEffect(LivingUnit affectedUnit, boolean touched);//Skill effect

    public void setActivate(boolean activate, boolean forcedAdjust) {
        this.activate = activate;
    }

    public boolean activate() {
        return activate;
    }

    //Getter and Setter
    @Override
    public boolean available() {
        return !isDisable() && (Clocks.masterClock.currentTime() - startTime() > coolDown);
    }

    protected void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    protected LivingUnit target() {
        return target;
    }

    public void setTarget(LivingUnit target) {
        this.target = target;
    }
}
