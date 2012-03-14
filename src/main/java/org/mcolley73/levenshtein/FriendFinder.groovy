package org.mcolley73.levenshtein

import java.util.regex.Pattern

/**
 * A word is a friend if you can add, remove, or substitute 
 * exactly one letter in word X to create word Y. 
 * @author Mark
 */
class FriendFinder {
	
	File wordsFile
	List wordsList = []
	
	FriendFinder(){
		println "Building new FriendFinder..."
		wordsFile = new File("word.list.txt")
		wordsFile.eachLine { word ->
			wordsList.add(word);
		}
		println "...FriendFinder is ready!"
	}
	
	List findFriends(String word, List ignoreList){
		def friends = []
		def addOnePattern = ~buildAddOneRegex(word)
		def subOrRemovePattern = ~buildSubstitutionRegex(word)
		def wordLength = word.length();
		
		//friends.addAll(findFriendsByAnyMeans(word,ignoreList))
		friends.addAll(findFriendsByAddingOne(word,ignoreList))
		friends.addAll(findFriendsBySubstitutingOrRemoving(word, ignoreList))
		//friends.removeAll(ignoreList)
		//friends.addAll(findFriendsByRemoving(word, ignoreList))
		return friends.unique();
	}
	
	List findFriendsByAnyMeans(String word, List ignoreList){
		def friends = [];
				
		def wordLength = word.length();
		wordsList.each { String line ->
			if(line.length()==wordLength + 1){
				def patternString = buildAddOneRegex(word)
				def patternAddOne = ~patternString
				if(patternAddOne.matcher(line).matches()){
					println "add one: " + word + " --> " + line;
					friends << line;
				}
			}else if(line.length()==wordLength || line.length()==wordLength - 1){
				def patternString = buildSubstitutionOrRemovalRegex(word)
				def patternSubOne = ~patternString
				if(patternSubOne.matcher(line).matches() && line != word){
					println "sub one: " + word + " --> " + line;
					friends << line;
				}
			}
		}
		friends.removeAll(ignoreList);
		return friends;
	}
	
	List findFriendsByAddingOne(String word, List ignoreList){
		def friends = [];
		def patternString = buildAddOneRegex(word)
		def patternAddOne = ~patternString
		
		def wordLength = word.length();
		wordsList.each { String line ->
			if(line.length()==wordLength + 1){
				if(patternAddOne.matcher(line).matches()){
					println Thread.currentThread().id + " add one: " + word + " --> " + line;
					friends << line;
				}
			}
		}
		friends.removeAll(ignoreList);
		return friends;
	}
	
	List findFriendsBySubstituting(String word, List ignoreList){
		def friends = [];
		def patternString = buildSubstitutionRegex(word)
		def patternSubOne = ~patternString
		
		def wordLength = word.length();
		wordsList.each { String line ->
			if(line.length()==wordLength){
				if(patternSubOne.matcher(line).matches() && line != word  && !ignoreList.contains(line)){
					println "sub one: " + word + " --> " + line;
					friends << line;
				}
			}
		}
		return friends;
	}
	
	List findFriendsBySubstitutingOrRemoving(String word, List ignoreList){
		def friends = [];
		def patternString = buildSubstitutionOrRemovalRegex(word)
		def patternSubOne = ~patternString
		
		def wordLength = word.length();
		wordsList.each { String line ->
			if(line.length()==wordLength || line.length()==wordLength - 1){
				if(patternSubOne.matcher(line).matches() && line != word){
					println Thread.currentThread().id + " sub one: " + word + " --> " + line;
					friends << line;
				}
			}
		}
		friends.removeAll(ignoreList);
		return friends;
	}
	
	List findFriendsByRemoving(String word, List ignoreList){
		def friends = [];
		def patternString = buildRemovalRegex(word)
		def patternAddOne = ~patternString
		
		def wordLength = word.length();
		wordsList.each { String line ->
			if(line.length()==wordLength - 1){
				if(patternAddOne.matcher(line).matches() && !ignoreList.contains(line)){
					println "rem one: " + word + " --> " + line;
					friends << line;
				}
			}
		}
		return friends;
	}
	
	String buildAddOneRegex(String word){
		return "(([A-Za-z]" + word + ")|(" + word + "[A-Za-z]))"
	}
		
	String buildRemovalRegex(String word){
		def regex = "("
		for(i in 0..word.size()-1){
			if(i > 0){
				regex += "|";
			}
			regex += "("
			if(i==0){
				regex += word.substring(1);
			}else if(i==(word.size()-1)){
				regex += word.substring(0, i);
			}else{
				regex += word.substring(0, i) + word.substring(i+1);
			}
			regex += ")"
		}
		regex += ")"
		return regex
	}
	
	String buildSubstitutionRegex(String word){
		def regex = "("
		for(i in 0..word.size()-1){
			if(i > 0){
				regex += "|";
			}
			regex += "("
			if(i==0){
				regex += "[A-Za-z]" + word.substring(1);
			}else if(i==(word.size()-1)){
				regex += word.substring(0, i) + "[A-Za-z]";
			}else{
				regex += word.substring(0, i) + "[A-Za-z]" + word.substring(i+1);
			}
			regex += ")"
		}
		regex += ")"
		return regex
	}
	
	String buildSubstitutionOrRemovalRegex(String word){
		def regex = "("
		for(i in 0..word.size()-1){
			if(i > 0){
				regex += "|";
			}
			regex += "("
			if(i==0){
				regex += "[A-Za-z]?" + word.substring(1);
			}else if(i==(word.size()-1)){
				regex += word.substring(0, i) + "[A-Za-z]?";
			}else{
				regex += word.substring(0, i) + "[A-Za-z]?" + word.substring(i+1);
			}
			regex += ")"
		}
		regex += ")"
		return regex
	}
		
}
