package co.edu.usco.petcotas.mapper;

import co.edu.usco.petcotas.dto.PetCreateDto;
import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.dto.PetUpdateDto;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.PetImage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public final class PetMapper {

    private PetMapper() {}

    public static PetSummaryDto toSummary(Pet p) {
        String statusName = (p.getStatus() == null) ? null : p.getStatus().getName();

        return new PetSummaryDto(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getSize(),
                p.getAge(),
                p.getMainImage(),
                p.getShortDescription(),
                statusName
        );
    }

    public static PetDetailDto toDetail(Pet p) {
        List<String> imgs = (p.getImages() == null)
                ? Collections.emptyList()
                : p.getImages().stream()
                    .map(PetImage::getUrl)
                    .collect(Collectors.toList());

        String statusName = (p.getStatus() == null) ? null : p.getStatus().getName();

        return new PetDetailDto(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getSize(),
                p.getAge(),
                statusName,
                p.getShortDescription(),
                p.getFullDescription(),
                p.getMainImage(),
                imgs
        );
    }

    /* ----------------- New helpers ----------------- */

    /**
     * Crea una entidad Pet a partir de un PetCreateDto.
     * NOTA: no resuelve el Status aquí (el servicio debe asignarlo).
     */
    public static Pet fromCreateDto(PetCreateDto dto) {
        Pet p = new Pet();
        p.setName(dto.getName());
        p.setType(dto.getType());
        p.setSize(dto.getSize());
        p.setAge(dto.getAge());
        p.setShortDescription(dto.getShortDescription());
        p.setFullDescription(dto.getFullDescription());
        p.setMainImage(dto.getMainImage());
        // images, status y adoptedBy se gestionan desde el servicio/otros endpoints
        return p;
    }

    /**
     * Actualiza la entidad Pet existente con los campos no nulos de PetUpdateDto.
     * No resuelve ni asigna Status (el servicio debe hacerlo si es necesario).
     */
    public static void updateFromDto(Pet pet, PetUpdateDto dto) {
        if (dto == null || pet == null) return;

        if (dto.getName() != null) pet.setName(dto.getName());
        if (dto.getType() != null) pet.setType(dto.getType());
        if (dto.getSize() != null) pet.setSize(dto.getSize());
        if (dto.getAge() != null) pet.setAge(dto.getAge());
        if (dto.getShortDescription() != null) pet.setShortDescription(dto.getShortDescription());
        if (dto.getFullDescription() != null) pet.setFullDescription(dto.getFullDescription());
        if (dto.getMainImage() != null) pet.setMainImage(dto.getMainImage());
        // status se maneja en el servicio (porque necesita Status entity)
    }
}
