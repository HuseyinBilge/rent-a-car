package kodlama.io.rentacar.api.controllers;

import kodlama.io.rentacar.business.abstracts.ModelService;
import kodlama.io.rentacar.business.dto.requests.create.CreateModelRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateModelRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateModelResponse;
import kodlama.io.rentacar.business.dto.responses.get.model.GetAllModelsResponse;
import kodlama.io.rentacar.business.dto.responses.get.model.GetModelResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateModelResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/models")
public class ModelsController {
    private final ModelService modelService;

    @GetMapping
    public List<GetAllModelsResponse> getAll() {
        return modelService.getAll();
    }

    @GetMapping("{id}")
    public GetModelResponse getById(@PathVariable int id) {
        return modelService.geyById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateModelResponse add(@RequestBody CreateModelRequest createModelRequest) {
        return modelService.add(createModelRequest);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        modelService.delete(id);
    }

    @PutMapping("{id}")
    public UpdateModelResponse update(@PathVariable int id, @RequestBody UpdateModelRequest request) {
        return modelService.update(id, request);
    }
}
