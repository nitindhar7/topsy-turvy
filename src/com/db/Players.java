package com.db;

import com.j256.ormlite.field.DatabaseField;

public class Players {
	
	// Table Fields
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = false)
	String name;
	@DatabaseField(canBeNull = true)
	int sp_topscore;
	@DatabaseField(canBeNull = false)
	int sp_totalscore;
	@DatabaseField(canBeNull = false)
	int sp_gamesplayed;
	@DatabaseField(canBeNull = true)
	int mp_topscore;
	@DatabaseField(canBeNull = false)
	int mp_totalscore;
	@DatabaseField(canBeNull = false)
	int mp_gamesplayed;
	@DatabaseField(canBeNull = false)
	int sound;
	@DatabaseField(canBeNull = false)
	int vibration;

	// Required by ORMLite
	Players() {}

	public Players(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id).append(", ");
		sb.append("name=").append(name);
		return sb.toString();
	}
}