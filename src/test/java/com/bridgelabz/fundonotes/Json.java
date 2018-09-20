package com.bridgelabz.fundonotes;

public class Json {
	private JsonRequest request;
	private JsonResponse response;

	public JsonRequest getRequest() {
		return request;
	}

	public void setRequest(JsonRequest request) {
		this.request = request;
	}

	public JsonResponse getResponse() {
		return response;
	}

	public void setResponse(JsonResponse response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "Json [request=" + request + ", response=" + response + "]";
	}

}
