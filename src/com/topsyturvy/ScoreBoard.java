package com.topsyturvy;

public class ScoreBoard {
	private String name;
	private int topScore;

	public ScoreBoard(String name, int topScore) {
		this.name = name;
		this.topScore = topScore;
	}

	public String getName() {
		return name;
	}
	public int getTopScore() {
		return topScore;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTopScore(int topScore) {
		this.topScore = topScore;
	}
}