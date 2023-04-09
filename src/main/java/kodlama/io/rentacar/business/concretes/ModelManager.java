package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.ModelService;
import kodlama.io.rentacar.business.dto.requests.create.CreateModelRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateModelRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateModelResponse;
import kodlama.io.rentacar.business.dto.responses.get.model.GetAllModelsResponse;
import kodlama.io.rentacar.business.dto.responses.get.model.GetModelResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateModelResponse;
import kodlama.io.rentacar.entities.Model;
import kodlama.io.rentacar.repository.ModelRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModelManager implements ModelService {
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GetAllModelsResponse> getAll() {
        var response = modelRepository.findAll()
                .stream()
                .map(model -> modelMapper.map(model, GetAllModelsResponse.class))
                .toList();
        return response;
    }

    @Override
    public GetModelResponse geyById(int id) {
        checkIfModelExists(id);
        GetModelResponse response = modelMapper.map(modelRepository.findById(id).orElseThrow(), GetModelResponse.class);
        return response;
    }

    @Override
    public CreateModelResponse add(CreateModelRequest request) {
        Model model = modelMapper.map(request, Model.class);
        model.setId(0);
        modelRepository.save(model);
        CreateModelResponse response = modelMapper.map(model, CreateModelResponse.class);
        return response;
    }

    @Override
    public UpdateModelResponse update(int id, UpdateModelRequest request) {
        checkIfModelExists(id);
        Model model = modelMapper.map(request, Model.class);
        model.setId(id);
        modelRepository.save(model);
        UpdateModelResponse response = modelMapper.map(model, UpdateModelResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        checkIfModelExists(id);
        modelRepository.deleteById(id);
    }

    private void checkIfModelExists(int id) {
        if (!modelRepository.existsById(id))
            throw new RuntimeException("Model doesn't exist.");
    }
}
