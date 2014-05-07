package main.engineInterface;

import java.util.ArrayList;

import units.targetable.immoveable.Building;
import units.targetable.moveable.Moveable;
import units.untargetable.passiveInteractive.AOE;
import units.untargetable.passiveInteractive.projectile.Projectile;
import units.untargetable.visualEffect.VisualEffect;

/**
 * Provide thread-safe access to common resources.
 * These resources will be used by both GameEngine and GameGraphics.
 * Therefore they must be protected.
 * @author VDa
 *
 */
public class GameMaster {
	
	/**********************************************************************************/
	/**
	 * Private data holder.
	 * These will be holding data for the whole engine. They will be automatically initialized
	 * at the beginning. There is no need to initialize them
	 */
	
	private static ArrayList<VisualEffect> visualEffects;
	private static ArrayList<Projectile> projectiles;
	private static ArrayList<AOE> aoes;

	private static ArrayList<ArrayList<Building>> buildings;
	private static ArrayList<ArrayList<Moveable>> moveables;
	
	static {
		visualEffects = new ArrayList<VisualEffect>();
		projectiles = new ArrayList<Projectile>();
		aoes = new ArrayList<AOE>();
		
		buildings = new ArrayList<ArrayList<Building>>();
		moveables = new ArrayList<ArrayList<Moveable>>();
		
		for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
			buildings.add(new ArrayList<Building>());
			moveables.add(new ArrayList<Moveable>());
		}
	}

	/**********************************************************************************/
	/**
	 * These are accessors to the data stored in the system. All methods have to be thread-safe.
	 */
	
	
	public static ArrayList<VisualEffect> getVisualEffects() {
		return visualEffects;
	}
	
	public static void addVisualEffects(VisualEffect newComer) {
		visualEffects.add(newComer);
	}
	
	public static ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
	
	public static void addProjectile(Projectile newComer){
		projectiles.add(newComer);
	}
	
	public static ArrayList<AOE> getAOEs() {
		return aoes;
	}
	
	public static void addAOE(AOE newComer) {
		aoes.add(newComer);
	}
	
	
	/**********************************************************************************/
	public static ArrayList<Moveable> getMoveable(int side) {
		return moveables.get(side);
	}
	
	public static void addMoveable(int side, Moveable newComer) {
		moveables.get(side).add(newComer);
	}
	
	public static ArrayList<Building> getBuildings(int side) {
		return buildings.get(side);
	}
	
	public static void addBuilding(int side, Moveable newComer) {
		moveables.get(side).add(newComer);
	}
} 
