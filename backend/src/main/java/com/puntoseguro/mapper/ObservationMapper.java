package com.puntoseguro.mapper;
import com.puntoseguro.dto.AuthorResponse; import com.puntoseguro.dto.ObservationHistoryResponse; import com.puntoseguro.dto.ObservationResponse; import com.puntoseguro.entity.Observation; import com.puntoseguro.entity.ObservationHistory; import org.springframework.stereotype.Component;
@Component public class ObservationMapper {
 public ObservationResponse toDTO(Observation o) { return new ObservationResponse(o.getId(),o.getTexto(),new AuthorResponse(o.getAuthor().getId(),o.getAuthor().getNombre()),o.getFechaCreacion()); }
 public ObservationHistoryResponse historyToDTO(ObservationHistory h) { return new ObservationHistoryResponse(h.getId(),h.getTexto(),new AuthorResponse(h.getEditor().getId(),h.getEditor().getNombre()),h.getFechaEdicion(),h.isImportado()); }
}
