package net.ddns.minersonline.HistorySurvival.api.data.text;

public class JSONHoverEvent {
	private String action;
	private String value;

	public JSONHoverEvent(String action, String value) {
		this.action = action;
		this.value = value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
