package kodlama.io.rentacar.business.concretes;

import kodlama.io.rentacar.business.abstracts.BrandService;
import kodlama.io.rentacar.business.dto.requests.create.CreateBrandRequest;
import kodlama.io.rentacar.business.dto.requests.update.UpdateBrandRequest;
import kodlama.io.rentacar.business.dto.responses.create.CreateBrandResponse;
import kodlama.io.rentacar.business.dto.responses.get.brand.GetAllBrandsResponse;
import kodlama.io.rentacar.business.dto.responses.get.brand.GetBrandResponse;
import kodlama.io.rentacar.business.dto.responses.update.UpdateBrandResponse;
import kodlama.io.rentacar.business.rules.BrandBusinessRules;
import kodlama.io.rentacar.entities.Brand;
import kodlama.io.rentacar.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandManager implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final BrandBusinessRules rules;

    @Override
    public List<GetAllBrandsResponse> getAll() {
        List<Brand> brands = brandRepository.findAll();
        List<GetAllBrandsResponse> response = brands
                .stream()
                .map(brand -> modelMapper.map(brand, GetAllBrandsResponse.class))
                .toList();
        return response;
    }

    @Override
    public GetBrandResponse geyById(int id) {
        rules.checkIfBrandExists(id);
        Brand brand = brandRepository.findById(id).orElseThrow();
        GetBrandResponse response = modelMapper.map(brand, GetBrandResponse.class);
        return response;

    }

    @Override
    public CreateBrandResponse add(CreateBrandRequest request) {
      /*  // manuel mapping
        Brand brand = new Brand();
        brand.setName(request.getName());
        brandRepository.save(brand);

        CreateBrandResponse response = new CreateBrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());*/
        Brand brand = modelMapper.map(request, Brand.class);
        brand.setId(0);
        brandRepository.save(brand);
        CreateBrandResponse response = modelMapper.map(brand, CreateBrandResponse.class);
        return response;
    }

    @Override
    public UpdateBrandResponse update(int id, UpdateBrandRequest request) {
        rules.checkIfBrandExists(id);
        Brand brand = modelMapper.map(request, Brand.class);
        brand.setId(id);
        brandRepository.save(brand);
        UpdateBrandResponse response = modelMapper.map(brand, UpdateBrandResponse.class);
        return response;
    }

    @Override
    public void delete(int id) {
        rules.checkIfBrandExists(id);
        brandRepository.deleteById(id);
    }


}
