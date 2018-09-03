package com.rsi.devjam.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyProfile {
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("real_name")
	private String realName;
	private String email;
	private String skype;
	private String phone;
	private String image24;
	private String image32;
	private String image48;
	private String image72;
	private String image192;
	private String image512;
	private String team;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage24() {
		return image24;
	}

	public void setImage24(String image24) {
		this.image24 = image24;
	}

	public String getImage32() {
		return image32;
	}

	public void setImage32(String image32) {
		this.image32 = image32;
	}

	public String getImage48() {
		return image48;
	}

	public void setImage48(String image48) {
		this.image48 = image48;
	}

	public String getImage72() {
		return image72;
	}

	public void setImage72(String image72) {
		this.image72 = image72;
	}

	public String getImage192() {
		return image192;
	}

	public void setImage192(String image192) {
		this.image192 = image192;
	}

	public String getImage512() {
		return image512;
	}

	public void setImage512(String image512) {
		this.image512 = image512;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

}
