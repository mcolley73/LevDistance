package org.mcolley73.levenshtein

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

class FriendFinderTest {
	
	FriendFinder friendFinder;
	
	@Before
	public void setUp() throws Exception {
		friendFinder = new FriendFinder()
	}
	
	@Test
	public void findFriends(){
		def friends = friendFinder.findFriends("apple", []);
		assertEquals(6, friends.size())
	}
	
	@Test
	public void findFriendsIgnoreList(){
		def friends = friendFinder.findFriends("apple", ["sapple"]);
		assertEquals(5, friends.size())
	}
	
	@Test
	public void testFindFriendsByWayOfAddingOne(){
		def friendsByAddingOne = friendFinder.findFriendsByAddingOne("apple", []);
		assertEquals(4, friendsByAddingOne.size())
	}
	
	@Test
	public void testFindFriendsByWayOfSubstitutingOne(){
		def friendsByAddingOne = friendFinder.findFriendsBySubstituting("apple", []);
		assertEquals(2, friendsByAddingOne.size())
	}
	
	@Test
	public void testFindFriendsByWayOfSubstitutingOrRemovingOne(){
		def friendsByAddingOne = friendFinder.findFriendsBySubstitutingOrRemoving("causes", []);
		assertEquals(13, friendsByAddingOne.size())
	}
	
	@Test
	public void testFindFriendsByWayOfRemovingOne(){
		def friendsByAddingOne = friendFinder.findFriendsByRemoving("causes", []);
		assertEquals(2, friendsByAddingOne.size())
	}
	
	@Test
	public void testBuildSubstitutionRegex(){
		def subRegex = friendFinder.buildSubstitutionRegex("top")
		assertEquals("(([A-Za-z]op)|(t[A-Za-z]p)|(to[A-Za-z]))", subRegex);
	}
	
}