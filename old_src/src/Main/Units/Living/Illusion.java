package Main.Units.Living;

import Main.Units.Units;
import Utilities.Pointt;

public abstract class Illusion extends LivingUnit {

   private LivingUnit original;
   private double healthScale;

   public Illusion(LivingUnit original, Pointt position, double movingAngle, double healthScale, double damageScale) {
      super(position, movingAngle, original.maxHealth(), original.side());
      this.game = original.game;
      this.original = original;
      this.healthScale = 1 / healthScale;

      setName(original.name());
      setRange(original.range());
      setRadius(original.radius());
      setAttackSpeed(original.attackSpeed());
      setAngularSpeed(original.angularSpeed());
      setSpeed(original.speed());
      setBat(original.bat());
      setColor(original.color());

      setMaxHealth(original.maxHealth());
      setHealth(original.health(), Units.FORCE_CHANGE);
      setDamage(original.damage() * damageScale);

      partColors = original.partColors();
   }

   @Override
   public void die(double damagingSpeed) {
      clearTasks();
      super.die(damagingSpeed);
   }

   @Override
   public final void setHealth(double health, int damageType) {
      if (damageType != FORCE_CHANGE) {
         double difference = health() - health;
         if (difference > 0) {
            super.setHealth(health() - healthScale * difference, damageType);
         } else {
            super.setHealth(health, damageType);
         }
      } else {
         super.setHealth(health, damageType);
      }
   }

   protected LivingUnit original() {
      return original;
   }
}
