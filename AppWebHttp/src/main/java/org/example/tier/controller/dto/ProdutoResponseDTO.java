package org.example.tier.controller.dto;

public class ProdutoResponseDTO {

    private int status = 200;
    private String reasonPhrase = "OK";
    private String responseBody;

    public ProdutoResponseDTO() {
    }

    public ProdutoResponseDTO(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
