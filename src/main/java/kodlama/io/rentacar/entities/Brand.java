package kodlama.io.rentacar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "markalar")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // @Column(name="ad")
    private String name;
    @OneToMany(mappedBy = "brand")
    //@JsonBackReference
    private List<Model> models;

}
