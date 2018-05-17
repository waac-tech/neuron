package world.waac.neuron.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class SearchResponseDTO implements Serializable {

    private List<String> filePaths;

    @Override
    public String toString() {
        String stringRep = (new Gson()).toJson(this);
        return stringRep;
    }


    public static SearchResponseDTO fromJSON(String jsonRep) {
        Gson gson = new Gson();
        SearchResponseDTO searchResponseDTO = gson.fromJson(jsonRep, SearchResponseDTO.class);
        return searchResponseDTO;
    }

    public SearchResponseDTO(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }
}
