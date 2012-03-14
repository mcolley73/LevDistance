package org.mcolley73.levenshtein

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SocialNetwork {

	String suspectZero;
	String breadCrumbs;
	File wordsFile;
	List firstDegree = null;
	List entireNetwork
	List ignoreList
	static Map graph = null
	static List handled = []
	
	FriendFinder finder
	
	SocialNetwork(String word, File file){
		suspectZero = word
		wordsFile = file
		ignoreList = []
		breadCrumbs = ""
		println "ignoreList size --> " + ignoreList.size()
	}
	
	SocialNetwork(String word, List ignores){
		suspectZero = word
		ignoreList = ignores
		finder = new FriendFinder()
	}
	
	SocialNetwork(String word, File file, List ignores, String crumbs){
		suspectZero = word
		wordsFile = file
		ignoreList = ignores
		breadCrumbs = crumbs + " " + word
		println "ignoreList size --> " + ignoreList.size()
	}
	
	static void buildGraph(File file){
		if(graph != null){
			return
		}
		graph = new HashMap();		
		FriendFinder finder = new FriendFinder();
		List friends = []
		file.eachLine { line ->
			println "Build Graph -> " + line
			friends = finder.findFriends(line, file, friends);
			graph.put(line,  friends);
		}
	}
	
	List buildFirstDegree(){
		firstDegree = new FriendFinder().findFriends(suspectZero, ignoreList)
		return firstDegree
	}
	
	List buildNetwork1(){
		println breadCrumbs
		if(firstDegree==null){
			buildFirstDegree();
		}
		ignoreList.addAll(firstDegree);
		this.entireNetwork = new ArrayList()
		this.entireNetwork.addAll(firstDegree)
		SocialNetwork friendNetwork
		List networks = []
		for(String friend : firstDegree){
			friendNetwork = new SocialNetwork(friend, this.wordsFile, ignoreList, breadCrumbs);
			ignoreList = ignoreList.unique();
			ignoreList.addAll(friendNetwork.buildFirstDegree());
			entireNetwork.addAll(friendNetwork.getFirstDegree());
			networks << friendNetwork
		}
		List subNetwork
		for(SocialNetwork network : networks){
			subNetwork = network.buildNetwork();
			ignoreList.addAll(subNetwork);
			ignoreList = ignoreList.unique();
			entireNetwork.addAll(subNetwork);
		}
		return entireNetwork.unique();
	}
	
	List buildNetwork2(){
		println breadCrumbs
		entireNetwork = [];
		
		FriendFinder finder = new FriendFinder();
		List bigger = finder.findFriendsByAddingOne(suspectZero, wordsFile, ignoreList)
		ignoreList.addAll(bigger);
		List netAdd = []
		for(String curWord : bigger){
			buildSubNetwork(curWord);
		}
		entireNetwork.unique(true);
		
		List sameSize = finder.findFriendsBySubstituting(suspectZero, wordsFile, ignoreList)
		ignoreList.addAll(sameSize);
		for(String curWord : sameSize){
			buildSubNetwork(curWord);
		}
		entireNetwork.unique(true);
		
		List smaller = finder.findFriendsByRemoving(suspectZero, wordsFile, ignoreList)
		ignoreList.addAll(smaller);
		for(String curWord : smaller){
			buildSubNetwork(curWord);
		}
		return entireNetwork.unique(true);
	}
	
	void buildSubNetwork(String curWord){
		SocialNetwork network = new SocialNetwork(curWord, wordsFile, ignoreList, breadCrumbs);
		List netAdd = network.buildNetwork();
		ignoreList.addAll(netAdd);
		ignoreList.unique(true);
		entireNetwork.addAll(netAdd);
	}
	
	List buildNetwork3(){
		println breadCrumbs
		entireNetwork = [];
		
		FriendFinder finder = new FriendFinder();
		List myFriends = finder.findFriends(suspectZero, wordsFile, ignoreList);
		SocialNetwork network
		List curList
		for(String friend : myFriends){
			if(!ignoreList.contains(friend)){
				ignoreList.add(friend)				
				network = new SocialNetwork(friend,wordsFile,ignoreList,breadCrumbs)
				curList = network.buildNetwork()
				ignoreList.addAll(curList);
				ignoreList.unique(true);
				entireNetwork.addAll(curList);
			}
		}
		return entireNetwork.unique(true);
	}
	
	List buildNetwork4(){
		println breadCrumbs
		entireNetwork = [];
		
		List myFriends = graph.get(suspectZero);
		ignoreList.addAll(myFriends)
		SocialNetwork network
		List curList
		for(String friend : myFriends){
			if(!ignoreList.contains(friend)){
				network = new SocialNetwork(friend,wordsFile,ignoreList,breadCrumbs)
				curList = network.buildNetwork()
				ignoreList.addAll(curList);
				ignoreList.unique(true);
				entireNetwork.addAll(curList);
			}
		}
		return entireNetwork.unique(true);
	}
	
	List buildNetwork5(List wordsList, List handledList, int curDepth, int targetDepth){
		List result = wordsList
		int originalLength = wordsList.size();
		FriendFinder finder = new FriendFinder();
		println "depth -----------> " + curDepth + " of " + targetDepth
		println "originalLength --> " + originalLength
		if(curDepth <= targetDepth){
			List friends
			for(String word : wordsList.toArray()){
				if(!handledList.contains(word)){
					friends = finder.findFriends(word, wordsFile, wordsList)
					handledList.add(word)
					
					wordsList.addAll(friends)
					wordsList.unique(true)
					wordsList.sort()
				}else{
					println "Handled: " + word
				}
			}
			
			File f = new File("network.depth."+curDepth+".txt")
			if(f.exists()){
				f.delete()
			}
			result.sort()
			result.each { word ->
				f.append(word + "\n")
			}
			
			int finalLength = wordsList.size();
			if(curDepth < targetDepth && finalLength > originalLength){
				buildNetwork(wordsList, handledList, curDepth + 1, targetDepth);
			}
			result = wordsList
		}
		println "originalLength:finalLength --> " + originalLength + ":" + result.size()
		
		return result
	}
	
	List buildNetwork6(int maxDepth){
		List existing = [suspectZero]
		int existingSize = existing.size()
		for(int i in 1..maxDepth){
			
			buildNetworkForDepth(existing, i)
			
			File f = new File("network.depth."+i+".txt")
						
			existing = []
			f.eachLine{ friendWord ->
				existing.add(friendWord)
			}
			
			if(existingSize==existing.size()){
				println "Breaking at depth " + i + " due to lack of change."
				break
			}else{
				existingSize = existing.size()
			}
		}
		return existing
	}
	
	List buildNetworkForDepth6(List existing, int curDepth){
		List result = []
		int originalLength = existing.size();
		println "depth -----------> " + curDepth
		println "originalLength --> " + originalLength
		
		List friends
		
		for(String word : existing.toArray()){
			if(!handled.contains(word)){
				friends = finder.findFriends(word, result)
				handled.add(word)
				
				result.addAll(friends)
				result.unique(true)
				result.sort()							
			}else{
				println "handled --------------------> " + word
			}
			
		}
		
		File f = new File("network.depth."+curDepth+".txt")
		if(f.exists()){
			f.delete()
		}
		result.sort()
		result.each { word ->
			f.append(word + "\n")
		}
		
		int finalLength = result.size();
		
		println "originalLength:finalLength --> " + originalLength + ":" + finalLength
		return result
	}
	
	
	List buildNetwork(int maxDepth){
		List existing = []
		existing.add(suspectZero)
		int existingSize = existing.size()
		for(int i in 1..maxDepth){
			
			//List thisDepth = buildNetworkForDepthSerial(existing, i)			
			List thisDepth = buildNetworkForDepthParallel(existing, i)

			existing.addAll(thisDepth)
			existing.unique(true)
			existing.sort();
			
			if(existingSize==thisDepth.size()){
				println "Breaking at depth " + i + " due to lack of change."
				break
			}else{
				existingSize = existing.size()
			}
		}
		existing.remove(suspectZero)
		return existing
	}
	
	List buildNetworkForDepthSerial(List existing, int curDepth){
		List result = []
		
		int originalLength = existing.size();
		println "depth -----------> " + curDepth
		println "originalLength --> " + originalLength
				
		String[] existingWords = existing.toArray();		
		List[] pieces = breakApart(existingWords);
		result.addAll(existing)
		
		List threads = []
		int ptr = 0
		for(List piece : pieces){
				List newfoundFriends = processPiece(piece, existing)
				result.addAll(newfoundFriends);
		}		
		
		File f = new File("network.depth."+curDepth+".txt")
		if(f.exists()){
			f.delete()
		}
		
		result.sort()
		result.unique(true);
		result.each { word ->
			f.append(word + "\n")
		}
		
		int finalLength = result.size();
		
		println "originalLength:finalLength --> " + originalLength + ":" + finalLength
		return result
	}
	
	List buildNetworkForDepthParallel(List existing, int curDepth){
		final List result = new ArrayList()
		
		int originalLength = existing.size();
		println "depth -----------> " + curDepth
		println "originalLength --> " + originalLength
				
		final String[] existingWords = existing.toArray();
		final List[] pieces = breakApart(existingWords);
		result.addAll(existing)
		
		final List threads = []
		int ptr = 0
		
		ExecutorService pool = Executors.newFixedThreadPool(20)
		def defer = { clos -> pool.submit(clos as Callable) }
		def logic = { piece ->
			List pieceResult = processPiece(piece, existing)
			synchronized (result){
				println " ------------- ADDING " + pieceResult.size() + " to size of " + result.size()
				result.addAll(pieceResult)
			}
		}

		
		List<Future> futures = pieces.collect{ piece ->			
		    defer{
			  	logic(piece)
			}
		}				
		futures.each{ c->
			c.get();
		}
		pool.shutdown();
		
		
		File f = new File("network.depth."+curDepth+".txt")
		if(f.exists()){
			f.delete()
		}
		
		result.sort()
		result.unique(true);
		result.each { word ->
			f.append(word + "\n")
		}
		
		int finalLength = result.size();
		
		println "originalLength:finalLength --> " + originalLength + ":" + finalLength
		return result
	}
	
	public List processPiece(List piece, List existing) {
		println Thread.currentThread().getId() + " processPiece"
		List curFriends
		List pieceFriends = []
		int handledCount = 0
		for(String word : piece.toArray()){
			if(!handled.contains(word)){
				curFriends = finder.findFriends(word, existing)
				handled.add(word)

				pieceFriends.addAll(curFriends)
			}else{
				handledCount++
			}
		}
		pieceFriends.unique(true)
		pieceFriends.sort()
		println Thread.currentThread().getId() + " piece found " + pieceFriends.size() + " friend(s); was " + (handledCount/piece.size()*100) + "% handled"
		return pieceFriends
	}
	
	public List[] breakApart(String[] original){
		List lists = new ArrayList()
		List curList
		int pieceSize = 100
		int listPtr = 0
		for(String s : original){
			if(listPtr++ % pieceSize==0){
				curList = []
				lists.add(curList)
			}
			curList.add(s) 
		}		
		
		def totalSize = 0
		for(List piece : lists){
			totalSize += piece.size();
		}
		println "breakApart --------> " + lists.size() + " piece(s) with total size of : " + totalSize
		return lists.toArray()
	}
	
}
