package com.db;

import com.j256.ormlite.field.DatabaseField;

public class Sessions {
	
	// Table Fields
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = false, foreign = true)
	Players player;

	// Required by ORMLite
	Sessions() {}

	public Sessions(Players player) {
		this.player = player;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id).append(", ");
		sb.append("player_id=").append(player);
		return sb.toString();
	}
}