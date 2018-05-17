package world.waac.neuron.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class FileRequestDTO implements Serializable {
    private String filePath;
    private String fromIp;


    @Override
    public String toString() {
        String stringRep = (new Gson()).toJson(this);
        return stringRep;
    }

    public static FileRequestDTO fromJSON(String jsonRep) {
        Gson gson = new Gson();
        FileRequestDTO fileRequestDTO = gson.fromJson(jsonRep, FileRequestDTO.class);
        return fileRequestDTO;
    }

    public FileRequestDTO(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }
}
