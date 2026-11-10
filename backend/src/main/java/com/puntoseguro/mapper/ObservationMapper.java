package com.puntoseguro.mapper;
import com.puntoseguro.dto.AuthorResponse; import com.puntoseguro.dto.ObservationResponse; import com.puntoseguro.entity.Observation; import org.springframework.stereotype.Component;
@Component public class ObservationMapper {
 public ObservationResponse toDTO(Observation o) { return new ObservationResponse(o.getId(),o.getTexto(),new AuthorResponse(o.getAuthor().getId(),o.getAuthor().getNombre()),o.getFechaCreacion()); }
}
