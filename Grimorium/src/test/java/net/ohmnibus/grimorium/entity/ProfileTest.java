package net.ohmnibus.grimorium.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ohmnibus on 02/09/2016.
 */
public class ProfileTest {

	@Test
	public void testSerialize() throws Exception {
		Profile profile = new Profile("Test", new SpellFilter());
		profile.mKey = "TestID";
		profile.getSpellFilter().setType(Spell.TYPE_WIZARDS, false);
		profile.getSpellFilter().setLevel(2, false);

		String serialized = profile.serialize();

		Profile profile2 = Profile.deserialize(serialized);

		assertEquals("First serialization differ from second one", serialized, Profile.serialize(profile2));

//		profile.setStarred(0);
//		profile.setStarred(11);
//		profile.setStarred(1013);
//		profile.setStarred(12433);
		serialized = profile.serialize();
		profile2 = Profile.deserialize(serialized);
		assertEquals("Third serialization differ from fourth one", serialized, Profile.serialize(profile2));
	}
}