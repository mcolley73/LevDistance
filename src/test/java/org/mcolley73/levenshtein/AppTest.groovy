package org.mcolley73.levenshtein;

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*;

class AppTest {

	App app;
	
	@Before
	public void setUp() throws Exception {
		app = new App();
	}
	
	@Test
	public void testOpenWordsList(){
		def wordsList = app.openWordsList();
		assertNotNull(wordsList);		
	}

}
