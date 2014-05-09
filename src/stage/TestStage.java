package stage;

import java.util.ArrayList;

import units.moveable.targetable.livings.Living;
import units.moveable.targetable.livings.boss.Archon;

public class TestStage extends Stage {

	@Override
	protected ArrayList<Living> generateCreeps() {
		ArrayList<Living> output = new ArrayList<Living>();
		
		output.add(new Archon());
		
		return output;
	}

}
