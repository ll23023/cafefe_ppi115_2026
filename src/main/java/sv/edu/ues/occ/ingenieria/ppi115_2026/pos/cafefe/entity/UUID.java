package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @author mil
 */
@Converter(autoApply = true)
public class UUID implements AttributeConverter<java.util.UUID, java.util.UUID> {
    
    @Override
    public java.util.UUID convertToDatabaseColumn(java.util.UUID attribute) {
        return attribute;
    }
    
    @Override
    public java.util.UUID convertToEntityAttribute(java.util.UUID dbData) {
        return dbData;
    }

    public UUID() {
    }
    
}