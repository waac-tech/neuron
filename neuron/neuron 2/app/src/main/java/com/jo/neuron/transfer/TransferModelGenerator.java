package com.jo.neuron.transfer;

import com.jo.neuron.models.DeviceDTO;
import com.jo.neuron.models.FileRequestDTO;
import com.jo.neuron.models.SearchRequestDTO;
import com.jo.neuron.models.SearchResponseDTO;

public class TransferModelGenerator {

    public static ITransferable generateDeviceTransferModelRequest(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA, TransferConstants.TYPE_REQUEST,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateDeviceTransferModelResponse(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA, TransferConstants.TYPE_RESPONSE,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateDeviceTransferModelRequestWD(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA_WD, TransferConstants.TYPE_REQUEST,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateSearchRequestModel(SearchRequestDTO searchRequestDTO) {
        TransferModel transferModel = new TransferModel(TransferConstants.SEARCH_REQUEST,
                TransferConstants.TYPE_REQUEST,
                searchRequestDTO.toString());
        return transferModel;
    }

    public static ITransferable generateSearchResponseModel(SearchResponseDTO searchResponseDTO) {
        TransferModel transferModel = new TransferModel(TransferConstants.SEARCH_RESPONSE,
                TransferConstants.TYPE_RESPONSE,
                searchResponseDTO.toString());
        return transferModel;
    }

    public static ITransferable generateFileRequestModel(FileRequestDTO fileRequestDTO) {
        TransferModel transferModel = new TransferModel(TransferConstants.FILE_REQUEST,
                TransferConstants.TYPE_REQUEST,
                fileRequestDTO.toString());
        return transferModel;
    }

    static class TransferModel implements ITransferable {

        int reqCode;
        String reqType;
        String data;

        TransferModel(int reqCode, String reqType, String data) {
            this.reqCode = reqCode;
            this.reqType = reqType;
            this.data = data;
        }

        @Override
        public int getRequestCode() {
            return reqCode;
        }

        @Override
        public String getRequestType() {
            return reqType;
        }

        @Override
        public String getData() {
            return data;
        }
    }
}
