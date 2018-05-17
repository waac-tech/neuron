package world.waac.neuron.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class SearchRequestDTO implements Serializable {

    private String keyword;

    private String fromIP;
    private int port;

    @Override
    public String toString() {
        String stringRep = (new Gson()).toJson(this);
        return stringRep;
    }

    public static SearchRequestDTO fromJSON(String jsonRep) {
        Gson gson = new Gson();
        SearchRequestDTO searchRequestDTO = gson.fromJson(jsonRep, SearchRequestDTO.class);
        return searchRequestDTO;
    }

    public SearchRequestDTO(String keyword, String fromIP, int port) {
        this.keyword = keyword;
        this.fromIP = fromIP;
        this.port = port;
    }

    public String getFromIP() {
        return fromIP;
    }

    public int getPort() {
        return port;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setFromIP(String fromIP) {
        this.fromIP = fromIP;
    }
}
