package com.db;

import com.j256.ormlite.field.DatabaseField;

public class Levels {

	// Table Fields
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	int besttime;
	@DatabaseField(canBeNull = true)
	int highscore;

	// Required by ORMLite
	Levels() {}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id).append(", ");
		return sb.toString();
	}
}