package net.ohmnibus.grimorium.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ohmnibus on 12/10/2016.
 */
public class SpellTest {

	@Test
	public void getLevelsCodeTest() {
		String[] labels;
		String[] testLabels;
		String sep = ",";
		int code;

		labels = new String[] { "C", "2", "10", "Q"};
		code = Spell.getLevelsCode(join(labels, sep));
		testLabels = Spell.getLevelsLabels(code);

		assertArrayEquals(labels, testLabels);

		labels = new String[] { "C", "0", "11", "Q"};
		code = Spell.getLevelsCode(join(labels, sep));
		testLabels = Spell.getLevelsLabels(code);

		assertEquals("C,Q", join(testLabels, sep));
	}

	private String join(String[] values, String separator) {
		if (values == null)
			return null;

		String retVal = "";
		String sep = "";
		for (String v: values) {
			retVal += sep + v;
			sep = separator;
		}

		return retVal;
	}
}