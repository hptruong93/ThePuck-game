package main.engineInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import units.immoveable.Building;
import units.moveable.livings.Living;
import units.moveable.untargetable.passiveInteractive.AOE;
import units.moveable.untargetable.passiveInteractive.projectile.Projectile;
import units.moveable.untargetable.visualEffect.VisualEffect;

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
	
	private static final HashSet<VisualEffect> visualEffects;
	private static final ReentrantReadWriteLock visualEffectsLock; 
	
	private static final HashSet<Projectile> projectiles;
	private static final ReentrantReadWriteLock projectilesLock;
	
	private static final HashSet<AOE> aoes;
	private static final ReentrantReadWriteLock aoesLock;

	private static final ArrayList<HashSet<Building>> buildings;
	private static final ArrayList<ReentrantReadWriteLock> buildingsLocks;
	
	private static final ArrayList<HashSet<Living>> livings;
	private static final ArrayList<ReentrantReadWriteLock> livingsLock; 
	
	static {
		visualEffects = new HashSet<VisualEffect>();
		visualEffectsLock = new ReentrantReadWriteLock();
		
		projectiles = new HashSet<Projectile>();
		projectilesLock = new ReentrantReadWriteLock();
		
		aoes = new HashSet<AOE>();
		aoesLock = new ReentrantReadWriteLock();
		
		buildings = new ArrayList<HashSet<Building>>();
		buildingsLocks = new ArrayList<ReentrantReadWriteLock>();
		
		livings = new ArrayList<HashSet<Living>>();
		livingsLock = new ArrayList<ReentrantReadWriteLock>();
		
		for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
			buildings.add(new HashSet<Building>());
			buildingsLocks.add(new ReentrantReadWriteLock());
			
			livings.add(new HashSet<Living>());
			livingsLock.add(new ReentrantReadWriteLock());
		}
	}

	/**********************************************************************************/
	/**
	 * These are accessors to the data stored in the system. All methods have to be thread-safe.
	 * Each resource will be protected by a ReentrantReadWriteLock.
	 * 
	 * Write lock for writing process is already implemented. Client does not have to take
	 * care of locks when write. The provided interface will cover it.
	 * 
	 * Remove lock for removing process is also implemented. Usage is similar to writing process.
	 * 
	 * Read process is more complicated and should be used with care. Procedure of reading a resource
	 * is presented below:
	 *     Resource resource = GameMaster.getResource();
	 *     try {
	 *         --> Using resource here
	 *     } finally {
	 *         releaseResource();
	 *     }
	 * This is to ensure that 
	 */
	/************Visual Effects*****************/
	
	public static HashSet<VisualEffect> getVisualEffects() {
		visualEffectsLock.readLock().lock();
		return visualEffects;
	}
	
	public static void releaseVisualEffect() {
		visualEffectsLock.readLock().unlock();
	}
	
	public static void addVisualEffects(VisualEffect newComer) {
		visualEffectsLock.writeLock().lock();
		try {
			visualEffects.add(newComer);
		} finally {
			visualEffectsLock.writeLock().unlock();
		}
	}
	
	public static void removeVisualEffect(VisualEffect toBeRemoved) {
		visualEffectsLock.writeLock().lock();
		try {
			visualEffects.remove(toBeRemoved);
		} finally {
			visualEffectsLock.writeLock().unlock();	
		}
	}
	
	/**************Projectiles******************/
	
	public static HashSet<Projectile> getProjectiles() {
		projectilesLock.readLock().lock();
		return projectiles;
	}
	
	public static void releaseProjectiles() {
		projectilesLock.readLock().unlock();
	}
	
	public static void addProjectile(Projectile newComer){
		projectilesLock.writeLock().lock();
		try {
			projectiles.add(newComer);
		} finally {
			projectilesLock.writeLock().unlock();
		}
	}

	public static void removeProjectile(Projectile toBeRemoved) {
		projectilesLock.writeLock().lock();
		try {
			projectiles.remove(toBeRemoved);
		} finally {
			projectilesLock.writeLock().unlock();	
		}
	}
	
	/**************AOEs*************************/
	
	public static HashSet<AOE> getAOEs() {
		aoesLock.readLock().lock();
		return aoes;
	}
	
	public static void releaseAOEs() {
		aoesLock.readLock().unlock();
	}
	
	public static void addAOE(AOE newComer) {
		aoesLock.writeLock().lock();
		try {
			aoes.add(newComer);
		} finally {
			aoesLock.writeLock().unlock();
		}
	}
	
	public static void removeAOE(AOE toBeRemoved) {
		aoesLock.writeLock().lock();
		try {
			aoes.remove(toBeRemoved);
		} finally {
			aoesLock.writeLock().unlock();	
		}
	}
	
	/**********************************************************************************/
	/**************Livings**********************/
	
	public static HashSet<Living> getLivings(int side) {
		livingsLock.get(side).readLock().lock();
		return livings.get(side);
	}
	
	public static void releaseLiving(int side) {
		livingsLock.get(side).readLock().unlock();
	}
	
	public static void addLiving(int side, Living newComer) {
		livingsLock.get(side).writeLock().lock();
		try {
			livings.get(side).add(newComer);
		} finally {
			livingsLock.get(side).writeLock().unlock();
		}
	}
	
	public static void removeLiving(int side, Living toBeRemoved) {
		livingsLock.get(side).writeLock().lock();
		try {
			livings.get(side).remove(toBeRemoved);
		} finally {
			livingsLock.get(side).writeLock().unlock();	
		}
	}
	
	/**************Buidlings********************/
	
	public static HashSet<Building> getBuildings(int side) {
		buildingsLocks.get(side).readLock().lock();
		return buildings.get(side);
	}
	
	public static void releaseBuilding(int side) {
		buildingsLocks.get(side).readLock().unlock();
	}
	
	public static void addBuilding(int side, Building newComer) {
		buildingsLocks.get(side).writeLock().lock();
		try {
			buildings.get(side).add(newComer);
		} finally {
			buildingsLocks.get(side).writeLock().unlock();
		}
	}
	
	public static void removeBuilding(int side, Building toBeRemoved) {
		buildingsLocks.get(side).writeLock().lock();
		try {
			buildings.get(side).remove(toBeRemoved);
		} finally {
			buildingsLocks.get(side).writeLock().unlock();	
		}
	}
} 
