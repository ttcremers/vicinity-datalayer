package com.theodoorthomas.vicinity.data.model;

import org.joda.time.DateTime;

import android.content.Context;

import com.theodoorthomas.vicinity.data.DataLayer;
import com.theodoorthomas.vicinity.data.DataLayerError;
import com.theodoorthomas.vicinity.data.Model;
import com.theodoorthomas.vicinity.data.anotations.Column;
import com.theodoorthomas.vicinity.data.anotations.Table;

@Table(name="notifications")
public class VNotification extends DataLayer implements Model {
	
	@Column(type="KEY")
	private Integer id;
	@Column(type="INTEGER")
	private Integer uid;
	@Column(type="INTEGER")
	private Integer distance;
	@Column(type="DateTime")
	private DateTime timestamp;
	@Column(type="Boolean")
	private Boolean isNotified;
	
	public VNotification(Context context) throws DataLayerError {
		super(context);
	}
	
	public VNotification(Context context, Integer uid, Integer distance,
			long timestamp) throws DataLayerError {
		super(context);
		this.uid = uid;
		this.distance = distance;
		this.timestamp = new DateTime(timestamp);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp; 
	}

	@Override
	public Model getResult() { 
		return this;
	}

	public Boolean getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(Boolean isNotified) {
		this.isNotified = isNotified;
	}
}
