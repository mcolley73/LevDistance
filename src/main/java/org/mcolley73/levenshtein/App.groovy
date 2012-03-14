package org.mcolley73.levenshtein

/**
 * In response to a challenge proposed by a Causes.com job posting.
 * 
 * 
 * Two words are friends if they have a Levenshtein distance 
 * (http://en.wikipedia.org/wiki/Levenshtein_distance) of 1. That 
 * is, you can add, remove, or substitute exactly one letter 
 * in word X to create word Y. 
 * 
 * A word�s social network  consists of all of its friends, plus 
 * all of their friends, and all of their friends� friends, and
 * so on. 
 * 
 * Write a program to tell us how big the social network for the 
 * word �causes� is.
 * 
 * 
 * 
 * Seems to me this will end up encompassing every word in the list no matter what word we
 * 	start with unless you start with a word that has 0 or 1 friends... but...
 * 
 * Is it just me or did I totally underestimate how much parallelism this was going to take? Doing 
 * this serially is taking... so... long.
 * 
 * Working to switch everything to breadth first to avoid crazy stack overflow...
 * 
 * Working to eek out better performance, focusing on the first 3 degrees...
 * 
 * Working to switch to parallel processing...
 * 
 * Watching this play out once the parallel was working, its interesting to see how the 
 * degrees of separation end up following a curve in terms of the number
 * of friends added. Early on, each degree adds a ton of friends, with the 3rd degree
 * quadrupling and the 4th degree tripling the size of the network. But from then on,
 * the number of friends added (the % increase in network size for a given depth increase of 1) 
 * steadily to decrease towards with rare exception -- everybody knows everybody.
 * 
 * @author Mark
 */
class App {

	public static void main(String[] args){
		Date startTime = new Date()
		File wordsList = new App().openWordsList()
		
		int lineCount = 0
		wordsList.eachLine{ lineCount++ }
		
		SocialNetwork causesNetwork = new SocialNetwork("causes", [])
		List wholeNetwork = causesNetwork.buildNetwork(100)
		
		Date finishTime = new Date();
		
		println "Start time:                      " + startTime
		println "Finish time:                     " + finishTime
		println "Total words in file:             " + lineCount
		println "Total words in 'causes' network: " + wholeNetwork.size()
	}
		
	File openWordsList(){
		return new File("word.list.txt");
	}
	
}
