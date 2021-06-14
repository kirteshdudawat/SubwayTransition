package com.kirtesh.subwaytransition.response;

/**
 * Response of getPath API
 */
public class SearchAvailablePathResponse {
    private String response;

    public SearchAvailablePathResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SearchAvailablePathResponse{");
        sb.append("response='").append(response).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
