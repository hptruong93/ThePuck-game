package main.engineInterface;

import java.util.HashSet;

import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Living;
import units.moveable.untargetable.passiveInteractive.AOE;
import units.moveable.untargetable.passiveInteractive.projectile.Projectile;
import units.moveable.untargetable.visualEffect.VisualEffect;

public abstract class ResourceFilter {
	public HashSet<Unit> filterAll(int side) {
		HashSet<Unit> output = new HashSet<Unit>();
		
		try {
			for (Building building : GameMaster.getBuildings(side)) {
				if (filteringTest(building)) {
					output.add(building);
				}
			}
		} finally {
			GameMaster.releaseBuilding(side);
		}
		
		try {
			for (Living living : GameMaster.getLivings(side)) {
				if (filteringTest(living)) {
					output.add(living);
				}
			}
		} finally {
			GameMaster.releaseLiving(side);
		}
		
		return output;
	}
	
	public HashSet<Building> filterBuilding(int side) {
		HashSet<Building> output = new HashSet<Building>();
		try {
			for (Building building : GameMaster.getBuildings(side)) {
				if (filteringTest(building)) {
					output.add(building);
				}
			}
		} finally {
			GameMaster.releaseBuilding(side);
		}
		return output;
	}
	
	public HashSet<Living> filterLiving(int side) {
		HashSet<Living> output = new HashSet<Living>();
		
		try {
			for (Living living : GameMaster.getLivings(side)) {
				if (filteringTest(living)) {
					output.add(living);
				}
			}
		} finally {
			GameMaster.releaseLiving(side);
		}
		return output;
	}
	
	public HashSet<AOE> filterAOE() {
		HashSet<AOE> output = new HashSet<AOE>();
		
		try {
			for (AOE aoe : GameMaster.getAOEs()) {
				if (filteringTest(aoe)) {
					output.add(aoe);
				}
			}
		} finally {
			GameMaster.releaseAOEs();
		}
		return output;
	}
	
	public HashSet<VisualEffect> filterVisualEffect() {
		HashSet<VisualEffect> output = new HashSet<VisualEffect>();
		
		try {
			for (VisualEffect visualEffect : GameMaster.getVisualEffects()) {
				if (filteringTest(visualEffect)) {
					output.add(visualEffect);
				}
			}
		} finally {
			GameMaster.releaseVisualEffect();
		}
		return output;
	}
	
	public HashSet<Projectile> filterProjectile() {
		HashSet<Projectile> output = new HashSet<Projectile>();
		
		try {
			for (Projectile projectile : GameMaster.getProjectiles()) {
				if (filteringTest(projectile)) {
					output.add(projectile);
				}
			}
		} finally {
			GameMaster.releaseProjectiles();
		}
		return output;
	}
	
	protected abstract boolean filteringTest(Unit unit);
}
