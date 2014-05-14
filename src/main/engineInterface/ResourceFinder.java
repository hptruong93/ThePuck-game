package main.engineInterface;

import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Living;
import units.moveable.untargetable.passiveInteractive.AOE;
import units.moveable.untargetable.passiveInteractive.projectile.Projectile;
import units.moveable.untargetable.visualEffect.VisualEffect;

public abstract class ResourceFinder {
	public Unit findAll(int side) {
		try {
			for (Building building : GameMaster.getBuildings(side)) {
				if (findingTest(building)) {
					return building;
				}
			}
		} finally {
			GameMaster.releaseBuilding(side);
		}
		
		try {
			for (Living living : GameMaster.getLivings(side)) {
				if (findingTest(living)) {
					return living;
				}
			}
		} finally {
			GameMaster.releaseLiving(side);
		}
		
		return null;
	}
	
	public Building findBuilding(int side) {
		try {
			for (Building building : GameMaster.getBuildings(side)) {
				if (findingTest(building)) {
					return building;
				}
			}
		} finally {
			GameMaster.releaseBuilding(side);
		}
		return null;
	}
	
	public Living findLiving(int side) {
		try {
			for (Living living : GameMaster.getLivings(side)) {
				if (findingTest(living)) {
					return living;
				}
			}
		} finally {
			GameMaster.releaseLiving(side);
		}
		return null;
	}
	
	public AOE findAOE() {
		try {
			for (AOE aoe : GameMaster.getAOEs()) {
				if (findingTest(aoe)) {
					return aoe;
				}
			}
		} finally {
			GameMaster.releaseAOEs();
		}
		return null;
	}
	
	public VisualEffect findVisualEffect() {
		try {
			for (VisualEffect effect : GameMaster.getVisualEffects()) {
				if (findingTest(effect)) {
					return effect;
				}
			}
		} finally {
			GameMaster.releaseVisualEffect();
		}
		return null;
	}
	
	public Projectile findProjectile() {
		try {
			for (Projectile projectile : GameMaster.getProjectiles()) {
				if (findingTest(projectile)) {
					return projectile;
				}
			}
		} finally {
			GameMaster.releaseProjectiles();
		}
		return null;
	}
	
	protected abstract boolean findingTest(Unit unit);
}
