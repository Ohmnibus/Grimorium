package net.ohmnibus.grimorium.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ohmnibus on 05/08/2016.
 */
public class SpellFilterTest {

	@Test
	public void testSerialize() throws Exception {
		SpellFilter spellFilter = new SpellFilter();
		spellFilter.setType(Spell.TYPE_WIZARDS, false);
		spellFilter.setLevel(2, false);

		String serialized = spellFilter.serialize();

		SpellFilter spellFilter2 = SpellFilter.deserialize(serialized);

		//assertNotEquals("First serialization differ from second one", serialized, spellFilter2);
		assertEquals("First serialization differ from second one", serialized, SpellFilter.serialize(spellFilter2));

//		spellFilter.setStarred(0);
//		spellFilter.setStarred(11);
//		spellFilter.setStarred(1013);
//		spellFilter.setStarred(12433);
		serialized = spellFilter.serialize();
		spellFilter2 = SpellFilter.deserialize(serialized);
		assertEquals("Third serialization differ from fourth one", serialized, SpellFilter.serialize(spellFilter2));
	}
}