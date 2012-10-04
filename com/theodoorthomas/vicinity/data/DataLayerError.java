package com.theodoorthomas.vicinity.data;

public class DataLayerError extends Exception {

	public DataLayerError() {
	}

	public DataLayerError(String detailMessage) {
		super(detailMessage);
	}

	public DataLayerError(Throwable throwable) {
		super(throwable);
	}

	public DataLayerError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
