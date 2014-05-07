package stage;

import java.util.ArrayList;

import units.targetable.moveable.Living;
import units.targetable.moveable.livings.boss.Archon;

public class TestStage extends Stage {

	@Override
	protected ArrayList<Living> generateCreeps() {
		ArrayList<Living> output = new ArrayList<Living>();
		
		output.add(new Archon());
		
		return output;
	}

}
