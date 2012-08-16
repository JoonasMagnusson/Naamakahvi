package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;


import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class StationSelectTest extends FestSwingJUnitTestCase {
	private DummyCafeUI master;
	private StationSelect station;
	private FrameFixture panel;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				ui.initStations();
				station = new StationSelect(ui, ui.stations);
				ui.add(station);
				return ui;
				
			}
		});
		panel = new FrameFixture(robot(), master);
		panel.show(new Dimension(640, 480));

	}
	
	@Override
	public void onTearDown(){
		master.dispose();
	}
	
	@Test
	public void headerVisible(){
		panel.label(JLabelMatcher.withText("Select Location:")).requireVisible();
	}
	
	@Test
	public void stationsVisible(){
		for (int i = 0; i < 10; i++){
			panel.button(JButtonMatcher.withText("station"+i)).requireVisible();
		}
	}
	
	@Test
	public void stationsSelectable(){
		for (int i = 0; i < 10; i++){
			panel.button(JButtonMatcher.withText("station"+i)).click();
			boolean b = GuiActionRunner.execute(new GuiQuery<Boolean>(){
				protected Boolean executeInEDT(){
					return master.uiCreated;
				}
			});
			assertEquals(true, b);
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					master.resetCreation();
				}
			});
		}
	}

}
