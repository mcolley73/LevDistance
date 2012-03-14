package org.mcolley73.levenshtein;

import static org.junit.Assert.*;

import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

class SocialNetworkTest {

	SocialNetwork socialNetwork;
	
	@Before
	public void setUp(){
		socialNetwork = new SocialNetwork("apple", []);
		SocialNetwork.setHandled([])
	}
	
	@AfterClass
	public static void cleanUp(){
		File depth1 = new File("network.depth.1.txt");
		File depth2 = new File("network.depth.2.txt");
		File depth3 = new File("network.depth.3.txt");
		depth1.delete();
		depth2.delete();
		depth3.delete();
	}
	
	@Test
	public void testInstantiation(){
		assert socialNetwork instanceof SocialNetwork
	}
	
	@Test
	public void buildFirstDegree(){
		List firstDegree = socialNetwork.buildFirstDegree()
		assertEquals(6, firstDegree.size())
	}
	
	@Test
	public void buildFirstDegreeHonorsIgnores(){
		socialNetwork = new SocialNetwork("causes", ["caused"]);
		List firstDegree = socialNetwork.buildFirstDegree()
		assertEquals(12, firstDegree.size())
	}
	
	@Test
	public void testBuildNetwork(){
		List wholeNetwork = socialNetwork.buildNetwork(1)
		assertEquals(6, wholeNetwork.size())
	}
	
	@Test
	public void testBuildNetworkDepth(){
		socialNetwork = new SocialNetwork("causes", []);
		List wholeNetwork = socialNetwork.buildNetwork(1)
		assertEquals(13, wholeNetwork.size())
	}
	
	@Test
	public void testBuildNetworkDepth2(){
		socialNetwork = new SocialNetwork("causes", []);
		List wholeNetwork = socialNetwork.buildNetwork(2)
		assertEquals(99, wholeNetwork.size())
	}
	
	@Test
	public void testBuildNetworkDepth3(){
		socialNetwork = new SocialNetwork("causes", []);
		List wholeNetwork = socialNetwork.buildNetwork(3)
		assertEquals(708, wholeNetwork.size())
	}
	
	@Test
	public void testbuildNetworkForDepth(){
		List wholeNetwork = socialNetwork.buildNetworkForDepthParallel(["apple"], 1)
		assertEquals(7, wholeNetwork.size())
	}
	
}
