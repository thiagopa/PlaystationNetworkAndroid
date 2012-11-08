package br.com.thiagopagonha.psnapi.model;

import java.util.Date;


public class Friend {
	private String psnId;
	private String playing;
	private String avatarSmall;
	private Date updated;
	
	public String getPsnId() {
		return psnId;
	}
	public void setPsnId(String psnId) {
		this.psnId = psnId;
	}
	public String getPlaying() {
		return playing;
	}
	public void setPlaying(String playing) {
		this.playing = playing;
	}
	public String getAvatarSmall() {
		return avatarSmall;
	}
	public void setAvatarSmall(String avatarSmall) {
		this.avatarSmall = avatarSmall;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
