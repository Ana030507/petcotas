package co.edu.usco.petcotas.dto;

public class PetImageDto {
    private Long id;
    private String url;

    public PetImageDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
