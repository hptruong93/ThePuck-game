package Main.Items;

import Buffs.Curse;
import Main.Units.Living.LivingUnit;
import Features.Clocks;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public abstract class Item implements Runnable {

   private String name;
   private int timerID;
   private ArrayList<Curse> effects;
   private LivingUnit holder;
   private boolean dropable;
   private boolean transferable;

   public Item(String name) {
      this.name = name;
      effects = new ArrayList<>();
   }

   protected abstract void schedule();

   protected abstract void applyEffects(LivingUnit target);

   public void setHolder(LivingUnit holder) {
      this.holder = holder;
   }

   protected ArrayList<Curse> effects() {
      return effects;
   }
}
